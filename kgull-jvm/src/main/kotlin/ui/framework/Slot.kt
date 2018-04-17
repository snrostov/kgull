package jb.kgull.ui.framework

import commons.IndentedAppendable
import commons.Val
import commons.nested

open class Slot<C : UIComponent>(
        val stage: Stage,
        val parent: Slot<*>?,
        component: C
) {
    var invalid: Boolean = true

    internal var component: C = component
        private set

    internal var children = mapOf<Any, Slot<*>>()
        private set

    internal var vals = setOf<Val<*>>()
        private set

    var parentData: Any? = null

    internal fun ensureActual() {
        if (invalid) {
            println("!!!!!!!! Updating subtree !!!!!")
            print(IndentedAppendable(System.out))
            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

            updateChildren()
        } else {
            children.forEach {
                it.value.ensureActual()
            }
        }
    }

    internal fun updateChildren(newComponent: C = component) {
        val builder = SlotReconciler(this, newComponent)
        with(newComponent) { builder.build() }
        builder.complete()
        invalid = false
    }

    internal open fun onUpdated(
            component: C,
            children: Map<Any, Slot<*>>,
            vals: Set<Val<*>>
    ) {
        this.component = component
        this.children = children
        this.vals = vals
    }

    fun valUpdated(v: Val<*>) {
        if (v in vals) {
            invalidate()
        }
    }

    private fun invalidate() {
        if (invalid) return

        println("===== Invalidating subtree ====")
        print(IndentedAppendable(System.out))
        println("===============================")

        invalid = true
        stage.ensureActualizeScheduled()
    }

    internal fun dispose() {
        component.unmount(this, null)
        vals.forEach {
            it.removeListener(this)
        }
    }

    fun print(out: IndentedAppendable) {
        out.appendln(toString())
        out.nested { nested ->
            children.values.forEach {
                it.print(nested)
            }
        }
    }

    override fun toString(): String {
        val toString = component.toString()
        return toString.replace(Regex("(, )?[^=,]+=Function\\d<[^>]+>"), "")
    }
}