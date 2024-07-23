package com.editor.backend

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mindrot.jbcrypt.BCrypt
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

data class UserData (
    var id: Int,
    var email: String,
    var password: String,
    var name: String,
)

@Serializable()
data class LoginData (
    var email: String,
    var name: String,
    var token: String,
    var status: String
)

// Pseudo in-memory-database to store user tokens
// We need to use tokens and not user IDs since we don't want users to be able to make
// requests for other users notes. Brute forcing sequential IDs is trivial. 256/512 bit
// random digits is not, so we can rely on using this to communicate with the client
// TODO: Switch to Spring Security to do this in a more "proper" way and support OAuth
object UserAuth {
    var userTokens: HashMap<Int, String> = HashMap<Int, String>()
    var tokenUsers: HashMap<String, Int> = HashMap<String, Int>()

    // We need to be able to convert a user token to a user id and check if valid
    fun userTokenToId(token: String): Int {
        //println(tokenUsers);

        if (!tokenUsers.containsKey(token)) {
            // User is not signed in. Return a more meaningful error in the controller
            // telling the client to re-authenticate. (probably a try-catch there)
            return -1;
        }

        // Else, the user is signed in.
        return tokenUsers.getValue(token);
//        return UserAuth.tokenUsers.get(token);
    }
}

@Service
class UserService(val db: JdbcTemplate) {

    fun createToken(userId: Int): String {
        // If the user already has a token - we need to save it so we can delete it from tokenUsers
        if (UserAuth.userTokens.containsKey(userId)) {
            UserAuth.tokenUsers.remove(UserAuth.userTokens.get(userId));
        }

        // Create a new token for the user TODO: Use SHA256 or something
        //val token = Base64.getEncoder().encode(Random.nextBytes(64)).toString();
        val token = java.util.UUID.randomUUID().toString();

        // Insert the token with the given ID in userTokens
        UserAuth.userTokens[userId] = token;

        // Insert the new token in tokenUsers
        UserAuth.tokenUsers[token] = userId;

        return token;
    }

    fun checkAuthDB(email: String, password: String): String {

        // As a precaution - run the email through a regex to ensure it is a real email
        val emailRegex: Regex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        if (!emailRegex.matches(email)) {
            return Json.encodeToString(LoginData("", "", "", "ERROR: Login Error - Email address is incorrectly formatted (failed regex)."));
        }

        // First, ensure this user actually exists
        // Email is indexed with HASH on the DB, so this should be an O(1) operation
        var userExists = 0;
        db.query("SELECT COUNT(*) FROM users WHERE email  = ?", email) { rs, _ ->
            userExists = rs.getInt("COUNT(*)");
        }

        if (userExists == 0) {
            return Json.encodeToString(LoginData("", "", "", "ERROR: Login Error - Requested user email does not exist."));
        }

        // Next, get the hashed password from the database for the user
        var user: UserData = db.query("SELECT * FROM users WHERE email = ?", email) {
                rs, _ -> UserData(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getString("name"))}[0]


        // Next, use bcrypt to compare whether it matches
        if (BCrypt.checkpw(password, user.password)) {
            // If user password is correct, get a token for the user from createToken after returning
            println("Password matched!");

            val token = createToken(user.id);

            return Json.encodeToString(LoginData(user.email, user.name, token, "SUCCESS"));


        } else {
            // If user did not exist, or password is invalid - return an error
            println("Invalid password!");

            return Json.encodeToString(LoginData("", "", "", "ERROR: Login Error - Incorrect password."));
        }

        return Json.encodeToString(LoginData("", "", "", "ERROR: Login Error - Unknown error occured?!?"));
    }
    fun createUserDB(emailOrig: String, password: String, name: String): Int {
        val email = emailOrig.lowercase();

        // As a precaution - run the email through a regex to ensure it is a real email
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

        if (!emailRegex.matches(email)) {
            return -1;  // somebody tried a non-email
        }

        // First, we need to ensure this user does not already exist
        var userExists = 0;
        db.query("SELECT COUNT(*) FROM users WHERE email  = ?", email) { rs, _ ->
            userExists = rs.getInt("COUNT(*)");
        }

        if (userExists != 0) {
            return -2;  // Duplicate user
        }

        // Next, we need to hash and salt the password so we don't store it in plaintext
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Finally, insert all of this into the database
        db.update("INSERT INTO users (email, password, type, name) VALUES (?, ?, ?, ?)", email, hashedPassword, 1, name);
        return 0;
    }

}

@RestController
class UserController(val service: UserService) {

    @PostMapping("/users/register")
    fun registerUser(email: String, password: String, name: String): String {
        // Create a new account for the user
        val status = service.createUserDB(email, password, name);
        if (status == -1) {
            return Json.encodeToString(LoginData("", "", "", "ERROR: Registration error - Failed regex."));
        }
        else if (status == -2)
        {
            return Json.encodeToString(LoginData("", "", "", "ERROR: Registration error - Duplicate account. Login to existing user."));
        }

        // Log them in so they don't need to do that separately
        return service.checkAuthDB(email, password);
    }

    @PostMapping("/users/login")
    fun loginUser(email: String, password: String): String {
        // Return JSON containing the code (success/fail), auth token if success
        val status = service.checkAuthDB(email, password);

        // For now, just the authentication
        return status;
    }

    @GetMapping("/users/load")
    fun loadUserPrefs(token: String): String {
        //TODO
        return "";
    }

    @PostMapping("/users/save")
    fun saveUserPrefs(token: String, preferences: String): String {
        //TODO
        return "";
    }

}