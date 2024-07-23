package com.editor.application

import com.editor.application.menubar.account.Credentials
import com.editor.application.documents.DocumentsView
import com.editor.application.filetree.FileTreeView
import com.editor.application.menubar.MenuBarView
import com.editor.application.settings.SettingsManager
import fr.brouillard.oss.cssfx.CSSFX
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import com.editor.application.menubar.CustomHotkeyMenu
import com.editor.application.menubar.CustomHotkeyFunctions

class Main : Application() {

    companion object {
        @JvmStatic
        private var pStage: Stage? = null
        var docView: DocumentsView? = null
        private var mbView: MenuBarView? = null
        private var ftView: FileTreeView? = null

        fun getPrimaryStage(): Stage? {
            return pStage
        }

        fun getDocumentsView(): DocumentsView? {
            return docView
        }

        fun getMenuBarView(): MenuBarView? {
            return mbView
        }

        fun getFileTreeView(): FileTreeView? {
            return ftView
        }
        var saveShortcut = mutableListOf(KeyCode.S)
        var openShortcut = mutableListOf(KeyCode.O)
        var newTabShortcut = mutableListOf(KeyCode.T)
        var closeTabShortcut = mutableListOf(KeyCode.W)
        var boldShortcut = mutableListOf(KeyCode.B)
        var italicShortcut = mutableListOf(KeyCode.I)
        var headingShortcut = mutableListOf(KeyCode.H)
    }

    override fun start(stage: Stage) {
        pStage = stage

        // Init the settings
        SettingsManager
        Credentials
        // Watch for CSS changes
        CSSFX.start()

        val params = parameters.unnamed

        docView = DocumentsView(params)
        mbView = MenuBarView()
        ftView = FileTreeView()

        val root = BorderPane(docView, mbView, null, null, ftView)

        stage.scene = Scene(root, 1200.0, 600.0).apply {
//            stylesheets.add(this::class.java.getResource("/css/LoginWindow.css")?.toString() ?: "")
        }
        stage.title = "Zircon"
        stage.minWidth = 700.0
        stage.minHeight = 400.0
        stage.show()

        val customHotkeys = CustomHotkeyFunctions()

        stage.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            customHotkeys.saveKeyPressed = customHotkeys.handleKeyEvent(event, saveShortcut, CustomHotkeyMenu.saveButtonInitial.text, customHotkeys.saveKeyPressed)
            customHotkeys.openKeyPressed = customHotkeys.handleKeyEvent(event, openShortcut, CustomHotkeyMenu.openButtonInitial.text, customHotkeys.openKeyPressed)
            customHotkeys.newTabKeyPressed = customHotkeys.handleKeyEvent(event, newTabShortcut, CustomHotkeyMenu.newTabButtonInitial.text, customHotkeys.newTabKeyPressed)
            customHotkeys.closeTabKeyPressed = customHotkeys.handleKeyEvent(event, closeTabShortcut, CustomHotkeyMenu.closeTabButtonInitial.text, customHotkeys.closeTabKeyPressed)
            customHotkeys.boldKeyPressed = customHotkeys.handleKeyEvent(event, boldShortcut, CustomHotkeyMenu.boldButtonInitial.text, customHotkeys.boldKeyPressed)
            customHotkeys.italicKeyPressed = customHotkeys.handleKeyEvent(event, italicShortcut, CustomHotkeyMenu.italicButtonInitial.text, customHotkeys.italicKeyPressed)
            customHotkeys.boldKeyPressed = customHotkeys.handleKeyEvent(event, headingShortcut, CustomHotkeyMenu.headingButtonInitial.text, customHotkeys.headingKeyPressed)
        }
    }

    override fun stop() {
        SettingsManager.save()
        super.stop()
    }
}
