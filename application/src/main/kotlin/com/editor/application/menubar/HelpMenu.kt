package com.editor.application.menubar

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

class HelpMenu: Menu("Help") {

    private val aboutItem = MenuItem("About")

    init {
        items.addAll(aboutItem)
    }
}