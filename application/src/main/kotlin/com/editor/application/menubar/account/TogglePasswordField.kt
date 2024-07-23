package com.editor.application.menubar.account

import javafx.geometry.Insets
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.beans.binding.DoubleBinding
import javafx.scene.control.Skin
import javafx.scene.control.skin.TextFieldSkin
import org.kordamp.ikonli.javafx.FontIcon

class TogglePasswordField: TextField() {

    override fun createDefaultSkin(): Skin<*> {
        return TogglePasswordFieldSkin(this)
    }

    internal open class TogglePasswordFieldSkin(textField: TogglePasswordField) : TextFieldSkin(textField) {

        private var show: ToggleButton?

        init {
            textField.padding = Insets(10.0, 25.0, 10.0, 10.0)      // trial and error

            show = ToggleButton().apply {
                graphic = FontIcon("far-eye-slash")
                isFocusTraversable = false
                setMaxSize(20.0, 20.0)
                setMinSize(20.0, 20.0)
                padding = Insets(10.0)
                selectedProperty().addListener { _, _, _ ->
                    // Resetting the text to invalidate the text property so that it will call the maskText method.
                    val txt = textField.text
                    val pos = textField.caretPosition
                    textField.text = null
                    textField.text = txt
                    textField.positionCaret(pos)
                    graphic = if (isSelected) FontIcon("far-eye") else FontIcon("far-eye-slash")
                }
                translateXProperty().bind(object : DoubleBinding() {
                    init {
                        bind(textField.widthProperty(), widthProperty())
                    }

                    override fun computeValue(): Double {
                        return (textField.width - width) / 2
                    }
                })
            }

            children.add(show)
        }

        override fun maskText(txt: String): String {
            val bullet = '●'
            return if (show != null && !show!!.isSelected) {
                val n = txt.length
                val passwordBuilder = StringBuilder(n)
                for (i in 0 until n) {
                    passwordBuilder.append(bullet)
                }
                passwordBuilder.toString()
            } else {
                txt
            }
        }
    }
}