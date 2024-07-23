package com.editor.application.menubar

import com.editor.application.Main
import com.editor.application.documents.EditorPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class CustomHotkeyFunctions {

    fun updateInitialKey(action: String, keyCode: String) {
        if (keyCode == "CONTROL" || keyCode == "ALT" || keyCode == "SHIFT") {
            var buttonText = when (keyCode) {
                "CONTROL" -> {
                    "Ctrl"
                }
                "ALT" -> {
                    "Alt"
                }
                else -> {
                    "Shift"
                }
            }
            when (action) {
                "save" -> {
                    if (Main.saveShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.saveButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "open" -> {
                    if (Main.openShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.openButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "newTab" -> {
                    if (Main.newTabShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.newTabButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "closeTab" -> {
                    if (Main.closeTabShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.closeTabButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "bold" -> {
                    if (Main.boldShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.boldButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "italic" -> {
                    if (Main.italicShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.italicButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
                "heading" -> {
                    if (Main.headingShortcut[0].toString() != keyCode) {
                        CustomHotkeyMenu.headingButtonInitial.text = buttonText
                    } else {
                        println("Invalid key code.")
                    }
                }
            }
        } else {
            println("Invalid Key Code.")
        }
    }

    fun updateKeycode(action: String, keyCode: String) {
        when (action) {
            "save" -> {
                if (keyCode == CustomHotkeyMenu.saveButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.saveButtonInitial.text != "Shift") {
                    Main.saveShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.saveButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.saveButtonInitial.text != "Alt") {
                    Main.saveShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.saveButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.saveButtonInitial.text != "Ctrl") {
                    Main.saveShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.saveButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.saveShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.saveButtonKey.text = Main.saveShortcut[0].toString()
                }
            }
            "open" -> {
                if (keyCode == CustomHotkeyMenu.openButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.openButtonInitial.text != "Shift") {
                    Main.openShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.openButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.openButtonInitial.text != "Alt") {
                    Main.openShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.openButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.openButtonInitial.text != "Ctrl") {
                    Main.openShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.openButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.openShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.openButtonKey.text = Main.openShortcut[0].toString()
                }
            }
            "newTab" -> {
                if (keyCode == CustomHotkeyMenu.newTabButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.newTabButtonInitial.text != "Shift") {
                    Main.newTabShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.newTabButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.newTabButtonInitial.text != "Alt") {
                    Main.newTabShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.newTabButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.newTabButtonInitial.text != "Ctrl") {
                    Main.newTabShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.newTabButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.newTabShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.newTabButtonKey.text = Main.newTabShortcut[0].toString()
                }
            }
            "closeTab" -> {
                if (keyCode == CustomHotkeyMenu.closeTabButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.closeTabButtonInitial.text != "Shift") {
                    Main.closeTabShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.closeTabButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.closeTabButtonInitial.text != "Alt") {
                    Main.closeTabShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.closeTabButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.closeTabButtonInitial.text != "Ctrl") {
                    Main.closeTabShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.closeTabButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.closeTabShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.closeTabButtonKey.text = Main.closeTabShortcut[0].toString()
                }
            }
            "bold" -> {
                if (keyCode == CustomHotkeyMenu.boldButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.boldButtonInitial.text != "Shift") {
                    Main.boldShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.boldButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.boldButtonInitial.text != "Alt") {
                    Main.boldShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.boldButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.boldButtonInitial.text != "Ctrl") {
                    Main.boldShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.boldButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.boldShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.boldButtonKey.text = Main.boldShortcut[0].toString()
                }
            }
            "italic" -> {
                if (keyCode == CustomHotkeyMenu.italicButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.italicButtonInitial.text != "Shift") {
                    Main.italicShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.italicButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.italicButtonInitial.text != "Alt") {
                    Main.italicShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.italicButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.italicButtonInitial.text != "Ctrl") {
                    Main.italicShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.italicButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.italicShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.italicButtonKey.text = Main.italicShortcut[0].toString()
                }
            }
            "heading" -> {
                if (keyCode == CustomHotkeyMenu.headingButtonOptional.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "SHIFT" && CustomHotkeyMenu.headingButtonInitial.text != "Shift") {
                    Main.headingShortcut[0] = KeyCode.SHIFT
                    CustomHotkeyMenu.headingButtonKey.text = "Shift"
                } else if (keyCode == "ALT" && CustomHotkeyMenu.headingButtonInitial.text != "Alt") {
                    Main.headingShortcut[0] = KeyCode.ALT
                    CustomHotkeyMenu.headingButtonKey.text = "Alt"
                } else if (keyCode == "CONTROL" && CustomHotkeyMenu.headingButtonInitial.text != "Ctrl") {
                    Main.headingShortcut[0] = KeyCode.CONTROL
                    CustomHotkeyMenu.headingButtonKey.text = "Ctrl"
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                    return
                } else {
                    Main.headingShortcut[0] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.headingButtonKey.text = Main.headingShortcut[0].toString()
                }
            }
        }
    }

    fun updateOptionalKey(action: String, keyCode: String) {
        when (action) {
            "save" -> {
                if (keyCode == CustomHotkeyMenu.saveButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.saveShortcut.size == 2) {
                        Main.saveShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.saveButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.saveShortcut.size == 2) {
                    Main.saveShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.saveButtonOptional.text = Main.saveShortcut[1].toString()
                } else if (Main.saveShortcut.size == 1) {
                    Main.saveShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.saveButtonOptional.text = Main.saveShortcut[1].toString()
                }
                println(Main.saveShortcut)
            }
            "open" -> {
                if (keyCode == CustomHotkeyMenu.openButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.openShortcut.size == 2) {
                        Main.openShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.openButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.openShortcut.size == 2) {
                    Main.openShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.openButtonOptional.text = Main.openShortcut[1].toString()
                } else if (Main.openShortcut.size == 1) {
                    Main.openShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.openButtonOptional.text = Main.openShortcut[1].toString()
                }
                println(Main.openShortcut)
            }
            "newTab" -> {
                if (keyCode == CustomHotkeyMenu.newTabButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.newTabShortcut.size == 2) {
                        Main.newTabShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.newTabButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.newTabShortcut.size == 2) {
                    Main.newTabShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.newTabButtonOptional.text = Main.newTabShortcut[1].toString()
                } else if (Main.newTabShortcut.size == 1) {
                    Main.newTabShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.newTabButtonOptional.text = Main.newTabShortcut[1].toString()
                }
                println(Main.newTabShortcut)
            }
            "closeTab" -> {
                if (keyCode == CustomHotkeyMenu.closeTabButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.closeTabShortcut.size == 2) {
                        Main.closeTabShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.closeTabButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.closeTabShortcut.size == 2) {
                    Main.closeTabShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.closeTabButtonOptional.text = Main.closeTabShortcut[1].toString()
                } else if (Main.closeTabShortcut.size == 1) {
                    Main.closeTabShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.closeTabButtonOptional.text = Main.closeTabShortcut[1].toString()
                }
                println(Main.closeTabShortcut)
            }
            "bold" -> {
                if (keyCode == CustomHotkeyMenu.boldButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.boldShortcut.size == 2) {
                        Main.boldShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.boldButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.boldShortcut.size == 2) {
                    Main.boldShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.boldButtonOptional.text = Main.boldShortcut[1].toString()
                } else if (Main.boldShortcut.size == 1) {
                    Main.boldShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.boldButtonOptional.text = Main.boldShortcut[1].toString()
                }
                println(Main.boldShortcut)
            }
            "italicize" -> {
                if (keyCode == CustomHotkeyMenu.italicButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.italicShortcut.size == 2) {
                        Main.italicShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.italicButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.italicShortcut.size == 2) {
                    Main.italicShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.italicButtonOptional.text = Main.italicShortcut[1].toString()
                } else if (Main.italicShortcut.size == 1) {
                    Main.italicShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.italicButtonOptional.text = Main.italicShortcut[1].toString()
                }
                println(Main.italicShortcut)
            }
            "heading" -> {
                if (keyCode == CustomHotkeyMenu.headingButtonKey.text) {
                    println("Invalid key code.")
                    return
                }
                if (keyCode == "BACK_SPACE" || keyCode == "DELETE") {
                    if (Main.headingShortcut.size == 2) {
                        Main.headingShortcut.removeAt(1)
                    }
                    CustomHotkeyMenu.headingButtonOptional.text = ""
                } else if (KeyCode.getKeyCode(keyCode) == null) {
                    println("Invalid key code.")
                } else if (Main.headingShortcut.size == 2) {
                    Main.headingShortcut[1] = KeyCode.getKeyCode(keyCode)
                    CustomHotkeyMenu.headingButtonOptional.text = Main.headingShortcut[1].toString()
                } else if (Main.headingShortcut.size == 1) {
                    Main.headingShortcut.add(KeyCode.getKeyCode(keyCode))
                    CustomHotkeyMenu.headingButtonOptional.text = Main.headingShortcut[1].toString()
                }
                println(Main.headingShortcut)
            }
        }
    }

    var saveKeyPressed = 0
    var openKeyPressed = 0
    var newTabKeyPressed = 0
    var closeTabKeyPressed = 0
    var boldKeyPressed = 0
    var italicKeyPressed = 0
    var headingKeyPressed = 0

    fun handleKeyEvent(event: KeyEvent, keyCombination: MutableList<KeyCode>, initialButtonText: String, keyPressed: Int): Int {
        val eventDown = when (initialButtonText) {
            "Ctrl" -> {
                event.isControlDown
            }
            "Shift" -> {
                event.isShiftDown
            }
            "Alt" -> {
                event.isAltDown
            }
            else -> {
                event.isControlDown
            }
        }
        if (event.code == keyCombination[keyPressed] && eventDown) {
            val newKeyPressed = keyPressed + 1
            if (newKeyPressed == keyCombination.size) {
                when (keyCombination) {
                    Main.saveShortcut -> {
                        (Main.docView!!.selectionModel.selectedItem.content as EditorPane).save()
                    }
                    Main.openShortcut -> {
                        Main.docView!!.openTab()
                    }
                    Main.newTabShortcut -> {
                        Main.docView!!.newTab(null)
                    }
                    Main.closeTabShortcut -> {
                        Main.docView!!.tabs.remove(Main.docView!!.selectionModel.selectedItem)
                    }
                    Main.boldShortcut -> {
                        (Main.docView!!.selectionModel.selectedItem.content as EditorPane).bolden()
                    }
                    Main.italicShortcut -> {
                        (Main.docView!!.selectionModel.selectedItem.content as EditorPane).italicize()
                    }
                    Main.headingShortcut -> {
                        (Main.docView!!.selectionModel.selectedItem.content as EditorPane).increaseHeading()
                    }
                }
                return 0 // Reset the keyPressed variable
            } else {
                return newKeyPressed
            }
        } else {
            return 0 // Reset the keyPressed variable
        }
    }
}
