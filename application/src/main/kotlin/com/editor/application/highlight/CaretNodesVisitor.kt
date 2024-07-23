package com.editor.application.highlight

import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.util.ast.*

class CaretNodesVisitor(val caretPosition: Int) {
    var nodes: MutableList<Node> = mutableListOf()
    private val visitor = NodeVisitor(
        VisitHandler(Heading::class.java, this::visit),
        VisitHandler(StrongEmphasis::class.java, this::visit),
        VisitHandler(Emphasis::class.java, this::visit),
    )

    fun visit(node: Document) {
        visitor.visit(node)
    }

    fun visit(node: Node) {
        if (caretPosition in node.startOffset .. node.endOffset) {
            nodes.add(node)
            visitor.visitChildren(node)
        }
    }
}