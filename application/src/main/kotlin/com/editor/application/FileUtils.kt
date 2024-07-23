package com.editor.application

import java.io.File
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.Paths

object FileUtils {

    private val notesDirectory = System.getProperty("user.home") + System.getProperty("file.separator") + "Notes"

    fun getNotesDirectory(create: Boolean): String {
        if (create && !Files.isDirectory(Paths.get(notesDirectory))) Files.createDirectory(Paths.get(notesDirectory))
        return notesDirectory + System.getProperty("file.separator")
    }

    fun getRelativePath(file: File): String {
        val path = file.relativeTo(File(notesDirectory)).path.toString()
        return path.split(System.getProperty("file.separator")).joinToString("/")
    }

    fun rename(from: File, to: String): File? {
        try {
            val newFile = File(from.parent + System.getProperty("file.separator") + to)
            val success = from.renameTo(newFile)
            return if (success) newFile else null
        } catch (e: FileSystemException) {
            throw e
        }

    }
}