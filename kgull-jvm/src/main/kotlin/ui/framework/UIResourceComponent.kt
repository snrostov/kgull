package ui.framework

import jb.kgull.ui.framework.UIComponent
import jb.kgull.ui.framework.Slot


abstract class UIResourceComponent<R : Any> : UIComponent {
    protected lateinit var resource: R

    abstract fun initResource(): R
    abstract fun disposeResource(resource: R)

    override fun mount(slot: Slot<*>, old: UIComponent?) {
        resource = (old as UIResourceComponent<R>?)?.resource
                ?: initResource()
    }

    override fun unmount(slot: Slot<*>, replacement: UIComponent?) {
        if (replacement != null) {
            disposeResource(resource)
        }
    }
}