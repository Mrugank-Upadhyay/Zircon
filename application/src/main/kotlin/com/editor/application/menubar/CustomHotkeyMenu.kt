package com.editor.application.menubar

import com.editor.application.Main
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import org.kordamp.ikonli.javafx.FontIcon

class CustomHotkeyMenu: Menu() {

    private val customHotkeys = CustomHotkeyFunctions()

    companion object {
        var saveButtonInitial = Button("Ctrl")
        var saveButtonKey = Button("S")
        var saveButtonOptional = Button("")

        var openButtonInitial = Button("Ctrl")
        var openButtonKey = Button("O")
        var openButtonOptional = Button("")

        var newTabButtonInitial = Button("Ctrl")
        var newTabButtonKey = Button("T")
        var newTabButtonOptional = Button("")

        var closeTabButtonInitial = Button("Ctrl")
        var closeTabButtonKey = Button("W")
        var closeTabButtonOptional = Button("")

        var boldButtonInitial = Button("Ctrl")
        var boldButtonKey = Button("B")
        var boldButtonOptional = Button("")

        var italicButtonInitial = Button("Ctrl")
        var italicButtonKey = Button("I")
        var italicButtonOptional = Button("")

        var headingButtonInitial = Button("Ctrl")
        var headingButtonKey = Button("H")
        var headingButtonOptional = Button("")

        var resetButtonKey = Button("Reset")
    }

    private fun showKeyboardShortcutsUI() {
        val dialog = Dialog<String>()
        dialog.title = "Edit Keyboard Shortcuts"
        dialog.headerText = "Configure your custom keyboard shortcuts"
        dialog.graphic = FontIcon("fas-keyboard")

        val saveButton = ButtonType("Done", ButtonBar.ButtonData.OK_DONE)
        dialog.dialogPane.buttonTypes.addAll(saveButton)

        val content = VBox(15.0)

        fun createButtonHBox(text: String, buttonInitial: Button, buttonKey: Button, buttonOptional: Button): HBox {
            val contentHBox = HBox(Text(text).apply { font = Font.font(null, FontWeight.BOLD, 14.0) }).apply { alignment = Pos.CENTER_LEFT }
            return HBox(
                contentHBox,
                HBox(10.0, buttonInitial, Text("+").apply { font = Font.font(null, FontWeight.BOLD, 14.0) }, buttonKey, buttonOptional).apply { alignment = Pos.CENTER }).apply {
                alignment = Pos.CENTER
                setHgrow(contentHBox, Priority.ALWAYS)
            }
        }

        content.children.addAll(
            Text(""),
            createButtonHBox("Save", saveButtonInitial, saveButtonKey, saveButtonOptional),
            createButtonHBox("Open", openButtonInitial, openButtonKey, openButtonOptional),
            createButtonHBox("New Tab", newTabButtonInitial, newTabButtonKey, newTabButtonOptional),
            createButtonHBox("Close Tab", closeTabButtonInitial, closeTabButtonKey, closeTabButtonOptional),
            createButtonHBox("Bold", boldButtonInitial, boldButtonKey, boldButtonOptional),
            createButtonHBox("Italic", italicButtonInitial, italicButtonKey, italicButtonOptional),
            createButtonHBox("Heading", headingButtonInitial, headingButtonKey, headingButtonOptional),
            resetButtonKey.apply { alignment = Pos.CENTER },
        )

        dialog.dialogPane.content = content

        fun createInitialButton(button: Button, action: String) {
            button.onKeyPressed = EventHandler { event ->
                val keyCode = event.code.toString()
                println("Initial key for $action: $keyCode")
                customHotkeys.updateInitialKey(action, keyCode)
            }
        }

        createInitialButton(saveButtonInitial, "save")
        createInitialButton(openButtonInitial, "open")
        createInitialButton(newTabButtonInitial, "newTab")
        createInitialButton(closeTabButtonInitial, "closeTab")
        createInitialButton(boldButtonInitial, "bold")
        createInitialButton(italicButtonInitial, "italic")
        createInitialButton(headingButtonInitial, "heading")

        fun createKeycodeButton(button: Button, action: String) {
            button.onKeyPressed = EventHandler { event ->
                val keyCode = event.code.toString()
                println("Key code for $action: $keyCode")
                customHotkeys.updateKeycode(action, keyCode)
            }
        }

        createKeycodeButton(saveButtonKey, "save")
        createKeycodeButton(openButtonKey, "open")
        createKeycodeButton(newTabButtonKey, "newTab")
        createKeycodeButton(closeTabButtonKey, "closeTab")
        createKeycodeButton(boldButtonKey, "bold")
        createKeycodeButton(italicButtonKey, "italic")
        createKeycodeButton(headingButtonKey, "heading")

        fun createOptionalButton(button: Button, action: String) {
            button.onKeyPressed = EventHandler { event ->
                val keyCode = event.code.toString()
                println("Optional key for $action: $keyCode")
                customHotkeys.updateOptionalKey(action, keyCode)
            }
        }

        createOptionalButton(saveButtonOptional, "save")
        createOptionalButton(openButtonOptional, "open")
        createOptionalButton(newTabButtonOptional, "newTab")
        createOptionalButton(closeTabButtonOptional, "closeTab")
        createOptionalButton(boldButtonOptional, "bold")
        createOptionalButton(italicButtonOptional, "italic")
        createOptionalButton(headingButtonOptional, "heading")

        fun resetKeybinds(keyCode: String, buttonInitial: Button, buttonKey: Button, buttonOptional: Button, shortcut: MutableList<KeyCode>) {
            buttonInitial.text = "Ctrl"
            buttonKey.text = (keyCode)
            buttonOptional.text = ""
            shortcut.clear()
            shortcut.add(KeyCode.getKeyCode(keyCode))
        }

        resetButtonKey.onMouseClicked = EventHandler {
            resetKeybinds("S", saveButtonInitial, saveButtonKey, saveButtonOptional, Main.saveShortcut)
            resetKeybinds("O", openButtonInitial, openButtonKey, openButtonOptional, Main.openShortcut)
            resetKeybinds("T", newTabButtonInitial, newTabButtonKey, newTabButtonOptional, Main.newTabShortcut)
            resetKeybinds("W", closeTabButtonInitial, closeTabButtonKey, closeTabButtonOptional, Main.closeTabShortcut)
            resetKeybinds("B", boldButtonInitial, boldButtonKey, boldButtonOptional, Main.boldShortcut)
            resetKeybinds("I", italicButtonInitial, italicButtonKey, italicButtonOptional, Main.italicShortcut)
            resetKeybinds("H", headingButtonInitial, headingButtonKey, headingButtonOptional, Main.headingShortcut)
        }

        dialog.setResultConverter { dialogButton ->
            if (dialogButton == saveButton) {
                "saved"
            } else {
                null
            }
        }

        dialog.showAndWait()
    }

    private val customHotkeyItem = MenuItem("Edit keyboard shortcuts").apply {
        onAction = EventHandler {
            showKeyboardShortcutsUI()
        }
    }

    init {
        graphic = FontIcon("fas-keyboard")
        items.setAll(customHotkeyItem)
    }
}
