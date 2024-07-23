package com.editor.application.documents

import com.editor.application.Note
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.text.SimpleDateFormat
import java.util.*

class ConflictWindow(note: Note, editorPane: EditorPane, close: Runnable): VBox() {

    private val title = Label("Merge Conflicts!").apply {
        style = "-fx-font-weight: bold"
    }
    private val description = Label("Would you like to keep the local version or server version?")
    private val options = ToggleGroup()
    private val serverModified = convertLongToTime(note.serverFile!!.modified)
    private val server = RadioButton("Server version (modified: $serverModified)").apply {
        toggleGroup = options
        isSelected = true
    }
    private val localModified = convertLongToTime(note.localFile!!.lastModified())
    private val local = RadioButton("Local version (modified: $localModified)").apply {
        toggleGroup = options
    }
    private val merge = Button("Merge").apply {
        onAction = EventHandler {
            editorPane.merge(server.isSelected)
            close.run()
        }
    }
    private val mergeView = HBox(merge).apply {
        alignment = Pos.CENTER
    }

    init {
        spacing = 5.0
        padding = Insets(10.0)
        children.addAll(title, description, server, local, mergeView)
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MM-dd-yyyy HH:mm")
        return format.format(date)
    }
}