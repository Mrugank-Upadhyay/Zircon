package com.editor.application.highlight

import com.editor.application.lexers.JavaScriptLexer
import com.editor.application.lexers.PythonLexer
import com.editor.application.lexers.Token
import com.editor.application.lexers.XHTMLLexer
import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler

// Want visit blocks -> inside blocks
class HighlighterVisitor {
    var blockRanges: MutableList<BlockRange> = mutableListOf()

    private val visitor = NodeVisitor(
        VisitHandler(Heading::class.java, this::visit),
        VisitHandler(HtmlBlock::class.java, this::visit),
        VisitHandler(HtmlCommentBlock::class.java, this::visit),
        VisitHandler(IndentedCodeBlock::class.java, this::visit),
        VisitHandler(FencedCodeBlock::class.java, this::visit),
        VisitHandler(BulletList::class.java, this::visit),
        VisitHandler(BulletListItem::class.java, this::visit),
        VisitHandler(OrderedList::class.java, this::visit),
        VisitHandler(OrderedListItem::class.java, this::visit),
        VisitHandler(Paragraph::class.java, this::visit),
        VisitHandler(Reference::class.java, this::visit),
        VisitHandler(ThematicBreak::class.java, this::visit),
        VisitHandler(BlockQuote::class.java, this::visit),
    )

    fun highlightCode(offset: Int, code: String, language: String): MutableList<StyleRange> {
        val lexer = when (language) {
            "javascript" -> JavaScriptLexer()
            "python" -> PythonLexer()
            "html" -> XHTMLLexer()
            else -> null
        }
        val tokens = mutableListOf<Token>()
        lexer?.parse(code, offset, tokens)
        return tokens.map { token -> StyleRange(
            listOf("code-" + token.type.toString().lowercase()),
            token.start,
            token.end
        )
        }.toMutableList()
    }

    fun visit(node: Heading) {
        add("heading${node.level}", node)
    }

    fun visit(node: HtmlBlock) {
        val styleRanges = highlightCode(node.startOffset, node.chars.unescape(), "html")
        add("html", node, styleRanges)
    }

    fun visit(node: HtmlCommentBlock) {
        add(listOf("html-comment", "html"), node)
    }
    fun visit(node: IndentedCodeBlock) {
        add("indented-code-block", node, mutableListOf())
    }

    fun visit(node: FencedCodeBlock) {
        val styleRanges = highlightCode(node.childChars.startOffset, node.childChars.unescape(), node.info.unescape())
        add("fenced-code-block", node, styleRanges)
    }

    fun visit(node: BulletList) {
        add("bullet-list", node)
    }

    fun visit(node: BulletListItem) {
        add("bullet-list-item", node)
    }

    fun visit(node: OrderedList) {
        add("ordered-list", node)
    }

    fun visit(node: OrderedListItem) {
        add("ordered-list-item", node)
    }

    fun visit(node: Paragraph) {
        add("paragraph", node)
    }

    fun visit(node: Reference) {
        add("reference", node)
    }

    fun visit(node: ThematicBreak) {
        add("break", node)
    }

    fun visit(node: BlockQuote) {
        add("block-quote", node)
    }

    fun visit(node: Document) {
        visitor.visit(node)
    }

    fun add(styleClasses: List<String>, node: Node, styleRanges: MutableList<StyleRange>? = null) {
        val inlineVisitor = InlineVisitor()
        if (styleRanges == null) {
            inlineVisitor.visit(node)
        }
        blockRanges.add(
            BlockRange(
                styleClasses,
                node.startOffset,
                node.endOffset,
                node.startLineNumber,
                node.endLineNumber,
                styleRanges ?: inlineVisitor.styleRanges
            )
        )
    }

    fun add(styleClass: String, node: Node, styleRanges: MutableList<StyleRange>? = null) {
        add(listOf(styleClass), node, styleRanges)
    }
}