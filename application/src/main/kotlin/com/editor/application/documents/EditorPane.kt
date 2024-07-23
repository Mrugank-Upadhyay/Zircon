package com.editor.application.documents

import com.editor.application.*
import com.editor.application.api.ApiFunctions
import com.editor.application.highlight.Highlighter
import com.editor.application.settings.SettingsManager
import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.DataHolder
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.fxmisc.richtext.util.UndoUtils
import org.kordamp.ikonli.javafx.FontIcon
import org.reactfx.SuspendableYes
import java.io.File

class EditorPane(var note: Note?, val parentTab: Tab) : BorderPane() {

    private val area = EditorTextArea()
    private val saveChooser = LocalFileChooser("Save As")
    private val pdfChooser = PDFFileChooser()
    private val uploadBtn = createButton(styleClass = "upload", action = this::upload, icon = "fas-upload", toolTip = "Upload").apply {
        isDisable = (ApiFunctions.userToken == "" || note == null || note!!.serverFile !== null)
    }
    private val downloadBtn = createButton(styleClass = "download", action = this::download, icon = "fas-download", toolTip = "Download").apply {
        isDisable = (ApiFunctions.userToken == "" || note == null || note!!.localFile !== null)
    }
    private var caretNodes: List<Node> = listOf()
    private val highlighter = Highlighter()
    private val suspendUndo = SuspendableYes()

    private var shareWindow: Stage? = null
    private var shareBtn: Button
    private var autoSaveBtn: Button


    init {
        area.isWrapText = true
        area.undoManager = UndoUtils.richTextSuspendableUndoManager(area, suspendUndo)
        if (note !== null && note!!.serverFile !== null && integerToPermission(note!!.serverFile!!.permission) == Permissions.READ_ONLY) {
            area.isDisable = true
        }

        val theme = SettingsManager.settings.theme
        val themeBtn = createButton(
            styleClass = "theme", icon = if (theme == "light") "fas-sun" else "far-moon", toolTip = "Toggle Theme"
        )
        themeBtn.setOnMouseClicked { _ -> toggleTheme(themeBtn) }
        val richModeBtn =
            createToggleButton(styleClass = "richmode", icon = "fab-markdown", toolTip = "Toggle Rich-Text Mode")
        richModeBtn.isSelected = true

        val saveBtn = createButton(styleClass = "save", action = this::save, icon = "fas-save", toolTip = "Save")

        val undoBtn = createButton(styleClass = "undo", action = area::undo, icon = "fas-undo-alt", toolTip = "Undo")
        val redoBtn = createButton(styleClass = "redo", action = area::redo, icon = "fas-redo-alt", toolTip = "Redo")

        val cutBtn = createButton(styleClass = "cut", action = area::cut, icon = "fas-cut", toolTip = "Cut")
        val copyBtn = createButton(styleClass = "copy", action = area::copy, icon = "fas-copy", toolTip = "Copy")
        val pasteBtn = createButton(styleClass = "paste", action = area::paste, icon = "fas-paste", toolTip = "Paste")
        shareBtn = createButton(styleClass = "share", action = this::createShareWindow, icon = "fas-share-alt", toolTip = "Share")
        shareBtn.isDisable = !(note != null && note!!.serverFile != null)

        val boldBtn =
            createToggleButton(styleClass = "bold", action = this::bolden, icon = "fas-bold", toolTip = "Bold")
        val italicsBtn =
            createToggleButton(styleClass = "bold", action = this::italicize, icon = "fas-italic", toolTip = "Italic")

        val headingIncBtn = createButton(
            styleClass = "heading-inc",
            action = this::increaseHeading,
            text = "+",
            icon = "fas-heading",
            toolTip = "Increase Heading"
        )
        val headingDecBtn = createButton(
            styleClass = "heading-dec",
            action = this::decreaseHeading,
            text = "-",
            icon = "fas-heading",
            toolTip = "Decrease Heading"
        )

        val codeBtn =
            createToggleButton(styleClass = "code", action = this::inlineCode, icon = "fas-code", toolTip = "Code")

        var isAutoSaveActive = note != null
        autoSaveBtn = createButton(styleClass = "autoSave", icon = if (isAutoSaveActive) "far-check-circle" else "far-clock",
            toolTip = if (isAutoSaveActive) "Auto Save Enabled" else "Auto Save Disabled").apply {
                isDisable = note == null
        }
        saveBtn.isDisable = isAutoSaveActive
        autoSaveBtn.setOnMouseClicked {
            isAutoSaveActive = !isAutoSaveActive
            autoSaveBtn.graphic = FontIcon(if (isAutoSaveActive) "far-check-circle" else "far-clock")
            autoSaveBtn.tooltip = Tooltip(if (isAutoSaveActive) "Auto Save Enabled" else "Auto Save Disabled")
            saveBtn.isDisable = isAutoSaveActive
        }

        this.addEventFilter(KeyEvent.KEY_PRESSED) {
            isAutoSaveActive = note != null
            autoSaveBtn.graphic = FontIcon(if (isAutoSaveActive) "far-check-circle" else "far-clock")
            autoSaveBtn.tooltip = Tooltip(if (isAutoSaveActive) "Auto Save Enabled" else "Auto Save Disabled")
            saveBtn.isDisable = isAutoSaveActive
        }

        val rightAlignedShare = HBox(shareBtn).apply {
            alignment = Pos.CENTER_RIGHT
        }
        HBox.setHgrow(rightAlignedShare, Priority.ALWAYS)

        val toolBar = ToolBar(
            themeBtn,
            richModeBtn,
            Separator(Orientation.VERTICAL),
            saveBtn,
            autoSaveBtn,
            uploadBtn,
            downloadBtn,
            undoBtn,
            redoBtn,
            cutBtn,
            copyBtn,
            pasteBtn,
            Separator(Orientation.VERTICAL),
            headingIncBtn,
            headingDecBtn,
            boldBtn,
            italicsBtn,
            codeBtn,
            rightAlignedShare,
        )

        // Layout
        top = toolBar
        center = area

        area.stylesheets.add(this::class.java.getResource("/css/EditorArea.css")?.toString() ?: "")
        changeCodeTheme("onedark", theme)

        fun createStyleSpans(text: String = area.text) = suspendUndo.suspendWhile {
            val blockRanges = highlighter.parse(text)
            val spansBuilder = StyleSpansBuilder<Collection<String>>()
            var pos = 0
            val textLength = area.length
            blockRanges.flatMap { block -> block.styleRanges }.forEach { sr ->
                if (pos < sr.start) {
                    spansBuilder.add(emptyList(), sr.start - pos)
                }
                if (sr.length <= 0) {
                    println(sr)
                }
                spansBuilder.add(sr.styleClasses, sr.length)
                pos = sr.end
            }
            if (pos <= textLength) {
                spansBuilder.add(emptyList(), textLength - pos)
            }

            val spans = spansBuilder.create()

            area.setStyleSpans(0, spans)

            var line = 0
            for (block in blockRanges) {
                if (line < block.startLine) {
                    for (i in line until block.startLine) {
                        area.setParagraphStyle(i, listOf())
                    }
                }
                for (i in block.startLine..block.endLine) {
                    val classes = block.styleClasses + when (i) {
                        block.startLine -> listOf("rounded-top")
                        block.endLine -> listOf("rounded-bottom")
                        else -> listOf()
                    }
                    area.setParagraphStyle(i, classes)
                }
                line = block.endLine + 1
            }
        }

        fun applyNoStyles() = suspendUndo.suspendWhile {
            area.setStyle(0, area.text.length, listOf(""))
            for (i in 0 until area.paragraphs.size) {
                area.clearParagraphStyle(i)
            }
        }

        richModeBtn.selectedProperty().addListener { _, _, selected ->
            if (selected) {
                createStyleSpans()
            } else {
                applyNoStyles()
            }
        }

        area.textProperty().addListener { _, _, newValue ->
            if (richModeBtn.isSelected) {
                createStyleSpans(newValue)
            } else {
                applyNoStyles()
            }

            // Add/remove unsaved indicator
            if (parentTab.text[0] != '*') {
                if (note == null || note!!.read() != area.text) {
                    parentTab.text = '*' + parentTab.text
                }
            } else {
                if (note != null && note!!.read() == area.text) {
                    parentTab.text = parentTab.text.substring(1)
                }
            }
        }

        area.caretPositionProperty().addListener { _, _, pos ->
            caretNodes = highlighter.caretNodes(pos)
            var bold = false
            var italic = false
            var code = false
            headingIncBtn.isDisable = false
            headingDecBtn.isDisable = true
            for (node in caretNodes) {
                when (node) {
                    is StrongEmphasis -> bold = strictWithin(node, pos)
                    is Emphasis -> italic = strictWithin(node, pos)
                    is Code -> code = strictWithin(node, pos)
                    is Heading -> {
                        headingIncBtn.isDisable = node.level == 6
                        headingDecBtn.isDisable = false
                    }
                }
            }
            codeBtn.isSelected = code
            boldBtn.isSelected = bold
            italicsBtn.isSelected = italic
        }

        // Populate editor
        if (note != null) {
            // Merge conflict
            if (note!!.localFile != null && note!!.serverFile != null &&
                note!!.localFile!!.readText() != note!!.serverFile!!.content) {
                mergeFile()
            } else {
                area.replaceText(note!!.read())
            }
        } else {
            area.replaceText("# Untitled\n\n")
        }

        val timeoutHandler = TimeoutHandler()

        // TODO: Move all functions out of init
        fun autoSave() {
            if (note != null && isAutoSaveActive) {
                timeoutHandler.clearTimeout()
                timeoutHandler.setTimeout(5000) {
                    this.save()
                    println("File automatically saved!")
                }
            }
        }

        // Only autosave if area isn't disabled
        area.textProperty().addListener { _ ->
            if (!this.area.isDisable) {
                autoSave()
            }
        }
    }


    private fun createShareWindow() {
        if (shareWindow != null) {
            shareWindow?.close()
        }
        shareWindow = Stage().apply {
            scene = Scene(ShareWindow(this@EditorPane))

            // Set position of second window, related to primary window.
//            newWindow?.x = Main.getPrimaryStage()?.x?.plus(100.0)!!
//            newWindow?.y = Main.getPrimaryStage()?.y?.plus(100.0)!!
            title = "Share"
            minHeight = 300.0
            minWidth = 500.0
            isResizable = true

        }
        shareWindow?.show()
        shareWindow?.toFront()
    }

    private fun toggleTheme(button: Button) {
        val theme = SettingsManager.settings.theme
        SettingsManager.setTheme(if (theme == "light") "dark" else "light")
        button.graphic = FontIcon(if (theme == "light") "far-moon" else "fas-sun")
        if (theme == "light") {
            changeCodeTheme("onedark", "dark")
        } else {
            changeCodeTheme("onedark", "light")
        }
    }

    var codeTheme: String = ""
    var variant: String = ""
    private fun changeCodeTheme(codeTheme: String, variant: String) {
        area.stylesheets.remove("/css/code-themes/${this.codeTheme}/${this.variant}.css")
        area.stylesheets.remove("/css/code-themes/${this.codeTheme}/theme.css")
        area.stylesheets.add("/css/code-themes/${codeTheme}/${variant}.css")
        area.stylesheets.add("/css/code-themes/${codeTheme}/theme.css")
        this.codeTheme = codeTheme
        this.variant = variant
    }

    private fun mergeFile() {
        val conflictWindow = Stage()
        val close = Runnable {
            conflictWindow.close()
        }
        conflictWindow.apply {
            scene = Scene(ConflictWindow(note!!, this@EditorPane, close))
            title = "Merge Conflict"
            minHeight = 200.0
            minWidth = 400.0
            isResizable = true
        }
        conflictWindow.showAndWait()
        conflictWindow.toFront()
    }

    fun merge(keepServer: Boolean) {
        area.replaceText(if (keepServer) note!!.serverFile!!.content else note!!.localFile!!.readText())
        save()
    }


    private fun integerToPermission(integer: Int): Permissions {
        return when (integer) {
            0 -> Permissions.OWNER
            1 -> Permissions.READ_WRITE
            2 -> Permissions.READ_ONLY
            else -> {
                throw Exception("Unknown Permission")
            }
        }
    }

    private fun strictWithin(node: Node, pos: Int): Boolean {
        return pos in node.startOffset + 1 until node.endOffset
    }

    private fun createButton(
        styleClass: String? = null,
        action: Runnable? = null,
        text: String? = null,
        toolTip: String? = null,
        icon: String? = null
    ): Button {
        return createButton(
            styleClass, action, text, toolTip, icon, false
        ) as Button
    }

    private fun createToggleButton(
        styleClass: String? = null,
        action: Runnable? = null,
        text: String? = null,
        toolTip: String? = null,
        icon: String? = null,
    ): ToggleButton {
        return createButton(
            styleClass, action, text, toolTip, icon, true
        ) as ToggleButton
    }

    private fun createButton(
        styleClass: String? = null,
        action: Runnable? = null,
        text: String? = null,
        toolTip: String? = null,
        icon: String? = null,
        toggle: Boolean
    ): Any {
        val button = if (toggle) ToggleButton() else Button()

        button.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE)
        if (styleClass != null) {
            button.styleClass.add(styleClass)
        }
        if (action != null) {
            button.setOnAction {
                action.run()
                area.requestFocus()
            }
        }
        if (toolTip != null) {
            button.tooltip = Tooltip(toolTip)
        }
        if (text != null) {
            button.text = text
        }
        if (icon != null) {
            button.graphic = FontIcon(icon)
        }

        return button
    }

    private inline fun <reified T> toggleDelimiters(delimiter: String) {
        val pos = area.caretPosition
        val delimLen = delimiter.length
        var delimNode: DelimitedNodeImpl? = null
        for (node in caretNodes) {
            when (node) {
                is T -> delimNode = node as DelimitedNodeImpl
            }
        }
        var surroundedByDelims = false
        try {
            surroundedByDelims = area.getText(
                area.caretPosition - delimLen,
                area.caretPosition + delimLen
            ) == delimiter.repeat(2)
        } catch (_: Exception) {}

        if (area.selection.start != area.selection.end) {
            val start = area.selection.start
            val end = area.selection.end
            if (delimNode == null) {
                area.replaceSelection("$delimiter${area.selectedText}$delimiter")
            } else {
                val delimOpen = delimNode.openingMarker
                val delimClose = delimNode.closingMarker
                val hasStart = start in delimOpen.startOffset .. delimOpen.endOffset
                val hasEnd = end in delimClose.startOffset .. delimClose.endOffset
                if (hasStart && hasEnd) {
                    area.replaceText(delimClose.startOffset, delimClose.endOffset, "")
                    area.replaceText(delimOpen.startOffset, delimOpen.endOffset, "")
                }
                else if (hasStart) {
                    area.insertText(end, delimiter)
                    area.replaceText(delimOpen.startOffset, delimOpen.endOffset, "")
                }
                else if (hasEnd) {
                    area.replaceText(delimClose.startOffset, delimClose.endOffset, "")
                    area.insertText(start, delimiter)
                }
                else {
                    area.replaceSelection("$delimiter${area.selectedText}$delimiter")
                }
            }
        } else if (surroundedByDelims) {
            area.replaceText(area.caretPosition - delimLen, area.caretPosition + delimLen, "")
        } else if (delimNode == null) {
            area.insertText(pos, delimiter.repeat(2))
            area.moveTo(pos + delimLen)
        } else {
            if (pos == delimNode.openingMarker.endOffset) {
                area.moveTo(delimNode.openingMarker.startOffset)
            } else if (pos == delimNode.closingMarker.startOffset) {
                area.moveTo(delimNode.closingMarker.endOffset)
            } else {
                area.insertText(pos, delimiter.repeat(2))
                area.moveTo(pos + 2)
            }
        }
    }

    fun bolden() {
        toggleDelimiters<StrongEmphasis>("**")
    }

    fun italicize() {
        toggleDelimiters<Emphasis>("*")
    }

    private fun inlineCode() {
        toggleDelimiters<Code>("`")
    }

    fun increaseHeading() {
        val pos = area.caretPosition
        val lineStart = pos - area.caretColumn
        var heading: Heading? = null
        for (node in caretNodes) {
            when (node) {
                is Heading -> heading = node
            }
        }
        if (heading == null) {
            area.insertText(lineStart, "# ")
            area.moveTo(pos + 2)
        } else {
            if (heading.level == 6) {
                return
            }
            area.replaceText(
                heading.openingMarker.startOffset, heading.openingMarker.endOffset, "#".repeat(heading.level + 1)
            )
            area.moveTo(pos + 1)
        }
    }

    private fun decreaseHeading() {
        val pos = area.caretPosition
        var heading: Heading? = null
        for (node in caretNodes) {
            when (node) {
                is Heading -> heading = node
            }
        }
        if (heading != null) {
            if (heading.level == 1) {
                area.replaceText(
                    heading.openingMarker.startOffset, heading.openingMarker.endOffset + 1, ""
                )
                area.moveTo(pos - 2)
            } else {
                area.replaceText(
                    heading.openingMarker.startOffset, heading.openingMarker.endOffset, "#".repeat(heading.level - 1)
                )
                area.moveTo(pos - 1)
            }
        }
    }

    fun save() {
        // Local Save
        if (note == null) {
            val localFile = saveChooser.showSave() ?: return
            localFile.writeText(area.text)
            note = Note(localFile.name, false, localFile)
            parentTab.text = localFile.name
            uploadBtn.isDisable = false
            autoSaveBtn.isDisable = false
            Main.getFileTreeView()!!.refreshView()
        } else {
            note!!.save(area.text)
        }
        // Remove unsaved indicator
        if (parentTab.text[0] == '*') {
            parentTab.text = parentTab.text.substring(1)
        }
    }

    /** Export to PDF or HTML file locally
     * Type determines export, 0=pdf, 1=html.
     * File must be saved first so that the local file name exists.
     */
    fun export(type: Int) {
        // TODO: Add Alert Dialog on failure
        val targetFile = if (type == 0) {
            pdfChooser.showSavePDF() ?: return
        }
        else {
            pdfChooser.showSaveHTML() ?: return
        }

        val htmlRendered: HtmlRenderer = HtmlRenderer.builder().build()
        val parsedDocument = highlighter.doc
        val htmlString = htmlRendered.render(parsedDocument)
        val exportLocation = targetFile.absolutePath
        if (type == 0) {
            PdfConverterExtension.exportToPdf(exportLocation, htmlString, "", DataHolder.NULL)
        }
        else {
            targetFile.writeText(htmlString)
        }

    }

    private fun upload() {
        // requires local file to exist in notes dir, managed through enabling/disabling buttons
        ApiFunctions.save(note!!.getRelativePath(), area.text)
        val returnedNote = ApiFunctions.open(note!!.getRelativePath())
        note!!.serverFile = if (returnedNote.status.contains("ERROR")) null else returnedNote
        shareBtn.isDisable = false
        Main.getFileTreeView()!!.refreshView()
    }

    private fun download() {
        // requires server file to exist, managed through enabling/disabling buttons
        val file = File(FileUtils.getNotesDirectory(true) + note!!.serverFile!!.filename)
        file.writeText(area.text)
        note!!.localFile = file
        Main.getFileTreeView()!!.refreshView()
    }

    fun disableAccountSync(disabled: Boolean) {
        uploadBtn.isDisable = if (disabled) true else (note == null || note!!.localFile == null)
        downloadBtn.isDisable = if (disabled) true else (note == null || note!!.serverFile == null)
    }
}
