package com.editor.application.lexers

data class Token(
    val type: TokenType,
    val start: Int,
    val length: Int,
    val pairValue: Byte = 0
) {
    constructor(type: TokenType, start: Int, length: Int) : this(type, start, length, 0)

    override fun hashCode(): Int {
        return start
    }
    val end get() = start + length

}