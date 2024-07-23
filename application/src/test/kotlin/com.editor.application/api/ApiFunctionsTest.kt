package com.editor.application.api

import org.junit.jupiter.api.Assertions

class ApiFunctionsTest {
    @org.junit.jupiter.api.Test
    fun register_INVALID_EMAIL() {
        // Invalid email addresses should return the following:
        // {"email":"","name":"","token":"","status":"ERROR: Registration error - Failed regex."}

        val expected = LoginData("", "", "", "ERROR: Registration error - Failed regex.");

        Assertions.assertEquals(ApiFunctions.register("invalid@e.m", "password", "name"), expected);
        Assertions.assertEquals(ApiFunctions.register("abcde", "password", "name"), expected);
        Assertions.assertEquals(ApiFunctions.register("$$$@***.**", "password", "name"), expected);
        Assertions.assertEquals(ApiFunctions.register("aabc@hostname", "password", "name"), expected);
    }

    @org.junit.jupiter.api.Test
    fun register_USER_EXISTS() {
        // If the user already exists, the following should be returned
        // {"email":"","name":"","token":"","status":"ERROR: Registration error - Duplicate account. Login to existing user."}

        val expected = LoginData("", "", "", "ERROR: Registration error - Duplicate account. Login to existing user.");

        Assertions.assertEquals(ApiFunctions.register("markdowney@example.com", "markdowney", "Mark Downey"), expected);
        Assertions.assertEquals(ApiFunctions.register("markdowney@example.com", "ignored", "Name Does Not Matter"), expected);
        Assertions.assertEquals(ApiFunctions.register("richtess@example.com", "hellp", "Rich Text"), expected);
        Assertions.assertEquals(ApiFunctions.register("a@b.cd", "abcd", "A Barely Connected Database"), expected);
    }

    @org.junit.jupiter.api.Test
    fun register_VALID_USER() {
        // If the registration is successful, the returned data will be identical to a login.
        // This is because successful registrations automatically log in users.

        // We need a random email to register!
        val uuid = java.util.UUID.randomUUID().toString();
        val email = uuid + "@example.com";

        val expected = LoginData(email, uuid, "", "SUCCESS");
        val test = ApiFunctions.register(email, "123", uuid)
        test.token = ""

        Assertions.assertEquals(test, expected);
    }

    @org.junit.jupiter.api.Test
    fun login_INVALID_EMAIL() {
        // Invalid email addresses should return the following:
        // {"email":"","name":"","token":"","status":"ERROR: Login Error - Email address is incorrectly formatted (failed regex)."

        val expected = LoginData("", "", "", "ERROR: Login Error - Email address is incorrectly formatted (failed regex).");
        Assertions.assertEquals(ApiFunctions.login("invalid@e.m", "password"), expected);
        Assertions.assertEquals(ApiFunctions.login("abcde", "password"), expected);
        Assertions.assertEquals(ApiFunctions.login("$$$@***.**", "password"), expected);
        Assertions.assertEquals(ApiFunctions.login("aabc@hostname", "password"), expected);
    }

    @org.junit.jupiter.api.Test
    fun login_NONEXISTENT_USER() {
        // If the user does not exist (is not register), we should return the following:
        // {"email":"","name":"","token":"","status":"ERROR: Login Error - Requested user email does not exist."
        val expected = LoginData("", "", "", "ERROR: Login Error - Requested user email does not exist.");
        Assertions.assertEquals(ApiFunctions.login("IDONTEXIST@example.com", "password"), expected);
    }

    @org.junit.jupiter.api.Test
    fun login_INVALID_PASSWORD() {
        // Invalid password should return the following:
        // {"email":"","name":"","token":"","status":"ERROR: Login Error - Incorrect password."
        val expected = LoginData("", "", "", "ERROR: Login Error - Incorrect password.");
        Assertions.assertEquals(ApiFunctions.login("markdowney@example.com", "invalid_password"), expected);
        Assertions.assertEquals(ApiFunctions.login("a@b.cd", "1234"), expected);
        Assertions.assertEquals(ApiFunctions.login("richtess@example.com", "markdowney@example.com"), expected);
    }

    @org.junit.jupiter.api.Test
    fun login_VALID_USER() {
        // If the registration is successful, the returned data will be identical to a login.
        // This is because successful registrations automatically log in users.
        // Since the tokens are randomly generated each login - we can not test those.

        val expected_mark_downey = LoginData("markdowney@example.com", "Mark Downey", "", "SUCCESS");
        val test_mark_downey = ApiFunctions.login("markdowney@example.com", "markdowney")
        test_mark_downey.token = ""
        Assertions.assertEquals(test_mark_downey, expected_mark_downey);

        val expected_rich_tess = LoginData("richtess@example.com", "Rich Tess", "", "SUCCESS");
        val test_rich_tess = ApiFunctions.login("richtess@example.com", "richtess")
        test_rich_tess.token = ""
        Assertions.assertEquals(test_rich_tess, expected_rich_tess);
    }

    @org.junit.jupiter.api.Test
    fun save_INVALID_USER_TOKEN() {
        // Saving before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""
        val expected = NoteData(status = "ERROR: User Error - User token is not valid.")
        val note = ApiFunctions.save("Tom_and_Jerry.md", "https://www.youtube.com/watch?v=GbPQj7k9tvQ")
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun save_SHARED_NOTE_USER_WITHOUT_WRITE_ACCESS() {
        // Saving before logging in should return the following:
        // ERROR: Insufficient Permission - You only have read-only access to this note but tried to edit.
        
        val email2 = "save_test_no_write_access_2@example.com"
        val password2 = "123"
        
        ApiFunctions.login(email2, password2)
        val expected = NoteData(status = "ERROR: Insufficient Permission - You only have read-only access to this note but tried to edit.")
        val note = ApiFunctions.save("no_write_access.md", "tried changing this but shouldn't be able to hopefully!")
        Assertions.assertEquals(note, expected)
    }
    
    @org.junit.jupiter.api.Test
    fun save_SAVE_AS_OWNER() {
        // Should be able to successfully save as the owner of the file
        ApiFunctions.login("w@x.yz", "wxyz")

        val curr_time = java.time.LocalDateTime.now()
        val expected = NoteData("Success.", 0, "testfile1.md", 0, 0, "$curr_time", 0)

        // testfile1.md already exists for this user, and is created by them
        val note = ApiFunctions.save("testfile1.md", "$curr_time")
        note.id = 0
        note.created = 0
        note.modified = 0
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun save_SAVE_AS_READ_WRITE() {
        // Should be able to successfully save

        val email2 = "save_test_read_write_2@example.com"
        val password2 = "123"

        ApiFunctions.login(email2, password2)
        val curr_time = java.time.LocalDateTime.now()
        val expected = NoteData("Success.", 0, "save_read_write.md", 0, 0, "$curr_time", 1)

        val note = ApiFunctions.save("save_read_write.md", "$curr_time")
        note.id = 0
        note.created = 0
        note.modified = 0
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun save_CREATE_NEW_NOTE() {
        ApiFunctions.login("bob@example.com", "bob")
        val uuid = java.util.UUID.randomUUID().toString()
        val expected = NoteData("Success.", 0, "Bob_the_builder_$uuid.md", 0, 0, "Hey bob! - Wendy", 0)
        val note = ApiFunctions.save("Bob_the_builder_$uuid.md", "Hey bob! - Wendy")
        note.id = 0
        note.created = 0
        note.modified = 0
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun open_INVALID_USER_TOKEN() {
        // Opening before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = NoteData(status = "ERROR: User Error - User token is not valid.")
        val note = ApiFunctions.open("The_Backyardigans.md")
        Assertions.assertEquals(expected, note)
    }

    @org.junit.jupiter.api.Test
    fun open_NONEXISTENT_NOTE() {
        // Opening before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.login("pablo@backyardigans.com", "pablo")
        val expected = NoteData(status = "ERROR: Note error - Note does not exist.")
        val note = ApiFunctions.open("Uniqua.md")
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun open_SUCCESS() {
        ApiFunctions.login("pablo@backyardigans.com", "pablo")
        val expected = NoteData(status = "Success.", id = 0, filename = "Tyrone.md", created = 0, modified =  0, content="# My Best Friend Is Actually Austin\n" +
                "\n", permission = 0)
        val note = ApiFunctions.open("Tyrone.md")
        note.id = 0
        note.created = 0
        note.modified = 0
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun rename_INVALID_USER_TOKEN() {
        // Renaming of notes before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = NoteData(status = "ERROR: User Error - User token is not valid.")
        val note = ApiFunctions.rename("Humungousaur.md", "Ultimate_Humungousaur.md")
        Assertions.assertEquals(expected, note)
    }

    @org.junit.jupiter.api.Test
    fun rename_NONEXISTENT_NOTE() {
        ApiFunctions.login("two_note_tony@example.com", "two_note_tony")
        val expected = NoteData(status = "ERROR: Note Does Not Exist - Note with this name does not exist.")
        val note = ApiFunctions.rename("three_note_tony.md", "four_note_tony.md")
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun rename_USER_NO_WRITE_ACCESS() {
        // share note tony only has READ_ONLY access
        ApiFunctions.login("share_note_tony@example.com", "share_note_tony")
        val expected = NoteData(status = "ERROR: Insufficient Permission - You don't have write access to rename this note. Ask the owner to check your permission level.")
        val note = ApiFunctions.rename("one_note_tony.md", "share_note_tony.md")
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun rename_SUCCESS() {
        ApiFunctions.login("bob@example.com", "bob")
        val uuid = java.util.UUID.randomUUID().toString()
        ApiFunctions.save("Bob_the_builder_$uuid.md", "Hey bob! - Wendy")
        val note = ApiFunctions.rename("Bob_the_builder_$uuid.md", "Bob_the_builder_renamed_$uuid.md")
        Assertions.assertEquals(note.status, "Success.")
    }

    @org.junit.jupiter.api.Test
    fun delete_INVALID_USER_TOKEN() {
        // Deletion of notes before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = NoteData(status = "ERROR: User Error - User token is not valid.")
        val note = ApiFunctions.delete("Homer.md")
        Assertions.assertEquals(expected, note)
    }


    @org.junit.jupiter.api.Test
    fun delete_NONEXISTENT_NOTE() {
        ApiFunctions.login("two_note_tony@example.com", "two_note_tony")
        val expected = NoteData(status = "ERROR: Invalid Note - Specified note does not exist.")
        val note = ApiFunctions.delete("three_note_tony.md")
        Assertions.assertEquals(note, expected)
    }


    @org.junit.jupiter.api.Test
    fun delete_USER_NOT_OWNER() {
        // share note tony only has READ_ONLY access
        ApiFunctions.login("share_note_tony@example.com", "share_note_tony")
        val expected = NoteData(status = "ERROR: Insufficient Permission - Only owners can delete notes. It appears this note is shared with you, ask the owner to delete it.")
        val note = ApiFunctions.delete("one_note_tony.md")
        Assertions.assertEquals(note, expected)
    }

    @org.junit.jupiter.api.Test
    fun delete_SUCCESS() {
        ApiFunctions.login("bob@example.com", "bob")
        val uuid = java.util.UUID.randomUUID().toString()
        ApiFunctions.save("Bob_the_builder_$uuid.md", "Hey bob! - Wendy")
        val note = ApiFunctions.delete("Bob_the_builder_$uuid.md")
        Assertions.assertEquals(note.status, "Success?")
    }

    @org.junit.jupiter.api.Test
    fun listNotes_INVALID_USER_TOKEN() {
        // Getting list of notes before logging in should return the following:
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = listOf(NoteData(status = "ERROR: User Error - User token is not valid."))
        val notes = ApiFunctions.listNotes()
        Assertions.assertEquals(expected, notes)
    }

    @org.junit.jupiter.api.Test
    fun listNotes_SUCCESS() {
        //two_note_tony@example.com
        ApiFunctions.login("two_note_tony@example.com", "two_note_tony")
        val expected = listOf(NoteData("Success.", 0, "one_note_tony.md", 0, 0, "# One Note Tony", 0),
                              NoteData("Success.", 0, "two_note_tony.md", 0, 0, "# Two Note Tony", 0))
        val notes = ApiFunctions.listNotes()
        notes.forEach {note ->
            note.id = 0
            note.created = 0
            note.modified = 0
        }
        Assertions.assertEquals(notes, expected)
    }

    @org.junit.jupiter.api.Test
    fun getShareList_NO_SHARE() {
        // share note tony only has READ_ONLY access
        ApiFunctions.login("two_note_tony@example.com", "two_note_tony")
        val expected = listOf<ShareData>(ShareData(0, "two_note_tony@example.com", 0))
        val users = ApiFunctions.getShareList("two_note_tony.md")
        users.forEach { shareUser ->
            shareUser.noteId = 0
        }
        Assertions.assertEquals(users, expected)

    }

    @org.junit.jupiter.api.Test
    fun getShareList_SUCCESS() {
        // share note tony only has READ_ONLY access
        ApiFunctions.login("share_note_tony@example.com", "share_note_tony")
        val expected = listOf<ShareData>(ShareData(0, "two_note_tony@example.com", 0), ShareData(0, "share_note_tony@example.com", 2))
        val users = ApiFunctions.getShareList("one_note_tony.md")
        users.forEach {shareUser ->
            shareUser.noteId = 0
        }
        Assertions.assertEquals(users, expected)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_INVALID_USER_TOKEN() {
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = "ERROR: User Error - User token is not valid."
        val share = ApiFunctions.shareNote("Looney_Tunes.md", "daffy@duck.com", 1)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_INVALID_PERMISSION() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid Permission - Permission must be 1 (R/W) or 2 (RO)"
        val share = ApiFunctions.shareNote("hunting_season.md", "daffy@duck.com", 3)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_INVALID_EMAIL() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid Email - Email address is invalid."
        val share = ApiFunctions.shareNote("hunting_season.md", "daffy@duc.k", 1)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_NONEXISTENT_USER() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid Email - User with email does not exist"
        val share = ApiFunctions.shareNote("hunting_season.md", "dafty@duck.com", 1)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_NONEXISTENT_FILE() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid note id - Note id for filename is not found."
        val share = ApiFunctions.shareNote("Hunting_Seasn.md", "daffy@duck.com", 1)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_INSUFFICIENT_PERMISSIONS() {
        ApiFunctions.login("marvin@themartian.com", "marvin the martian")
        val expected = "ERROR: Insufficient Permission - You don't have access to edit this share."
        val share = ApiFunctions.shareNote("hunting_season.md", "speedy@gonzales.com", 1)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_SUCCESS_NEW_SHARE() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")

        val uuid = java.util.UUID.randomUUID().toString()
        ApiFunctions.save("Attack of the drones $uuid.md", "TAKE COVER!!!!")
        val expected = "Success."
        val share = ApiFunctions.shareNote("Attack of the drones $uuid.md", "speedy@gonzales.com", 2)
        Assertions.assertEquals(expected, share)
    }

    @org.junit.jupiter.api.Test
    fun shareNote_SUCCESS_EDIT_SHARE() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "Success."
        val share = ApiFunctions.shareNote("hunting_season.md", "daffy@duck.com", 1)
        Assertions.assertEquals(expected, share)
    }


    @org.junit.jupiter.api.Test
    fun unshareNote_INVALID_USER_TOKEN() {
        // "ERROR: User Error - User token is not valid."

        // Delete the usertoken from any previous test
        ApiFunctions.userToken = ""

        val expected = "ERROR: User Error - User token is not valid."
        val unshare = ApiFunctions.unshareNote("Looney_Tunes.md", "daffy@duck.com")
        Assertions.assertEquals(expected, unshare)
    }

    @org.junit.jupiter.api.Test
    fun unshareNote_INVALID_EMAIL() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid Email - Email address is invalid."
        val unshare = ApiFunctions.unshareNote("hunting_season.md", "daffy@duc.k")
        Assertions.assertEquals(expected, unshare)
    }

    @org.junit.jupiter.api.Test
    fun unshareNote_NONEXISTENT_USER() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid Email - User with email does not exist"
        val unshare = ApiFunctions.unshareNote("hunting_season.md", "dafty@duck.com")
        Assertions.assertEquals(expected, unshare)
    }

    @org.junit.jupiter.api.Test
    fun unshareNote_NONEXISTENT_FILE() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")
        val expected = "ERROR: Invalid note id - Note id for filename is not found."
        val unshare = ApiFunctions.unshareNote("Hunting_Seasn.md", "daffy@duck.com")
        Assertions.assertEquals(expected, unshare)
    }

    @org.junit.jupiter.api.Test
    fun unshareNote_INSUFFICIENT_PERMISSIONS() {
        ApiFunctions.login("marvin@themartian.com", "marvin the martian")
        val expected = "ERROR: Insufficient Permission - You don't have access to edit this share."
        val unshare = ApiFunctions.unshareNote("hunting_season.md", "speedy@gonzales.com")
        Assertions.assertEquals(expected, unshare)
    }

    @org.junit.jupiter.api.Test
    fun unshareNote_SUCCESS() {
        ApiFunctions.login("bugs@bunny.com", "bugs bunny")

        val uuid = java.util.UUID.randomUUID().toString()
        ApiFunctions.save("Attack of the drones $uuid.md", "TAKE COVER!!!!")
        val expected = "Success."
        val share = ApiFunctions.shareNote("Attack of the drones $uuid.md", "speedy@gonzales.com", 1)
        val unshare = ApiFunctions.unshareNote("Attack of the drones $uuid.md", "speedy@gonzales.com")
        Assertions.assertEquals(expected, unshare)
    }
}