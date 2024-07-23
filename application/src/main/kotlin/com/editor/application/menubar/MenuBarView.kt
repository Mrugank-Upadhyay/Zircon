package com.editor.application.menubar

import javafx.scene.control.MenuBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class MenuBarView: HBox() {

    private val toolBarHeight = 40.0    // determined through trial and error
    private val menu = MenuBar(FileMenu(), EditMenu(), HelpMenu()).apply {
        minHeight = toolBarHeight
    }
    private val account = MenuBar(AccountMenu()).apply {
        minHeight = toolBarHeight
    }
    private val customHotkey = MenuBar(CustomHotkeyMenu()).apply {
        minHeight = toolBarHeight
    }

    init {
        setHgrow(menu, Priority.ALWAYS)
        children.addAll(menu, customHotkey, account)
    }
}