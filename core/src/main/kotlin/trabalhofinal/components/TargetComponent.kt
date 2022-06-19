package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import trabalhofinal.components.general.Component

class TargetComponent(
    val component: Component,
    val dist: Float
) {
    val type get() = component.type
    var color: Color
        get() = component.color
        set(value) {
            component.color = value
        }
}