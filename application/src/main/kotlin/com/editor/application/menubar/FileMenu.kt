package com.editor.application.menubar

import com.editor.application.Main
import com.editor.application.documents.EditorPane
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import kotlin.system.exitProcess

class FileMenu: Menu("File") {

    private val newItem = MenuItem("New").apply {
        onAction = EventHandler {
            Main.getDocumentsView()!!.newTab(null)
        }
    }

    private val openItem = MenuItem("Open").apply {
        onAction = EventHandler {
            Main.getDocumentsView()!!.openTab()
        }
    }

    private val saveItem = MenuItem("Save").apply {
        onAction = EventHandler {
            val selectedEditorPane: EditorPane = Main.getDocumentsView()!!.selectionModel.selectedItem.content as EditorPane
            selectedEditorPane.save()
        }
    }

    private val exportItemPDF = MenuItem("Export PDF").apply {
        onAction = EventHandler {
            val selectedEditorPane: EditorPane = Main.getDocumentsView()!!.selectionModel.selectedItem.content as EditorPane
            selectedEditorPane.export(0)
        }
    }
    private val exportItemHTML = MenuItem("Export HTML").apply {
        onAction = EventHandler {
            val selectedEditorPane: EditorPane = Main.getDocumentsView()!!.selectionModel.selectedItem.content as EditorPane
            selectedEditorPane.export(1)
        }
    }

    private val exitItem = MenuItem("Exit").apply {
        onAction = EventHandler {
            exitProcess(0)
        }
    }
    
    init {
        items.addAll(newItem, openItem, saveItem, exportItemPDF, exportItemHTML, exitItem)
    }
}
