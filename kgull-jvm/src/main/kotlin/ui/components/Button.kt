package jb.kgull.ui.components

import jb.kgull.ui.framework.UIBuilder
import jb.kgull.ui.framework.UIComponent
import ui.components.Box
import ui.components.Text
import java.awt.Color

data class Button(
        val text: String,
        val width: Double = 0.0,
        val height: Double = 0.0,
        val onClick: () -> Unit
) : UIComponent {
    override fun UIBuilder.build() {
        +Box(
                borderColor = Color.BLACK,
                padding = 5.0,
                backgroundColor = Color.lightGray,
                width = width,
                height = height,
                onClick = { onClick() }
        ) {
            +Text(text)
        }
    }
}