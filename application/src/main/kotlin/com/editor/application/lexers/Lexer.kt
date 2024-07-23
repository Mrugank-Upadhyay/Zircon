package com.editor.application.lexers

interface Lexer {
    fun parse(text: String, offset: Int, tokens: MutableList<Token>)
}