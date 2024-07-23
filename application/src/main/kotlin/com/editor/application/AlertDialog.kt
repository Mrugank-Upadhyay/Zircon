package com.editor.application

import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType

class AlertDialog(message: String, header: String = "Alert") {

   init {
        val type: ButtonType = ButtonType("Ok", ButtonBar.ButtonData.OK_DONE)
        val alertDialog: Alert = Alert(Alert.AlertType.ERROR).apply {
            title = "Error"
            headerText = header
            contentText = message
            dialogPane.buttonTypes.setAll(type)
        }
        alertDialog.showAndWait()
    }
}