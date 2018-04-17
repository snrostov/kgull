package ui.components

import commons.Val
import jb.kgull.ui.components.RenderObject
import jb.kgull.ui.framework.Slot
import jb.kgull.ui.framework.Stage
import jb.kgull.ui.framework.UIComponent
import java.awt.*
import javax.swing.JLabel

private val defaultFont = JLabel().font
private val canvas = Canvas()

data class Text(val text: String, val fontSize: Double = defaultFont.size.toDouble()) : UIComponent {
    override fun initSlot(stage: Stage, parent: Slot<*>?): Slot<*> = RenderNode(stage, parent, this)

    class RenderNode(stage: Stage, parent: Slot<*>?, component: Text) : RenderObject<Text>(stage, parent, component) {
        lateinit var font: Font
        lateinit var fontMetrics: FontMetrics

        init {
            updateFont(component)
        }

        private fun updateFont(component: Text) {
            font = defaultFont.deriveFont(component.fontSize.toFloat())
            fontMetrics = canvas.getFontMetrics(font)
        }

        override fun onUpdated(component: Text, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
            if (this.component.fontSize != component.fontSize) {
                updateFont(component)
            }

            super.onUpdated(component, children, vals)

            minWidth = fontMetrics.stringWidth(component.text).toDouble()
            minHeight = fontMetrics.height.toDouble()
        }

        override fun paint(g: Graphics2D) {
            val prevFont = g.font
            g.font = font
            g.color = Color.black
            g.drawString(component.text, 0f, height.toFloat() - fontMetrics.descent)
            g.font = prevFont
        }
    }
}