package com.editor.application.highlight

data class StyleRange(val styleClasses: List<String>, override val start: Int, override val end: Int): Range