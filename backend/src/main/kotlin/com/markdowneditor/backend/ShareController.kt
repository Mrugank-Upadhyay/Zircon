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
data class ShareData (
    var shareEmail: String,
    var sharePermission: Int,
    var status: String
)

@Serializable
data class NoteUserData(
    var noteId: Int,
    var shareEmail: String,
    var permission: Int,
)

@Service
class ShareService(val db: JdbcTemplate)
{

    fun listShareDB(userToken: String, noteTitle: String): String
    {
        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(NoteUserData(-1, "", -1));
        }

        // 5. Use the original user ID to get the note ID
        var noteId = -1;
        db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, noteTitle)
        {
                rs, _ -> noteId = rs.getInt("id");
        }

        // Use note ID to get all users for that note
        val data = db.query("SELECT * FROM note_users INNER JOIN users ON note_users.user_id = users.id WHERE note_users.note_id = ?", noteId)
        {
            rs, _ -> NoteUserData(rs.getInt("note_id"), rs.getString("email").lowercase(), rs.getInt("permission"));
        }

        return Json.encodeToString(data);
    }

    fun shareNoteDB(noteTitle: String, userToken: String, emailOrig: String, sharePermission: Int): String
    {
        val shareEmail = emailOrig.lowercase();

        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(ShareData("", -1, "ERROR: User Error - User token is not valid."));
        }

        // 2b. Check if permission valid
        if (sharePermission < 1 || sharePermission > 2)
        {
            return Json.encodeToString(ShareData("", -1, "ERROR: Invalid Permission - Permission must be 1 (R/W) or 2 (RO)"));
        }

        // 3. Convert share email to id
        // 3a. Check the email validity using the regex
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        if (!emailRegex.matches(shareEmail))
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Email - Email address is invalid."));
        }

        // 4. Check if user valid
        // 4a. Check if user exists
        var userExists = 0;
        db.query("SELECT COUNT(*) FROM users WHERE email  = ?", shareEmail)
        {
                rs, _ -> userExists = rs.getInt("COUNT(*)");
        }

        if (userExists == 0)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Email - User with email does not exist"));
        }

        var shareUserId = -1;
        db.query("SELECT id FROM users WHERE email = ?", shareEmail)
        {
                rs, _ -> shareUserId = rs.getInt("id");
        }

        if (shareUserId < 1)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Share User - Share user id is not found?"));
        }

        // 5. Use the original user ID to get the note ID
        var noteId = -1;
        db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, noteTitle)
        {
                rs, _ -> noteId = rs.getInt("id");
        }

        if (noteId < 1)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid note id - Note id for filename is not found."));
        }

        // Check is user has access to alter permissions
        var userPermission = 0;
        db.query("SELECT * FROM note_users WHERE note_id  = ? AND user_id = ?", noteId, userId)
        { rs, _ ->
            userPermission = rs.getInt("permission");
        }

        if (userPermission > 1)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Insufficient Permission - You don't have access to edit this share."));
        }

        // 6. Check if share already exists
        var shareExists = 0;
        db.query("SELECT COUNT(*) FROM note_users WHERE note_id  = ? AND user_id = ?", noteId, shareUserId)
        { rs, _ ->
            shareExists = rs.getInt("COUNT(*)");
        }

        var status: Int;

        // 7. If not, create with permission
        if (shareExists == 0)
        {
            status = db.update("INSERT INTO note_users (note_id, user_id, permission) VALUES (?, ?, ?)", noteId, shareUserId, sharePermission);
        }

        // 8. If yes, then alter the permission
        else
        {
            status = db.update("UPDATE note_users SET permission = ? WHERE note_id = ? AND user_id = ?", sharePermission, noteId, shareUserId);
        }

        if (status > 0)
        {
            return Json.encodeToString(ShareData(shareEmail, sharePermission, "Success."));
        }

        // 9. (Optional feature) - Email user when shared with them?
        // I won't run a mail server - it would end up in spam. Instead, use an actual service.


        // Note - we need actual user IDs to avoid emailing @example.com

        return "";
    }
    fun deleteShareDB(noteTitle: String, userToken: String, emailOrig: String): String
    {
        val shareEmail = emailOrig.lowercase();

        // 1: Convert token to ID
        val userId = UserAuth.userTokenToId(userToken);

        // 2: Check if valid
        if (userId < 0)
        {
            return Json.encodeToString(ShareData("", -1, "ERROR: User Error - User token is not valid."));
        }

        // 3. Convert share email to id
        // 3a. Check the email validity using the regex
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        if (!emailRegex.matches(shareEmail))
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Email - Email address is invalid."));
        }

        // 4. Check if user valid
        // 4a. Check if user exists
        var userExists = 0;
        db.query("SELECT COUNT(*) FROM users WHERE email  = ?", shareEmail)
        {
                rs, _ -> userExists = rs.getInt("COUNT(*)");
        }

        if (userExists == 0)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Email - User with email does not exist"));
        }

        var shareUserId = -1;
        db.query("SELECT id FROM users WHERE email = ?", shareEmail)
        {
                rs, _ -> shareUserId = rs.getInt("id");
        }

        if (shareUserId < 1)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid Share User - Share user id is not found?"));
        }

        // 5. Use the original user ID to get the note ID
        var noteId = -1;
        db.query("SELECT * FROM notes INNER JOIN note_users ON note_users.note_id = notes.id WHERE note_users.user_id  = ? AND notes.filename = ?", userId, noteTitle)
        {
                rs, _ -> noteId = rs.getInt("id");
        }

        if (noteId < 1)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Invalid note id - Note id for filename is not found."));
        }

        // Check is user has access to alter permissions
        var userPermission = 0;
        db.query("SELECT * FROM note_users WHERE note_id  = ? AND user_id = ?", noteId, userId) { rs, _ ->
            userPermission = rs.getInt("permission");
        }

        if (userPermission > 1 && shareUserId != userId)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "ERROR: Insufficient Permission - You don't have access to edit this share."));
        }

        // 6. Check if share already exists
        var shareExists = 0;
        db.query("SELECT COUNT(*) FROM note_users WHERE note_id  = ? AND user_id = ?", noteId, shareUserId) { rs, _ ->
            shareExists = rs.getInt("COUNT(*)");
        }

        var status: Int = 0;

        // 7. If not, delete
        if (shareExists != 0)
        {
            status = db.update("DELETE FROM note_users WHERE note_id = ? AND user_id = ?", noteId, shareUserId);
        }

        if (status > 0)
        {
            return Json.encodeToString(ShareData(shareEmail, -1, "Success."));
        }

        // 9. (Optional feature) - Email user when shared with them?
        // I won't run a mail server - it would end up in spam. Instead, use an actual service.

        return "";
    }

}

@RestController
class ShareController(val service: ShareService)
{

    @GetMapping("/shares/list")
    fun listShare(token: String, filename: String): String
    {
        return service.listShareDB(token, filename);
    }

    @PostMapping("/shares/share")
    fun shareNote(token: String, filename: String, shareEmail: String, permission: Int): String
    {
        return service.shareNoteDB(filename, token, shareEmail, permission);
    }

    @DeleteMapping("/shares/unshare")
    fun deleteShare(token: String, filename: String, shareEmail: String): String
    {
        return service.deleteShareDB(filename, token, shareEmail);
    }

}