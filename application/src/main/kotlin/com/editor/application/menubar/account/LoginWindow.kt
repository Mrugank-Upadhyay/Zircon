package com.editor.application.menubar.account

import com.editor.application.AlertDialog
import com.editor.application.api.ApiFunctions
import com.editor.application.api.UserData
import com.editor.application.menubar.account.Credentials
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class LoginWindow(afterLogin: Runnable, showRegister: Runnable, close: Runnable): VBox() {

    private val emailLabel = Label("Email")
    private val emailField = TextField().apply {
        promptText = "markdowney@example.com"
    }

    private val passwordLabel = Label("Password")
    private val passwordField = TogglePasswordField().apply {
        promptText = "password"
    }

    private val loginButton = Button("Log In").apply {
        onAction = EventHandler {
            // TODO: Add more error handling

            if (emailField.text == "" || passwordField.text == "") {
                AlertDialog("Please provide a valid email and password", "Invalid Email/Password")
                return@EventHandler
            }

            val loginData = ApiFunctions.login(emailField.text, passwordField.text)
            if (loginData.status.contains("ERROR")) {
                val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(loginData.status)!!.destructured
                AlertDialog(message, title)
                return@EventHandler
            }

            Credentials.save(UserData(emailField.text, passwordField.text))
            afterLogin.run()
            close.run()
        }
    }

    private val cancelButton = Button("Cancel").apply {
        onAction = EventHandler {
            close.run()
        }
    }

    private val options = HBox(loginButton, cancelButton).apply {
        alignment = Pos.CENTER
        spacing = 10.0
    }

    private val showRegisterButton = Button("Register").apply {
        styleClass.add("light-blue-btn")
        onAction = EventHandler {
            showRegister.run()
        }
    }

    private val buttonsBox = VBox(options, showRegisterButton).apply {
        alignment = Pos.CENTER
        spacing = 10.0
    }

    init {
        stylesheets.add(javaClass.getResource("/css/LoginWindow.css")?.toString() ?: "")
        children.addAll(emailLabel, emailField, passwordLabel, passwordField, buttonsBox)
        padding = Insets(10.0)
        spacing = 10.0
    }
}