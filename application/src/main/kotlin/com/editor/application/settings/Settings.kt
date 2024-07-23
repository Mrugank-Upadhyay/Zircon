package com.editor.application.settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(var theme: String = "light")