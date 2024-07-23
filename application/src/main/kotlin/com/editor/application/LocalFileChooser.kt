package com.editor.application

import javafx.stage.FileChooser
import java.io.File

class LocalFileChooser(title: String) {

    private val fileChooser = FileChooser().apply {
        this.title = title
        extensionFilters.add(FileChooser.ExtensionFilter("MD Files", "*.md"))
    }

    private fun init() {
        val notesDir = FileUtils.getNotesDirectory(true)
        fileChooser.initialDirectory = File(notesDir)
    }

    fun showOpen(): File? {
        init()
        Main.getPrimaryStage() ?: return null
        return fileChooser.showOpenDialog(Main.getPrimaryStage())
    }

    fun showSave(): File? {
        init()
        val file = fileChooser.showSaveDialog(Main.getPrimaryStage())
        if (file != null && !file.name.endsWith(".md")) {
            val filepath = file.path.substringBeforeLast(file.name)
            val newFile = File(filepath + file.name + ".md")
            return if (newFile.exists()) {
                AlertDialog("File with .md extension already exists")
                null
            } else {
                newFile
            }
        }
        return file
    }
}