package ui.components

import commons.Val
import jb.kgull.ui.components.RenderObject
import jb.kgull.ui.framework.*

data class Column(
        val spacing: Double = 0.0,
        val body: UIBody
) : UIComponent {
    override fun UIBuilder.build() = body()

    override fun initSlot(stage: Stage, parent: Slot<*>?): Slot<*> = RenderNode(stage, parent, this)

    class RenderNode(stage: Stage, parent: Slot<*>?, element: Column) : RenderObject<Column>(stage, parent, element) {
        override fun onUpdated(component: Column, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
            super.onUpdated(component, children, vals)

            minWidth = 0.0
            maxWidth = Double.POSITIVE_INFINITY

            renderChildren.forEach {
                if (it.minWidth > minWidth) minWidth = it.minWidth
            }

            var y = 0.0
            renderChildren.forEach {
                if (y != 0.0) {
                    y += component.spacing
                }

                it.height = it.minHeight
                it.width = it.minWidth

                it.y = y
                y += it.height
            }

            minHeight = y
            maxHeight = Double.POSITIVE_INFINITY
        }
    }
}