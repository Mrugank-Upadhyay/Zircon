package com.editor.application.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class NoteData (
    var status: String = "",
    var id: Int = -1,
    var filename: String = "",
    var created: Long = 0,
    var modified: Long = 0,
    var content: String = "",
    var permission: Int = 0
)


@Serializable
data class UserData (
    var email: String,
    var password: String,
)

@Serializable
data class LoginData (
    var email: String,
    var name: String,
    var token: String,
    var status: String,
)

@Serializable
data class ShareData (
    var noteId: Int,
    // Set it back to shareEmail once tony has changed it
    var shareEmail: String,
    var permission: Int,
)
@Serializable
data class ShareReturn(
    val shareEmail: String,
    val sharePermission: Int,
    val status: String,
)


object ApiFunctions {

//    private val serverUrl: String = "https://editor.tonytascioglu.com"
    private const val serverUrl: String = "https://beta.tonytascioglu.com"
    var userToken: String = ""
    var name: String = ""
    var email: String = ""

    fun login(email: String, password: String): LoginData {
        val params = listOf("email" to email, "password" to password)
        return runBlocking {
            val (_, _, result) = Fuel.post("$serverUrl/users/login", params).awaitStringResponseResult()
            println(result.get())
            val loginData: LoginData = Json.decodeFromString(result.get())
            println(loginData.toString())
            userToken = loginData.token
            name = loginData.name
            ApiFunctions.email = email
            return@runBlocking loginData
        }

    }

    fun register(email: String, password: String, name: String): LoginData {
        val params = listOf("email" to email, "password" to password, "name" to name)
        return runBlocking {
            val (_, _, result) = Fuel.post("$serverUrl/users/register", params).awaitStringResponseResult()
            println(result.get())
            val loginData: LoginData = Json.decodeFromString(result.get())
            println(loginData.toString())
            userToken = loginData.token
            ApiFunctions.name = loginData.name
            return@runBlocking loginData
        }
    }

    // saveNote
     fun save(filename: String, contents: String): NoteData {
        val params = listOf("token" to userToken, "filename" to filename, "content" to contents)
        return  runBlocking {
            // TODO: Ensure correct error checking for NoteData.status where save is called
            val (_, _, result) = Fuel.post("$serverUrl/notes/save", params).awaitStringResponseResult()
            return@runBlocking Json.decodeFromString(result.get())
        }
    }

    // getNote
    fun open(filename: String): NoteData {
        val params = listOf("token" to userToken, "filename" to filename)
        return runBlocking {
            val (_, _, result) = Fuel.get("$serverUrl/notes/get", params).awaitStringResponseResult()
            val noteData: NoteData = Json.decodeFromString(result.get())
            println("noteData: \n $noteData")
            // TODO Instead of returning null, have this be handled by the front end UI by using the Alert Dialog or some other method
            return@runBlocking noteData
        }
    }

    fun rename(oldFilename: String, newFilename: String): NoteData {
        val params = listOf("token" to userToken, "oldFilename" to oldFilename, "newFilename" to newFilename)
        return runBlocking {
            val (_, _, result) = Fuel.post("$serverUrl/notes/rename", params).awaitStringResponseResult()
            return@runBlocking Json.decodeFromString<NoteData>(result.get())
        }
    }

    fun delete(filename: String): NoteData {
        val params = listOf("token" to userToken, "filename" to filename)
        return runBlocking {
            val (_, _, result) = Fuel.delete("$serverUrl/notes/delete", params).awaitStringResponseResult()
            return@runBlocking Json.decodeFromString<NoteData>(result.get())
        }
    }

    fun listNotes(): List<NoteData> {
        val params = listOf("token" to userToken)
        return runBlocking {
            val (_, _, result) = Fuel.get("$serverUrl/notes/list", params).awaitStringResponseResult()
            val noteList: List<NoteData> = Json.decodeFromString(result.get())
            println("notelist: \n $noteList")
            return@runBlocking noteList
        }
    }


    // Shares
    fun getShareList(filename: String): List<ShareData> {
        val params = listOf("token" to userToken, "filename" to filename)
        return runBlocking {
            val (_, _, result) = Fuel.get("$serverUrl/shares/list", params).awaitStringResponseResult()
            println(result.get())
            return@runBlocking Json.decodeFromString<List<ShareData>>(result.get())
        }
    }

    fun shareNote(filename: String, shareEmail: String, permission: Int): String {
        val params = listOf("token" to userToken, "filename" to filename, "shareEmail" to shareEmail, "permission" to permission)
        return runBlocking {
            val (_, _, result) = Fuel.post("$serverUrl/shares/share", params).awaitStringResponseResult()
            val status: String = Json.decodeFromString<ShareReturn>(result.get()).status
            return@runBlocking status
        }
    }

    fun unshareNote(filename: String, shareEmail: String): String {
        val params = listOf("token" to userToken, "filename" to filename, "shareEmail" to shareEmail)
        return runBlocking {
            val (_, _, result) = Fuel.delete("$serverUrl/shares/unshare", params).awaitStringResponseResult()
            val status: String = Json.decodeFromString<ShareReturn>(result.get()).status
            return@runBlocking status
        }
    }
}