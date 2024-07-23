package com.editor.application.filetree

import com.editor.application.FileUtils
import com.editor.application.Main
import com.editor.application.Note
import com.editor.application.api.ApiFunctions
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File

class FileTreeView: Pane() {

    private val treeMap = HashMap<String, TreeItem<Note>>()

    private val startWidth = 250.0

    private val treeView = TreeView<Note>().apply {
        minWidth = 150.0
        prefWidth = startWidth
        maxWidth = 600.0    // stage min width / 2
        setCellFactory { _ -> FileTreeCell(this@FileTreeView) }
    }

    private val refreshButton = Button().apply {
        layoutX = startWidth - 42.0         // trial and error
        layoutY = 0.0
        graphic = FontIcon("fas-sync-alt")
        tooltip = Tooltip("Refresh")
        onAction = EventHandler {
            refreshView()
        }
    }

    private val resizeLine = Line(startWidth, 0.0, startWidth, 0.0).apply {
        stroke = Color.TRANSPARENT
        strokeWidth = 5.0
        onMouseEntered = EventHandler {
            scene.cursor = Cursor.H_RESIZE
        }
        onMouseDragged = EventHandler {
            if (treeView.minWidth < it.sceneX && it.sceneX < treeView.maxWidth) {
                treeView.prefWidth = it.sceneX
                startX = it.sceneX
                endX = it.sceneX
                refreshButton.layoutX = it.sceneX - refreshButton.width
            }
        }
        onMouseExited = EventHandler {
            scene.cursor = Cursor.DEFAULT
        }
    }

    fun refreshView() {
        treeMap.clear()
        // local files
        val notesDir = FileUtils.getNotesDirectory(false)
        createLocalTree(File(notesDir), null)   // updates treeMap
        // server files
        if (ApiFunctions.userToken != "") {
            val serverNotes = ApiFunctions.listNotes()
            for (note in serverNotes) {
                if (!treeMap.containsKey(note.filename)) {
                    val path = note.filename.split('/')
                    var idx = 0
                    var stem = "Notes"
                    while (idx < path.size && treeMap.containsKey(stem + '/' + path[idx])) {
                        stem += '/' + path[idx]
                        idx++
                    }
                    // at this point, stem is part that exists in tree
                    // now, add remaining parts to tree and map
                    while (idx < path.size) {
                        val item = TreeItem(Note(path[idx], true))
                        val partPath = stem + '/' + path[idx]
                        treeMap[partPath] = item
                        // get sorted position
                        var pos = 0
                        if (treeMap[stem]!!.children[0] != null) {      // directory contains at least one file
                            while (pos < treeMap[stem]!!.children.size &&
                                treeMap[stem]!!.children[pos].value.name < item.value.name
                            ) pos++
                        } else {
                            treeMap[stem]!!.children.clear()
                        }
                        treeMap[stem]!!.children.add(pos, item)
                        treeMap[stem]!!.children[pos].children.add(null)
                        stem = partPath
                        idx++
                    }
                    treeMap[stem]!!.value.isDirectory = false
                    treeMap[stem]!!.value.serverFile = note
                    treeMap[stem]!!.children.clear()
                } else {
                    treeMap[note.filename]?.value?.serverFile = note
                }
            }
        }
        treeView.root?.let {
            it.isExpanded = true
        }
    }

    private fun createLocalTree(file: File, parent: TreeItem<Note>?) {
        var item: TreeItem<Note>? = null
        if (file.isDirectory) {
            item = TreeItem(Note(file.name, true, file))
            val subFiles = file.listFiles()
            if (subFiles.isNullOrEmpty()) {
                item.children.add(null)
            } else {
                subFiles.sortBy { !it.isDirectory }     // show directories towards top
                subFiles.forEach { f -> createLocalTree(f, item) }
            }
        } else if (file.extension == "md") {
            item = TreeItem(Note(file.name, false, file))
        }
        if (item != null) {
            if (parent == null) {
                treeMap["Notes"] = item
                treeView.root = item
            } else {
                treeMap["Notes/" + FileUtils.getRelativePath(file)] = item
                parent.children.add(item)
            }
        }
    }

    init {
        // set height of tree view and resize line to be bottom of pane
        treeView.prefHeightProperty().bind(heightProperty())
        resizeLine.endYProperty().bind(heightProperty())

        // populate tree view
        refreshView()

        children.addAll(treeView, refreshButton, resizeLine)
    }
}