package com.editor.application

import javafx.scene.Parent
import javafx.scene.paint.Paint
import javafx.scene.Scene as JFXScene

class Scene: JFXScene {
    constructor(root: Parent): super(root)
    constructor(root: Parent, fill: Paint): super(root, fill)
    constructor(root: Parent, width: Double, height: Double): super(root, width, height)
    constructor(root: Parent, width: Double, height: Double, fill: Paint): super(root, width, height, fill)
    init {
        this.stylesheets.add(this::class.java.getResource("/css/main.css")?.toString() ?: "")
    }
}