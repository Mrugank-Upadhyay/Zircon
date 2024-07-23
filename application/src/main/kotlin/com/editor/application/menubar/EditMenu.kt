package com.editor.application.menubar

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

class EditMenu: Menu("Edit") {

    private val undoItem = MenuItem("Undo")
    private val redoItem = MenuItem("Redo")
    private val cutItem = MenuItem("Cut")
    private val copyItem = MenuItem("Copy")
    private val pasteItem = MenuItem("Paste")

    init {
        items.addAll(undoItem, redoItem, cutItem, copyItem, pasteItem)
    }
}