package com.editor.application.menubar

import com.editor.application.Main
import com.editor.application.menubar.account.LoginWindow
import com.editor.application.menubar.account.RegisterWindow
import com.editor.application.api.ApiFunctions
import com.editor.application.menubar.account.Credentials
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.Stage
import org.kordamp.ikonli.javafx.FontIcon

class AccountMenu: Menu() {

    private val loginWindowHeight = 300.0       // trial and error
    private val registerWindowHeight = 380.0    // trial and error

    private val afterLogin: Runnable = Runnable {
        items.setAll(logoutItem)
        this@AccountMenu.text = ApiFunctions.name
        Main.getDocumentsView()!!.disableAccountSync(false)
        Main.getFileTreeView()!!.refreshView()
    }

    private val showLogin: Runnable = Runnable {
        newWindow.title = "Log In"
        newWindow.scene.root = LoginWindow(afterLogin, showRegister, close)
        newWindow.height = loginWindowHeight
    }

    private val showRegister: Runnable = Runnable {
        newWindow.title = "Register"
        newWindow.scene.root = RegisterWindow(afterLogin, showLogin, close)
        newWindow.height = registerWindowHeight
    }

    private val close: Runnable = Runnable {
        newWindow.close()
    }

    private val newWindow = Stage().apply {
        title = "Log In"
        scene = Scene(LoginWindow(afterLogin, showRegister, close))
        height = loginWindowHeight
        width = 300.0
        isResizable = false
    }

    private val loginItem = MenuItem("Login").apply {
        onAction = EventHandler {
            showLogin.run()
            newWindow.show()
            newWindow.toFront()
        }
    }

    private val logoutItem = MenuItem("Logout").apply {
        onAction = EventHandler {
            items.setAll(loginItem)
            val delStatus = Credentials.delete()
            if (!delStatus) {
                println("ERROR: Unable to delete credentials file")
            }
            this@AccountMenu.text = ""

            Main.getDocumentsView()!!.disableAccountSync(true)
            ApiFunctions.userToken = ""
            Main.getFileTreeView()!!.refreshView()
        }
    }

    init {
        graphic = FontIcon("fas-user-circle")
        if (ApiFunctions.userToken == "") {
            items.setAll(loginItem)
            this.text = ""
            Main.getDocumentsView()!!.disableAccountSync(true)
        } else {
            items.setAll(logoutItem)
            this.text = ApiFunctions.name
            Main.getDocumentsView()!!.disableAccountSync(false)
        }
    }
}