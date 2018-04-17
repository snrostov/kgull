package jb.kgull.ui.components

import commons.Val
import jb.kgull.ui.framework.Slot
import jb.kgull.ui.framework.Stage
import jb.kgull.ui.framework.UIComponent
import jb.kgull.ui.geometry.Point
import java.awt.Graphics2D

open class RenderObject<C : UIComponent>(
        stage: Stage,
        parent: Slot<*>?,
        component: C
) : Slot<C>(stage, parent, component) {
    val renderParent: RenderObject<*>?

    init {
        if (parent != null) {
            var i: Slot<*> = parent
            while (i !is RenderObject<*>) {
                i = i.parent ?: error("Root is not a RenderObject")
            }
            renderParent = i
        } else {
            renderParent = null
        }
    }

    var renderChildren: List<RenderObject<*>> = listOf()
        private set

    var minWidth: Double = 0.0
    var maxWidth: Double = Double.POSITIVE_INFINITY

    var minHeight: Double = 0.0
    var maxHeight: Double = Double.POSITIVE_INFINITY

    var x: Double = 0.0
    var y: Double = 0.0
    var width: Double = 0.0
    var height: Double = 0.0

    open fun paint(g: Graphics2D) {
        paintBackground(g)
        paintChildren(g)
        paintForeground(g)
    }

    protected open fun paintBackground(g: Graphics2D) {

    }

    protected open fun paintChildren(g: Graphics2D) {
        renderChildren.forEach {
            val transform = g.transform
            val clip = g.clip

            g.translate(it.x, it.y)
            g.clipRect(0, 0, it.width.toInt(), it.height.toInt())

//            g.drawRect(0, 0, it.width.toInt(), it.height.toInt())
            it.paint(g)

            g.clip = clip
            g.transform = transform
        }
    }

    protected open fun paintForeground(g: Graphics2D) = Unit

    override fun onUpdated(component: C, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
        super.onUpdated(component, children, vals)

        if (renderParent != null) {
            minWidth = renderParent.minWidth
            maxWidth = renderParent.maxWidth
            minHeight = renderParent.minHeight
            maxHeight = renderParent.maxHeight
        }

        renderChildren = mutableListOf<RenderObject<*>>().also {
            collectDirectRenderChildrenTo(it)
        }
    }

    operator fun contains(pt: Point): Boolean =
            pt.x in x..(x + width) && pt.y in y..(y + height)

    open fun pointerClick(pt: Point) {
        renderChildren.forEach {
            if (pt in it) {
                it.pointerClick(Point(pt.x - it.x, pt.y - it.y))
            }
        }
    }

    open val renderBoxString
        get() = "pos=(${x.onePointStr}, ${y.onePointStr}), " +
                "size=(" +
                "${width.onePointStr} [${minWidth.onePointStr}..${maxWidth.onePointStr}], " +
                "${height.onePointStr} [${minHeight.onePointStr}..${maxHeight.onePointStr}]" +
                ")"

    override fun toString() = "#${super.toString()} $renderBoxString"
}

private fun Slot<*>.collectDirectRenderChildrenTo(list: MutableList<RenderObject<*>>) {
    children.values.forEach {
        if (it is RenderObject<*>) list.add(it)
        else it.collectDirectRenderChildrenTo(list)
    }
}

private val Double.onePointStr: String
    get() = when (this) {
        Double.POSITIVE_INFINITY -> "+INF"
        Double.NEGATIVE_INFINITY -> "-INF"
        Double.MAX_VALUE -> "MAX"
        Double.MIN_VALUE -> "MIN"
        else -> {
            val format = format(1)
            if (format.endsWith(".00")) format.substring(0, format.length - 3)
            else format
        }
    }

private fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)