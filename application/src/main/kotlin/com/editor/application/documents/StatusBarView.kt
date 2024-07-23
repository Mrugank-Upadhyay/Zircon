package com.editor.application.documents

import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class StatusBarView: VBox() {

    private val tmp = Label("Status")

    init {
        children.addAll(tmp)
        background = Background.fill(Color.LIGHTGRAY)
    }
}