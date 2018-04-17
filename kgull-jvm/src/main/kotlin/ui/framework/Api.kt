package jb.kgull.ui.framework

import commons.Val

typealias UIBody = UIBuilder.() -> Unit

interface UIComponent {
    val key: Any
        get() = this

    fun initSlot(stage: Stage, parent: Slot<*>?): Slot<*> = Slot(stage, parent, this)

    fun mount(slot: Slot<*>, old: UIComponent?) = Unit

    fun UIBuilder.build() = Unit

    fun unmount(slot: Slot<*>, replacement: UIComponent?) = Unit
}

interface UIBuilder {
    fun add(component: UIComponent)
    fun <T> subscribe(value: Val<T>)

    operator fun UIComponent.unaryPlus() = add(this)
    operator fun UIBody.unaryPlus() = this()

    fun <T> Val<T>.use(): T {
        subscribe(this)
        return value
    }
}

