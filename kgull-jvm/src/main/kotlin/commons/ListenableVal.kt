package commons

import jb.kgull.ui.framework.Slot

abstract class Val<T> {
    abstract val value: T
    private val listeners = mutableSetOf<Slot<*>>()

    fun addListener(holder: Slot<*>) {
        listeners.add(holder)
    }

    fun removeListener(holder: Slot<*>) {
        listeners.remove(holder)
    }

    fun fireChanged() {
        listeners.forEach {
            it.valUpdated(this)
        }
    }
}

class Var<T>(value: T) : Val<T>() {
    override var value: T = value
        set(value) {
            field = value
            fireChanged()
        }
}

operator fun Var<Int>.plusAssign(inc: Int) {
    value += inc
}

operator fun Var<Int>.minusAssign(inc: Int) {
    value -= inc
}