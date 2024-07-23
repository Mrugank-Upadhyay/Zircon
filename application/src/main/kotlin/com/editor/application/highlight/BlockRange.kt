package com.editor.application.highlight

data class BlockRange(
    val styleClasses: List<String>,
    override val start: Int,
    override val end: Int,
    val startLine: Int,
    val endLine: Int,
    val styleRanges: List<StyleRange>
): Range