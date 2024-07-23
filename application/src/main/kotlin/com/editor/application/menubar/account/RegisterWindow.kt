package com.editor.application.menubar.account

import com.editor.application.AlertDialog
import com.editor.application.api.ApiFunctions
import com.editor.application.api.UserData
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class RegisterWindow(afterRegister: Runnable, showLogin: Runnable, close: Runnable): VBox() {

    private val nameLabel = Label("Name")
    private val nameField = TextField().apply {
        promptText = "Mark Downey"
    }

    private val emailLabel = Label("Email")
    private val emailField = TextField().apply {
        promptText = "markdowney@example.com"
    }

    private val passwordLabel = Label("Password")
    private val passwordField = TogglePasswordField().apply {
        promptText = "password"
    }

    private val registerButton = Button("Register").apply {
        onAction = EventHandler {
            // TODO: Add more error handling
            if (nameField.text == "" || emailField.text == "" || passwordField.text == "") {
                // TODO Break into multiple for each field
                AlertDialog("Please provide a valid name, email and password", "Invalid Name/Email/Password")
                return@EventHandler
            }
            val loginData = ApiFunctions.register(emailField.text, passwordField.text, nameField.text)
            if (loginData.status.contains("ERROR")) {
                val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(loginData.status)!!.destructured
                AlertDialog(message, title)
                return@EventHandler
            }
            Credentials.save(UserData(emailField.text, passwordField.text))
            afterRegister.run()
            close.run()
        }
    }

    private val cancelButton = Button("Cancel").apply {
        onAction = EventHandler {
            close.run()
        }
    }

    private val options = HBox(registerButton, cancelButton).apply {
        alignment = Pos.CENTER
        spacing = 10.0
    }

    private val showLoginButton = Button("Log In").apply {
        styleClass.add("light-blue-btn")
        onAction = EventHandler {
            showLogin.run()
        }
    }

    private val buttonsBox = VBox(options, showLoginButton).apply {
        alignment = Pos.CENTER
        spacing = 10.0
    }

    init {
        stylesheets.add(javaClass.getResource("/css/LoginWindow.css")?.toString() ?: "")
        children.addAll(nameLabel, nameField, emailLabel, emailField, passwordLabel, passwordField, buttonsBox)
        padding = Insets(10.0)
        spacing = 10.0
    }
}