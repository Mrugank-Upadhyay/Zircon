package com.editor.application

import com.editor.application.api.ApiFunctions
import com.editor.application.api.ShareData
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.kordamp.ikonli.javafx.FontIcon
import java.util.function.Predicate
import com.editor.application.documents.EditorPane
import com.editor.application.AlertDialog


// Ensure to keep in sync with backend
enum class Permissions(val level: Int) {
    OWNER(0),
    READ_WRITE(1),
    READ_ONLY(2),
}


data class SharedPerson (
    val email: String,
    val permission: Permissions,
)

class ShareWindow(editorPane: EditorPane) : VBox() {
    // TODO Change the getParentTab.text to be more robust to actually get the server side filename)
    private lateinit var filename: String

    private var sharedPeople: ScrollPane = ScrollPane().apply {
        content = VBox().apply {
            isFitToWidth = true
        }
        isFitToHeight = true
        isFitToWidth = true
    }
    private var shareList: ObservableList<SharedPerson> = FXCollections.observableArrayList<SharedPerson?>()
    private val notePermission = editorPane.note?.serverFile?.let { integerToPermission(it.permission) }

    init {
        shareList.apply {
            addListener(ListChangeListener<SharedPerson> { personChange ->
                while (personChange.next()) {
                    if (personChange.wasAdded()) {
                        personChange.addedSubList.forEach { person ->
                        // For now, we don't have in place editing for the person
                            val personEmail = Label(person.email).apply {
                                maxWidth = Double.MAX_VALUE
                            }
                            val personPermissions = Label(person.permission.name).apply {
                                maxWidth = Double.MAX_VALUE
                            }
                            val deletePerson = Button().apply {
                                graphic = FontIcon("fas-trash")
                                onAction = EventHandler {
                                    val status = ApiFunctions.unshareNote(filename, person.email)
                                    if (status.contains("ERROR")) {
                                        val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(status)!!.destructured
                                        AlertDialog(message, title)
                                        return@EventHandler
                                    }
                                    shareList.remove(person)
                                }
                                // TODO TEST THIS TO ENSURE THE BUTTON IS DISABLED FOR READ_ONLY OR LOWER ACCESS

                                println("Disable button for read only users: This user has permission lvl: $notePermission")
                                isDisable = ((notePermission == Permissions.READ_ONLY || person.permission == Permissions.OWNER) && person.email != ApiFunctions.email)
                            }

                            val setRightBox = HBox(personPermissions, deletePerson).apply {
                                spacing = 10.0
                                alignment = Pos.CENTER
                            }
                            (sharedPeople.content as VBox).children.add(
                                HBox(
                                    personEmail,
                                    setRightBox
                                ).apply {
                                    setHgrow(personEmail, Priority.ALWAYS)
                                    setHgrow(setRightBox, Priority.NEVER)
                                    setMargin(this, Insets(5.0, 0.0, 5.0, 0.0))
                                    alignment = Pos.CENTER
                                }
                            )
                        }
                    } else if (personChange.wasRemoved()) {
                        for (person: SharedPerson in personChange.removed) {
                            println(person.toString())
                            (sharedPeople.content as VBox).children.removeIf(Predicate {personBox -> ((personBox as HBox).children[0] as Label).text == person.email})
                        }
                    }
                }
            })
        }
        
        val emailField: TextField = TextField().apply {
            setMargin(this, Insets(0.0, 10.0, 5.0, 10.0))
            promptText = "markdowney@example.com"
        }
        val permissionList: ObservableList<String> = FXCollections.observableArrayList(
            Permissions.READ_WRITE.name,
            Permissions.READ_ONLY.name
        )
        val permissionDropDown: ComboBox<String> = ComboBox<String>(permissionList).apply {
            promptText = "Select Permission"
        }
        val addBtn: Button = Button().apply {
            graphic = FontIcon("fas-plus")
            isDisable = (notePermission == Permissions.READ_ONLY)
            onAction = EventHandler {
                if (emailField.text == "" && permissionDropDown.value == null) {
                    AlertDialog("No Email And Permission Value Provided")
                    return@EventHandler
                }
                else if (emailField.text == "") {
                    AlertDialog("No Email Provided")
                    return@EventHandler
                }
                else if (permissionDropDown.value == null) {
                    AlertDialog("No Permission Value Provided")
                    return@EventHandler
                }

                val permission: Permissions = Permissions.valueOf(permissionDropDown.value)
                println("Permission selected = $permission of level ${permission.level}")


                // Add api call first, and ensure it succeeded before adding to ShareList
                // Ensure no duplicate emails
                val status = ApiFunctions.shareNote(filename, emailField.text, permission.level)

                if (status.contains("ERROR")) {
                    val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(status)!!.destructured
                    AlertDialog(message, title)
                    return@EventHandler
                }

                val sharedPerson: SharedPerson = SharedPerson(emailField.text, permission)

                shareList.add(sharedPerson)

                emailField.text = ""
            }
        }

        val shareBox: HBox = HBox(emailField, permissionDropDown, addBtn).apply {
            setHgrow(emailField, Priority.SOMETIMES)
            setHgrow(permissionDropDown, Priority.SOMETIMES)
            setHgrow(addBtn, Priority.SOMETIMES)
        }


        this.apply {
            padding = Insets(10.0, 10.0, 10.0, 10.0)
            children.addAll(shareBox, sharedPeople)
        }
    }

    // Populate from Database
    init {
        if (ApiFunctions.userToken != "") {
            filename = editorPane.note!!.name
            val serverShareList = ApiFunctions.getShareList(filename)
            println("serverShareList = ${serverShareList}")
            serverShareList.forEach { share:ShareData ->
                val (_, shareEmail, permission) = share
                val person: SharedPerson = SharedPerson(shareEmail, integerToPermission(permission))
                shareList.add(person)
            }
        }
    }

    private fun integerToPermission(integer: Int): Permissions {
        return when (integer) {
            0 -> Permissions.OWNER
            1 -> Permissions.READ_WRITE
            2 -> Permissions.READ_ONLY
            else -> {
                throw Exception("Unknown Permission")
            }
        }
    }

}