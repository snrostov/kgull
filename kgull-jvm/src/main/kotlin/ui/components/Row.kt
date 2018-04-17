package ui.components

import commons.Val
import jb.kgull.ui.components.RenderObject
import jb.kgull.ui.framework.*

data class Row(
        val spacing: Double = 0.0,
        val body: UIBody
) : UIComponent {
    override fun UIBuilder.build() = body()

    override fun initSlot(stage: Stage, parent: Slot<*>?): Slot<*> = RenderNode(stage, parent, this)

    class RenderNode(stage: Stage, parent: Slot<*>?, element: Row) : RenderObject<Row>(stage, parent, element) {
        override fun onUpdated(component: Row, children: Map<Any, Slot<*>>, vals: Set<Val<*>>) {
            super.onUpdated(component, children, vals)

            minHeight = 0.0
            maxWidth = Double.POSITIVE_INFINITY

            renderChildren.forEach {
                if (it.minHeight > minHeight) minHeight = it.minHeight
            }

            var x = 0.0
            renderChildren.forEach {
                if (x != 0.0) {
                    x += component.spacing
                }

                it.height = it.minHeight
                it.width = it.minWidth

                it.x = x
                x += it.width
            }

            minWidth = x
            maxWidth = Double.POSITIVE_INFINITY
        }
    }
}