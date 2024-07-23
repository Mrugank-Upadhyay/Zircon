package com.editor.application.documents

import com.editor.application.LocalFileChooser
import com.editor.application.Note
import javafx.event.EventHandler
import javafx.scene.control.*
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File

class DocumentsView(params: List<String>): TabPane() {

    private val newTabBtn = Tab().apply {
        isClosable = false
        graphic = FontIcon("fas-plus")
        tooltip = Tooltip("New Tab")
    }

    private val openChooser = LocalFileChooser("Open")

    private val closeAllRight: MenuItem = MenuItem("Close All Right").apply {
        onAction = EventHandler {
            val tab = this@DocumentsView.selectionModel.selectedItem!!
            val idx = tabs.indexOf(tab)
            tabs.remove(idx + 1, tabs.size - 1)
        }
    }

    private val closeAllLeft: MenuItem = MenuItem("Close All Left").apply {
        onAction = EventHandler {
            val tab = this@DocumentsView.selectionModel.selectedItem!!
            val idx = tabs.indexOf(tab)
            tabs.remove(0, idx)
        }
    }

    private val closeAllOthers: MenuItem = MenuItem("Close All Others").apply {
        onAction = EventHandler {
            val tab = this@DocumentsView.selectionModel.selectedItem!!
            tabs.removeIf{currTab -> currTab !== tab && currTab !== tabs[tabs.size - 1]}
        }
    }

    private val closeAll: MenuItem = MenuItem("Close All").apply {
        onAction = EventHandler {
            tabs.remove(0, tabs.size - 1)
        }
    }

    private val contextMenu: ContextMenu = ContextMenu(closeAllRight, closeAllLeft, closeAllOthers, closeAll)

    init {
        tabs.add(newTabBtn)
        selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            if (newTab === newTabBtn) {
                newTab(null)
            }
        }

        if (params.isNotEmpty()) {
            var oneOpened = false
            for (param in params) {
                // requires param is absolute path
                val file = File(param)
                if (file.exists() && file.isFile && file.extension == "md") {
                    newTab(Note(file.name, false, file, null))
                    oneOpened = true
                }
            }
            if (!oneOpened) newTab(null)
        } else {
            newTab(null)
        }
    }

    fun newTab(note: Note?, tabIndex: Int = tabs.size - 1) {
        println("Document view new tab Note values: ${note?.serverFile}")
        // if file already opened, don't open again
        if (note != null) {
            for (tab in tabs) {
                if (tab == newTabBtn) continue
                val editorPane = tab.content as EditorPane
                if (editorPane.note != null && editorPane.note!!.name == note.name) {
                    selectionModel.select(tab)
                    return
                }
            }
        }
        val tab = Tab(note?.name ?: "*Untitled")
        tab.content = EditorPane(note, tab)
        tabs.add(tabIndex, tab)
        tab.contextMenu = contextMenu
        selectionModel.select(tabIndex)
    }

    fun openTab() {
        val file = openChooser.showOpen()
        if (file != null) {
            newTab(Note(file.name, false, file))
        }
    }

    fun disableAccountSync(disabled: Boolean) {
        for (i in 0 until tabs.size - 1) {
            val editorPane = (tabs[i].content as EditorPane)
            editorPane.disableAccountSync(disabled)
        }
    }

}