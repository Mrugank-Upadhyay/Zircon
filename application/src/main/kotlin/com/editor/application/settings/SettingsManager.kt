package com.editor.application.settings

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object SettingsManager {

    private var settingsDir: String
    init {
        val appDirs: AppDirs = AppDirsFactory.getInstance()
        settingsDir = appDirs.getUserConfigDir("markdowneditor", "v1", "markdowneditor")
        Files.createDirectories(Paths.get(settingsDir))
    }
    private val settingsFile = File(settingsDir, "settings.json")
    private val format = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    var settings: Settings
    init {
        // Create file if it doesn't exist
        settingsFile.createNewFile()
        val settingsStr = settingsFile.bufferedReader().use{it.readText()}
        try {
            settings = format.decodeFromString(settingsStr)
        }
        catch (e: Exception) {
            println("Could not load settings.json.")
            settings = Settings()
        }
        setTheme(settings.theme)
    }

    fun setTheme(value: String) {
        settings.theme = value
        Application.setUserAgentStylesheet(
            when (value) {
                "light" -> PrimerLight().userAgentStylesheet
                else -> PrimerDark().userAgentStylesheet
            }
        )
    }
    fun save() {
        settingsFile.writeText(format.encodeToString(settings))
    }
}