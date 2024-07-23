package com.editor.application.highlight

import com.vladsch.flexmark.test.util.AstCollectingVisitor
import com.vladsch.flexmark.util.ast.Node
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.vladsch.flexmark.parser.Parser as FlexMarkParser

class Highlighter {
    private val parser = FlexMarkParser.builder().build()
    var doc = parser.parse("")

    fun parse(input: String): List<BlockRange> {
        doc = parser.parse(input)
        val visitor = HighlighterVisitor()
        visitor.visit(doc)
        return visitor.blockRanges
    }

    fun caretNodes(caretPosition: Int): List<Node> {
        val visitor = CaretNodesVisitor(caretPosition)
        visitor.visit(doc)
        return visitor.nodes
    }

    @OptIn(FlowPreview::class)
    fun printAst(input: Node) = runBlocking {
        launch {
            flow {
                emit(AstCollectingVisitor().collectAndGetAstText(input))
            }.debounce(300).collect{ value -> println(value) }
        }
    }
}