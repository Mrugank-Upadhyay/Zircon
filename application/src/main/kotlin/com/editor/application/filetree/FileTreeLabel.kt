package com.editor.application.filetree

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import org.kordamp.ikonli.javafx.FontIcon

class FileTreeLabel(name: String, isDir: Boolean, onLocal: Boolean, onServer: Boolean): HBox() {

    private val icon = if (isDir) FontIcon("far-folder") else FontIcon("far-file")
    private val label = Label(name)
    private val localIcon = if (!isDir && onLocal) FontIcon("fas-hdd") else null
    private val serverIcon = if (!isDir && onServer) FontIcon("fas-cloud") else null
    private val localTooltip = Tooltip("File available on local").apply {
        style = "-fx-font-size: 14"
    }
    private val serverTooltip = Tooltip("File available on server").apply {
        style = "-fx-font-size: 14"
    }

    init {
        Tooltip.install(localIcon, localTooltip)
        Tooltip.install(serverIcon, serverTooltip)

        alignment = Pos.CENTER_LEFT
        spacing = 5.0

        children.addAll(icon, label)
        if (localIcon != null) children.add(localIcon)
        if (serverIcon != null) children.add(serverIcon)
    }
}