package com.editor.application.menubar.account

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import com.editor.application.api.ApiFunctions
import com.editor.application.api.LoginData
import com.editor.application.api.UserData
import com.editor.application.settings.Settings
import javafx.application.Application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirs
import net.harawata.appdirs.AppDirsFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import com.editor.application.menubar.AccountMenu

object Credentials {

    private var credentialsDir: String

    init {
        val appDirs: AppDirs = AppDirsFactory.getInstance()
        credentialsDir = appDirs.getUserConfigDir("markdowneditor", "v1", "markdowneditor")
        Files.createDirectories(Paths.get(credentialsDir))
    }

    private val credentialsFile = File(credentialsDir, "credentials.json")
    private val format = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    private lateinit var credentials: UserData

    init {
        // Create file if it doesn't exist
        credentialsFile.createNewFile()
        val credentialsStr = credentialsFile.bufferedReader().use { it.readText() }
        println("credentialsStr = $credentialsStr")
        try {
            if (credentialsStr != "") {
                credentials = format.decodeFromString(credentialsStr)
                println("credentials = $credentials")
                ApiFunctions.login(credentials.email, credentials.password)
            }
        } catch (e: Exception) { println("Credentials Error: $e")}
    }

    fun save(newCredentials: UserData) {
        // TODO: SAVING RAW PASSWORD IS HIGHLY UNSECURE... FIX ASAP
        credentialsFile.writeText(format.encodeToString(newCredentials))
    }

    fun delete(): Boolean {
        return credentialsFile.delete()
    }
}