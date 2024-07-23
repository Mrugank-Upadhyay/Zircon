package com.editor.application.highlight

import com.vladsch.flexmark.ast.Code
import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.sequence.BasedSequence

class InlineVisitor {
    var styleRanges: MutableList<StyleRange> = mutableListOf()
    private val visitor = NodeVisitor(
        VisitHandler(StrongEmphasis::class.java, this::visit),
        VisitHandler(Emphasis::class.java, this::visit),
        VisitHandler(Link::class.java, this::visit),
        VisitHandler(Code::class.java, this::visit)
    )

    fun visit(node: StrongEmphasis) {
        add("bold", node)
//        add("delimiter", node.openingMarker)
//        add("delimiter", node.closingMarker)
    }

    fun visit(node: Emphasis) {
        add("italic", node)
//        add("delimiter", node.openingMarker)
//        add("delimiter", node.closingMarker)
    }

    fun visit(node: Link) {
        add("link", node)
    }

    fun visit(node: Code) {
        add("code", node)
//        add("delimiter", node.openingMarker)
//        add("delimiter", node.closingMarker)
    }

    fun visit(node: Node) {
        visitor.visit(node)
    }

    fun add(styleClasses: List<String>, node: Node) {
        addWithoutOverlap(StyleRange(styleClasses, node.startOffset, node.endOffset))
        visitor.visitChildren(node)
    }

    fun add(styleClasses: List<String>, basedSequence: BasedSequence) {
        val start = basedSequence.startOffset
        val endOffset = basedSequence.endOffset
        addWithoutOverlap(StyleRange(styleClasses, basedSequence.startOffset, basedSequence.endOffset))
    }

    fun add(styleClass: String, node: Node) {
        add(listOf(styleClass), node)
    }

    fun add(styleClass: String, basedSequence: BasedSequence) {
        add(listOf(styleClass), basedSequence)
    }

    private fun addWithoutOverlap(sr: StyleRange) {
        // Conditionally splits existing style ranges that overlap with the style range being added
        fun mapFunction(existing: StyleRange): Sequence<StyleRange> {
            // Case 1: `existing` does not overlap with `sr` so simply give back `existing`
            if (existing.end < sr.start || sr.end < existing.start) {
                return sequenceOf(existing)
            }
            // Case 2: `sr` fully contains `existing`. Combine the style classes for both.
            else if (sr.contains(existing)) {
                return sequenceOf(StyleRange(existing.styleClasses + sr.styleClasses, existing.start, existing.end))
            }
            // Case 3: `existing` contains `sr`. Will need to figure out correct way to split from here.
            else if (existing.contains(sr)) {
                // Case 3.1: `sr` start matches `existing` start. Split into 2 ranges. 1st having combined style classes
                if (existing.start == sr.start) {
                    return sequenceOf(
                        StyleRange(existing.styleClasses + sr.styleClasses, existing.start, sr.end),
                        StyleRange(existing.styleClasses, sr.end, existing.end)
                    )
                }
                // Case 3.2: `sr` end matches `existing` end. Split into 2 ranges. 2nd having combined style classes.
                else if (existing.end == sr.end) {
                    return sequenceOf(
                        StyleRange(existing.styleClasses, existing.start, sr.start),
                        StyleRange(existing.styleClasses + sr.styleClasses, sr.start, existing.end)
                    )
                }
                // Case 3.3: `sr` is within `existing` and does not touch its boundaries. Split into 3 ranges. 2nd has combined style classes.
                else {
                    return sequenceOf(
                        StyleRange(existing.styleClasses, existing.start, sr.start),
                        StyleRange(existing.styleClasses + sr.styleClasses, sr.start, sr.end),
                        StyleRange(existing.styleClasses, sr.end, existing.end)
                    )
                }
            }
            // Case 4: Only `sr` start is in `existing`. Split into 2 ranges. 2nd having combined style classes.
            else if (sr.start < existing.end) {
                return sequenceOf(
                    StyleRange(existing.styleClasses, existing.start, sr.start),
                    StyleRange(existing.styleClasses + sr.styleClasses, sr.start, existing.end)
                )
            }
            // Case 5: Only `sr` end is in `existing`. Split into 2 ranges. 1st having combined style classes.
            else if (existing.end < sr.end) {
                return sequenceOf(
                    StyleRange(existing.styleClasses + sr.styleClasses, existing.start, sr.end),
                    StyleRange(existing.styleClasses, sr.end, existing.end)
                )
            }
            return emptySequence()
        }

        if (styleRanges.isEmpty()) {
            styleRanges.add(sr)
            return
        }

        val start = styleRanges.first().start
        val end = styleRanges.last().end
        if (sr.end < start) {
            styleRanges.add(0, sr)
        }
        else if (sr.start > end) {
            styleRanges.add(sr)
        }
        else {
            styleRanges = styleRanges.flatMap{mapFunction(it)}.toMutableList()
        }
    }
}