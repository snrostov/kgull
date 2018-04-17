package jb.kgull.ui.framework

import commons.IndentedAppendable
import commons.Val
import jb.kgull.ui.components.RenderObject
import jb.kgull.ui.geometry.Point
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.SwingUtilities.invokeLater

class Stage(root: UIComponent) {
    private val root = object : RenderObject<UIComponent>(this, null, root) {
        override fun onUpdated(component: UIComponent, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
            super.onUpdated(component, children, vals)

            if (!renderChildren.isEmpty()) {
                val child = renderChildren.single()

                child.width = child.minWidth
                child.height = child.minHeight

                width = child.width
                height = child.height
            }
        }
    }

    fun actualize() {
        root.ensureActual()
    }

    lateinit var jFrame: JFrame

    fun run() {
        invokeLater {
            actualize()
            root.print(IndentedAppendable(System.out, ""))

            jFrame = JFrame("KGull")
            jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            val rootPane = object : JPanel() {
                override fun paint(g: Graphics) {
                    g as Graphics2D
                    g.setRenderingHint(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
                    )
                    g.color = Color.WHITE
                    g.fillRect(0, 0, width, height)

                    root.paint(g)
                }
            }

            rootPane.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    root.pointerClick(Point(e.x.toDouble(), e.y.toDouble()))
                }
            })

            jFrame.contentPane.add(rootPane)
            update()

            jFrame.isVisible = true
        }
    }

    fun ensureActualizeScheduled() {
        SwingUtilities.invokeLater(::update)
    }

    private fun update() {
        actualize()

        jFrame.rootPane.preferredSize = Dimension(root.width.toInt(), root.height.toInt())
        jFrame.pack()

        jFrame.rootPane.repaint()
    }
}

fun Window(body: UIBody) {
    Stage(object : UIComponent {
        override fun UIBuilder.build() {
            body()
        }
    }).run()
}