package ui.components

import commons.Val
import jb.kgull.ui.components.RenderObject
import jb.kgull.ui.framework.*
import jb.kgull.ui.geometry.Point
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

enum class Align {
    start {
        override fun align(childWidth: Double, width: Double) = 0.0
    },
    center {
        override fun align(childWidth: Double, width: Double) = width / 2 - childWidth / 2
    },
    end {
        override fun align(childWidth: Double, width: Double) = width - childWidth
    };

    abstract fun align(childWidth: Double, width: Double): Double
}

fun Box(
        backgroundColor: Color? = null,
        padding: Double = 0.0,
        borderColor: Color? = null,
        borderWidth: Double = 0.0,
        width: Double = 0.0,
        height: Double = 0.0,
        verticalAlign: Align = Align.center,
        horizontalAlign: Align = Align.center,
        onClick: ((Point) -> Unit)? = null,
        body: UIBody
) = Box(
        backgroundColor = backgroundColor,
        paddingLeft = padding,
        paddingTop = padding,
        paddingRight = padding,
        paddingBottom = padding,
        borderColor = borderColor,
        borderWidth = borderWidth,
        innerWidth = width,
        innerHeight = height,
        verticalAlign = verticalAlign,
        horizontalAlign = horizontalAlign,
        onClick = onClick,
        body = body
)

data class Box(
        val backgroundColor: Color? = null,
        val paddingLeft: Double = 0.0,
        val paddingTop: Double = 0.0,
        val paddingRight: Double = 0.0,
        val paddingBottom: Double = 0.0,
        val borderColor: Color? = null,
        val borderWidth: Double = 0.0,
        val innerWidth: Double = 0.0,
        val innerHeight: Double = 0.0,
        val verticalAlign: Align = Align.center,
        val horizontalAlign: Align = Align.center,
        val onClick: ((Point) -> Unit)? = null,
        val body: UIBody
) : UIComponent {
    override fun initSlot(stage: Stage, parent: Slot<*>?): Slot<*> = Node(stage, parent, this)

    override fun UIBuilder.build() {
        body()
    }

    class Node(
            stage: Stage,
            parent: Slot<*>?,
            component: Box
    ) : RenderObject<Box>(stage, parent, component) {
        override fun onUpdated(component: Box, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
            super.onUpdated(component, children, vals)

            val child = renderChildren.singleOrNull() ?: error("Box requires single child")

            child.width = child.minWidth
            child.height = child.minHeight

            val innerWidth = if (component.innerWidth == 0.0) child.width else component.innerWidth
            val innerHeight = if (component.innerHeight == 0.0) child.height else component.innerWidth

            child.x = component.paddingLeft +
                    component.borderWidth +
                    component.verticalAlign.align(child.width, innerWidth)

            child.y = component.paddingRight +
                    component.borderWidth +
                    component.verticalAlign.align(child.height, innerHeight)

            minWidth = innerWidth +
                    component.borderWidth + component.paddingLeft +
                    component.borderWidth + component.paddingRight

            maxWidth = child.maxWidth

            minHeight = innerHeight +
                    component.borderWidth + component.paddingTop +
                    component.borderWidth + component.paddingBottom

            maxHeight = child.maxHeight
        }

        override fun paintBackground(g: Graphics2D) {
            val c = component

            val backgroundColor = c.backgroundColor
            if (backgroundColor != null) {
                g.color = backgroundColor
                g.fillRect(
                        c.paddingLeft.toInt(),
                        c.paddingTop.toInt(),
                        width.toInt() - c.paddingRight.toInt() - c.paddingLeft.toInt(),
                        height.toInt() - c.paddingBottom.toInt() - c.paddingTop.toInt()
                )
            }

            val borderColor = c.borderColor
            if (borderColor != null) {
                g.color = borderColor
                g.stroke = BasicStroke(c.borderWidth.toFloat())
                g.drawRect(
                        c.paddingLeft.toInt(),
                        c.paddingTop.toInt(),
                        width.toInt() - c.paddingRight.toInt() - c.paddingLeft.toInt(),
                        height.toInt() - c.paddingBottom.toInt() - c.paddingTop.toInt()
                )
            }
        }

        override fun pointerClick(pt: Point) {
            super.pointerClick(pt)

            component.onClick?.invoke(pt)
        }
    }
}