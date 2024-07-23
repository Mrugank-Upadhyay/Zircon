package com.editor.application

import javafx.stage.FileChooser
import java.io.File

class PDFFileChooser {

    private val pdf = FileChooser.ExtensionFilter("PDF Files", "*.pdf")
    private val html = FileChooser.ExtensionFilter("HTML Files", "*.html")
    private val fileChooser = FileChooser().apply {
        title = "Export As"
        extensionFilters.addAll(pdf, html)
    }

    private fun init()
    {
        val notesDir = FileUtils.getNotesDirectory(true)
        fileChooser.initialDirectory = File(notesDir)
    }

    fun showSavePDF(): File?
    {
        init()
        Main.getPrimaryStage() ?: return null
        fileChooser.selectedExtensionFilter = pdf
        return fileChooser.showSaveDialog(Main.getPrimaryStage())
    }

    fun showSaveHTML(): File?
    {
        init()
        Main.getPrimaryStage() ?: return null
        fileChooser.selectedExtensionFilter = html
        return fileChooser.showSaveDialog(Main.getPrimaryStage())
    }
}