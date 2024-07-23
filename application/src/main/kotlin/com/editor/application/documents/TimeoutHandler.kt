package com.editor.application.documents

import javafx.application.Platform
import java.util.*

class TimeoutHandler {
    private var timer: Timer? = null
    fun setTimeout(delay: Long, task: () -> Unit) {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                Platform.runLater {
                    task()
                }
            }
        }, delay)
    }
    fun clearTimeout() {
        timer?.cancel()
        timer = null
    }
}