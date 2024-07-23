package com.editor.backend

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@Serializable()
data class NoteModelData (
    var id: Int,
    // Change DB Schema to rename title to filename
    var filename: String,
    var created: Long,
    var modified: Long,
    var content: String?,
    var permission: Int,
    var status: String,
)

@Serializable
data class NoteResponse(
    var status: String,
)

@Service
class NoteService(val db: JdbcTemplate) {

    // Storing large notes in the database will be terribly slow.
    // Abstract that away and save notes either on disk object storage in the future)
    // We can just save them using the id as the filename, since it's guaranteed to have no duplicates
    // The title/client side filename will be fetched from the database
    // By this point, we should confirm that the user has write access to this note
    fun saveNoteToDisk(id: Int, content: String) {

    }

    // Read the note with given id from disk or object storage
    // By this point, we should have confirmed the user has access to read this
    fun readNoteFromDisk(id: Int): String {
        return ""
    }

    // NOTE: Notes in the future might be stored as a URL.
    // In this case, it is the job of the model to pull the data and set it as part of the 'content' field.

    // TODO: Address the edge case of returning 0 values
    fun getNoteByIdDB(id: Int): NoteModelData = db.query("SELECT * FROM notes WHERE id = ?", id) {
            rs, _ -> NoteModelData(rs.getInt("id"), rs.getString("filename"), rs.getTimestamp("created").time, rs.getTimestamp("modified").time, rs.getString("content"), -1, "Success.")}[0]

    // Error Check to make sure unique note names when creating new notes
    fun getNoteByTitleDB(userToken: String, noteFilename: String): String
    {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(NoteResponse("ERROR: User Error - User token is not valid."));
        }

        var noteData = db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, noteFilename)
        {
            rs, _ -> NoteModelData(
                rs.getInt("id"),
                rs.getString("filename"),
                rs.getTimestamp("created").time,
                rs.getTimestamp("modified").time,
                rs.getString("content"),
                rs.getInt("permission"),
                "Success."
            )
        }

        if (noteData.size < 1) {
            return Json.encodeToString(NoteResponse("ERROR: Note error - Note does not exist."));
        }
        else if (noteData.size > 1) {
            noteData[0].status = "WARNING: Duplicate notes with this name exist. Returning [0].";
        }

        return Json.encodeToString(noteData[0]);
    }

    fun getNotesListDB(userToken: String): String
    {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            var errorList: List<NoteResponse> = listOf<NoteResponse>(NoteResponse("ERROR: User Error - User token is not valid."));
            return Json.encodeToString(errorList);
        }

        // 3: If valid, get the list of notes
        var noteList = db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ?", userId)
        {
            rs, _ -> NoteModelData(
                rs.getInt("id"),
                rs.getString("filename"),
                rs.getTimestamp("created").time,
                rs.getTimestamp("modified").time,
                rs.getString("content"),
                rs.getInt("permission"),
         "Success."
            )
        }

        return Json.encodeToString(noteList);
    }


    fun updateDescriptionByTitleDB(userToken: String, filename: String, description: String): String {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(NoteResponse("ERROR: User Error - User token is not valid."));
        }

        // Get permission and check if permission < 2 - ie: the user has write access
        if (checkUserPermission(userId, filename) > 1)
        {
            return Json.encodeToString(NoteResponse("ERROR: Insufficient Permission - You only have read-only access to this note but tried to edit."));
        }


        // First, we need to check if the note we are saving to exists.
        // This can be done by using a COUNT(*) statement to count the number of rows returned by the select.
        var noteExists = 0;
        db.query("SELECT COUNT(*) FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, filename) {
                rs, _ -> noteExists = rs.getInt("COUNT(*)");
        }

        // If the note with that name does not exist, create it.
        if (noteExists == 0) {
            createNoteDB(userId, filename);
        }

        // Now, we are guaranteed a note with this name exists and we can proceed with saving the contents.
        val status = db.update("UPDATE notes INNER JOIN note_users ON note_users.note_id = notes.id SET notes.content = ? WHERE note_users.user_id = ? AND notes.filename = ?", description, userId, filename);

        if (status > 0)
        {
            var noteData = db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, filename)
            {
                rs, _ -> NoteModelData(
                rs.getInt("id"),
                rs.getString("filename"),
                rs.getTimestamp("created").time,
                rs.getTimestamp("modified").time,
                rs.getString("content"),
                rs.getInt("permission"),
                "Success."
                )
            }

            if (noteData.size > 1) {
                noteData[0].status = "WARNING: Duplicate notes with this name exist. Returning [0].";
            }

            return Json.encodeToString(noteData[0]);
        }

        return Json.encodeToString(NoteResponse("ERROR: Unknown Error - DB update did not occur, ask Tony to check server logs."));
    }

    // Error Check to make sure unique note names when creating new notes
    fun createNoteDB(userId: Int, filename: String): Int {
        val permission = Permissions.OWNER.level
        db.update("INSERT INTO notes (filename) VALUES (?)", filename)
        return db.update("INSERT INTO note_users (note_id, user_id, permission) VALUES (LAST_INSERT_ID(), ?, ?)", userId, permission)
    }

    fun checkUserPermission(userId: Int, filename: String): Int
    {
        var permission = -1;
        db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, filename)
        {
                rs, _ -> permission = rs.getInt("permission");
        }
        return permission;
    }

    fun deleteNoteByTitle(userToken: String, filename: String): String
    {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(NoteResponse("ERROR: User Error - User token is not valid."));
        }

        // First, we need to check if the note we are deleting exists.
        // 5. Use the original user ID to get the note ID
        var noteId = -1;
        db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, filename)
        {
                rs, _ -> noteId = rs.getInt("id");
        }

        if (noteId < 1)
        {
            return Json.encodeToString(NoteResponse("ERROR: Invalid Note - Specified note does not exist."));
        }

        // NOTE: ONLY ALLOW OWNERS TO DELETE. Shared users can not delete a shared note.
        if (checkUserPermission(userId, filename) != 0)
        {
            return Json.encodeToString(NoteResponse("ERROR: Insufficient Permission - Only owners can delete notes. It appears this note is shared with you, ask the owner to delete it."));
        }

        // This is a 2 phase delete.
        // First, we delete from note users, then, from notes.
        // Ideally, this should be a transaction. Or - we don't delete and just mark as deleted with a timestamp...
        //DELETE FROM note_users WHERE note_id = ? AND user_id = ?

        var status = db.update("DELETE FROM note_users WHERE note_id = ?", noteId);
        if (status < 1)
        {
            return Json.encodeToString(NoteResponse("ERROR: Deletion Error - Deleting user from the note failed.."));
        }

        status = db.update("DELETE FROM notes WHERE id = ?", noteId);
        if (status < 1)
        {
            return Json.encodeToString(NoteResponse("ERROR: Deletion Error - Deleting the note itself failed.."));
        }

        return Json.encodeToString(NoteResponse("Success?"));
    }

    fun renameNoteByTitle(userToken: String, oldName: String, newName: String): String
    {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(NoteResponse("ERROR: User Error - User token is not valid."));
        }

        // First, we need to check if the note we are saving to exists.
        // This can be done by using a COUNT(*) statement to count the number of rows returned by the select.
        var noteExists = 0;
        db.query("SELECT COUNT(*) FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, oldName) {
                rs, _ -> noteExists = rs.getInt("COUNT(*)");
        }

        // If the note with that name does not exist, create it.
        if (noteExists == 0) {
            return Json.encodeToString(NoteResponse("ERROR: Note Does Not Exist - Note with this name does not exist."));
        }

        // NOTE: ONLY ALLOW OWNERS TO DELETE. Shared users can not delete a shared note.
        if (checkUserPermission(userId, oldName) > 1)
        {
            return Json.encodeToString(NoteResponse("ERROR: Insufficient Permission - You don't have write access to rename this note. Ask the owner to check your permission level."));
        }

        // Now, we are guaranteed a note with this name exists and we can proceed with saving the contents.
        val status = db.update("UPDATE notes INNER JOIN note_users ON note_users.note_id = notes.id SET notes.filename = ? WHERE note_users.user_id = ? AND notes.filename = ?", newName, userId, oldName);

        if (status > 0)
        {
            return Json.encodeToString(NoteResponse("Success."));
        }

        return Json.encodeToString(NoteResponse("ERROR: Unknown Error - DB update did not occur, ask Tony to check server logs."));
    }


}




@RestController
class NoteController(val service: NoteService) {

//    // TODO: THIS ONE IS FOR DEBUGGING ONLY - REMOVE BEFORE RELEASE
//    // IT ACCESSES ANY NOTE WITHOUT ANY AUTHENTICATION/USER CHECKING
//    @GetMapping("/admin/getById")
//    fun getNoteById(noteId: Int): String {
//        return Json.encodeToString(service.getNoteByIdDB(noteId))
//    }

    @GetMapping("/notes/list")
    fun getNotesList(token: String): String
    {
        return service.getNotesListDB(token);
    }

    @GetMapping("/notes/get")
    fun getNoteByTitle(token: String, filename: String): String {
        return service.getNoteByTitleDB(token, filename);
    }

    @PostMapping("/notes/save")
    fun saveNote(token: String, filename: String, content: String): String {
        // This will create the note if a note with that name does not exist
        return service.updateDescriptionByTitleDB(token, filename, content);
    }

    @DeleteMapping("/notes/delete")
    fun createNote(token: String, filename: String): String {
        return service.deleteNoteByTitle(token, filename);
    }

    @PostMapping("/notes/rename")
    fun renameNote(token: String, oldFilename: String, newFilename: String): String {
        return service.renameNoteByTitle(token, oldFilename, newFilename);
    }


}