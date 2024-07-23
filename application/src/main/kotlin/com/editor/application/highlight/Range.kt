package com.editor.application.highlight

interface Range {
    val start: Int
    val end: Int

    val length get() = end - start

    fun contains(other: Range): Boolean {
        return start <= other.start && other.end <= end
    }
    fun contains(pos: Int): Boolean {
        return pos in start..end
    }
}