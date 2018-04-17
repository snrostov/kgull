package jb.kgull.ui.framework

import commons.Val

/**
 * Acts as UIBuilder, ensures all children holders have actual UIComponents.
 */
class SlotReconciler<C : UIComponent>(
        private val slot: Slot<C>,
        private val newComponent: C
) : UIBuilder {
    private val oldVals = slot.vals
    private val oldChildren = slot.children

    private val newVals = mutableSetOf<Val<*>>()
    private val newChildren = mutableMapOf<Any, Slot<*>>()

    override fun add(component: UIComponent) {
        val key = component.key
        val existedHolder = oldChildren[key]

        if (existedHolder == null) {
            newSlot(key, component)
        } else {
            val oldElement = existedHolder.component

            val shouldCompareByContents = key !== component
            if (shouldCompareByContents && oldElement != component) {
                updateSlot(existedHolder as Slot<UIComponent>, oldElement, component)
            }

            newChildren[key] = existedHolder
        }
    }

    override fun <T> subscribe(value: Val<T>) {
        newVals.add(value)
        value.addListener(slot)
    }

    fun complete() {
        oldVals.forEach {
            if (it !in newVals) {
                it.removeListener(slot)
            }
        }

        oldChildren.forEach {
            if (it.key !in newChildren) {
                removeSlot(it.value)
            }
        }

        slot.onUpdated(newComponent, newChildren, newVals)
    }

    private fun newSlot(key: Any, component: UIComponent) {
        val slot = component.initSlot(slot.stage, slot)
        component.mount(slot, null)
        slot.updateChildren()

        newChildren[key] = slot
    }

    private fun <T : UIComponent> updateSlot(slot: Slot<T>, oldComponent: T, newComponent: T) {
        oldComponent.unmount(slot, newComponent)
        newComponent.mount(slot, null)

        slot.updateChildren(newComponent)
    }

    private fun removeSlot(slot: Slot<*>) {
        slot.dispose()
    }
}