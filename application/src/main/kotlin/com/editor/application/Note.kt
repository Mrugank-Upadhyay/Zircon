package com.editor.application

import com.editor.application.api.ApiFunctions
import com.editor.application.api.NoteData
import java.io.File

class Note(var name: String, var isDirectory: Boolean, var localFile: File? = null, var serverFile: NoteData? = null) {

    fun getRelativePath(): String {
        return serverFile?.filename ?: FileUtils.getRelativePath(localFile!!)
    }

    fun read(): String {
        return serverFile?.content ?: localFile!!.readText()
    }

    fun save(contents: String) {
        if (localFile != null && localFile!!.exists()) {
            localFile?.writeText(contents)
        }
        if (serverFile != null) {
            ApiFunctions.save(serverFile!!.filename, contents)
        }
    }

    fun rename(newName: String): Pair<String, String> {
        val oldName = name
        if (localFile != null && localFile!!.exists()) {
            val newFile = FileUtils.rename(localFile!!, newName)
            if (newFile != null) {
                localFile = newFile
            } else {
                AlertDialog("Local File Rename Error", "Rename Error")
                return Pair(oldName, oldName)
            }
        }

        if (serverFile !== null) {
            val minimalNoteData: NoteData = ApiFunctions.rename(oldName, newName)
            if (minimalNoteData.status.contains("ERROR")) {
                // revert local file
                localFile = FileUtils.rename(localFile!!, newName)
                val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(minimalNoteData.status)!!.destructured
                AlertDialog(message, title)
            } else {
                name = newName
                serverFile = minimalNoteData
            }
        }
        return Pair(oldName, name)
    }

    fun delete() {
        if (localFile != null && localFile!!.exists()) {
            localFile?.delete()
        }
        if (serverFile != null) {
            val retNoteData = ApiFunctions.delete(serverFile!!.filename)
            println("delete status code = ${retNoteData.status}")
            if (retNoteData.status.contains("ERROR")) {
                val (title, message) = Regex("""ERROR: ([\w\s]+) - ([\w\s]+)""").find(retNoteData.status)!!.destructured
                AlertDialog(message, title)
                return
            }
        }
    }

}