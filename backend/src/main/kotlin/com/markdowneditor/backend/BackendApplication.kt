package com.editor.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class BackendApplication

enum class Permissions(val level: Int) {
    OWNER(0),
    READ_WRITE(1),
    READ_ONLY(2),
}


fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args) {
        // Will be :80 on the final server (:443 with HTTPS) using NginX as a reverse proxy.
        // (8080 and 8081 are already used on Tony's server, so we're using 8082 for now)
        setDefaultProperties(mapOf("server.port" to "8083"))}
        println("Welcome to MarkDown Editor backend on :8083")
}
