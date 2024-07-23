package com.editor.application.documents

import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import org.fxmisc.richtext.GenericStyledArea
import org.fxmisc.richtext.StyledTextArea
import org.fxmisc.richtext.model.SegmentOps
import org.fxmisc.richtext.model.StyledSegment

typealias PS = Collection<String>
typealias SEG = String
typealias S = Collection<String>

private fun createNode(seg: StyledSegment<SEG, S>, applyStyle: (Text, S) -> Unit): Node {
    return StyledTextArea.createStyledTextNode(seg.segment, seg.style, applyStyle)
}

class EditorTextArea: GenericStyledArea<PS, SEG, S>(
    listOf(),
    { paragraph: TextFlow, styleClasses: S -> paragraph.styleClass.addAll(styleClasses) },
    listOf(),

    SegmentOps.styledTextOps(),
    { seg: StyledSegment<SEG, S> -> createNode(seg) { text, styleClasses -> text.styleClass.addAll(styleClasses) } },
)