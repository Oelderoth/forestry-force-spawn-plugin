@file:Suppress("unused")
package xyz.oelderoth.runelite.kotlin.extensions

import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

val MouseEvent.isLeftClick
    get() = SwingUtilities.isLeftMouseButton(this)
val MouseEvent.isRightClick
    get() = SwingUtilities.isRightMouseButton(this)