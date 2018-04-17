package jb.kgull

import commons.Var
import commons.minusAssign
import commons.plusAssign
import jb.kgull.ui.components.Button
import jb.kgull.ui.framework.UIBody
import jb.kgull.ui.framework.UIBuilder
import jb.kgull.ui.framework.UIComponent
import jb.kgull.ui.framework.Window
import ui.components.Box
import ui.components.Column
import ui.components.Row
import ui.components.Text
import ui.framework.UIResourceComponent
import java.util.*
import kotlin.concurrent.fixedRateTimer

fun main(args: Array<String>) {
    val myCount = Var(3)
    val myTime = Var(1)

    Window {
        +Box(padding = 20.0) {
            +Column {
                +MySection("Example") {
                    +Column {
                        +Text("This example demonstrates 3 basic UI framework cases:")
                        +Text("- Extracting parts to components: MyTitle and MySection components")
                        +Text("- Changing state: MyCounter")
                        +Text("- Holding disposable resource: MyTimer")
                    }
                }

                +MySection("Count of sub counters") {
                    +MyCounter("counter", myCount)
                }

                +MySection("Sub counters") {
                    var times = myCount.use()
                    while (times > 0) {
                        +Row {
                            var inRow = 4
                            while (times > 0 && inRow > 0) {
                                +MyCounter(times, myCount)
                                times--
                                inRow--
                            }
                        }
                    }
                }

//                +MySection("Timer") {
//                    +MyTimer(myTime)
//                }
            }
        }
    }
}

data class MyTitle(val title: String) : UIComponent {
    override fun UIBuilder.build() {
        +Text(title, fontSize = 24.0)
    }
}

data class MySection(val title: String, val body: UIBody) : UIComponent {
    override fun UIBuilder.build() {
        +Box(
                paddingTop = 10.0,
                paddingBottom = 10.0
        ) {
            +Column(spacing = 10.0) {
                +MyTitle(title)

                +body
            }
        }
    }
}

class MyCounter(override val key: Any, val count: Var<Int>) : UIComponent {
    override fun UIBuilder.build() {
        +Row {
            +Button("-", width = 20.0, height = 20.0, onClick = { count -= 1 })

            +Box(padding = 5.0, width = 20.0, height = 20.0) {
                +Text(count.use().toString())
            }

            +Button("+", width = 20.0, height = 20.0, onClick = {
                count += 1
            })
        }
    }
}

data class MyTimer(val count: Var<Int>) : UIResourceComponent<Timer>() {
    override fun initResource() =
            fixedRateTimer(period = 100) {
                count += 1
            }

    override fun disposeResource(resource: Timer) =
            resource.cancel()

    override fun UIBuilder.build() {
        +MyCounter("time", count)
    }
}