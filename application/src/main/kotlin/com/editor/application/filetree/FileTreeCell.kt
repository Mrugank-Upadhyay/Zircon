package com.editor.application.filetree

import com.editor.application.Main
import com.editor.application.Note
import com.editor.application.documents.EditorPane
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.control.TreeCell
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

class FileTreeCell(treeView: FileTreeView): TreeCell<Note>() {

    private var label: FileTreeLabel? = null

    private val textField = TextField().apply {
        onKeyReleased = EventHandler {
            if (it.code == KeyCode.ENTER) {
                // if rename failed, newName will just be oldName
                val (oldName, newName) = item.rename(text)
                val filteredTabs = Main.getDocumentsView()!!.tabs.filtered { tab -> tab.text == oldName }
                if (!filteredTabs.isEmpty()) {
                    val docTab = filteredTabs[0]
                    docTab.text = newName
                }
                updateItem(item, false)
                clearRename()
            } else if (it.code == KeyCode.ESCAPE) {
                clearRename()
            }
        }
    }

    private val renameItem = MenuItem("Rename").apply {
        onAction = EventHandler {
            startRename()
        }
    }

    private val deleteItem = MenuItem("Delete").apply {
        onAction = EventHandler {
            val name = item.name
            item.delete()
            treeView.refreshView()
            val tabToDelete = Main.getDocumentsView()!!.tabs.filtered{tab -> tab.text == name}
            println(tabToDelete)
            if (tabToDelete.size > 0) {
                Main.getDocumentsView()!!.tabs.remove(tabToDelete[0])
            }
        }
    }

    private val optionsMenu = ContextMenu(renameItem, deleteItem)

    private fun getString(): String {
        return if (item == null) "" else item.name
    }

    private fun startRename() {
        super.startEdit()
        graphic = textField
        textField.text = getString()
        textField.selectRange(0, getString().length - 3)    // select from start to left of ".md"
        textField.requestFocus()
    }

    private fun clearRename() {
        super.cancelEdit()
        graphic = label
    }

    override fun updateItem(item: Note?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item != null) {
            contextMenu = if (!item.isDirectory) optionsMenu else null
            label = FileTreeLabel(item.name, item.isDirectory, item.localFile != null, item.serverFile != null)
            graphic = label
        } else {
            contextMenu = null
            label = null
            graphic = null
        }
    }

    init {
        onMouseClicked = EventHandler {
            if (!isEmpty && !treeItem.value.isDirectory) {
                if (it.button.equals(MouseButton.PRIMARY) && it.clickCount == 2) {      // double right click
                    Main.getDocumentsView()!!.newTab(treeItem.value)
                }
            }
        }
    }
}