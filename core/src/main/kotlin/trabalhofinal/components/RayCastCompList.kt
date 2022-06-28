package trabalhofinal.components

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import trabalhofinal.components.general.Component
import trabalhofinal.components.general.MapBatchDrawable
import trabalhofinal.components.general.Player
import trabalhofinal.components.general.RayCastComponent

class RayCastCompList(private val components: MutableList<RayCastComponent>): Disposable, MapBatchDrawable {
    constructor() : this(mutableListOf())

    fun add(comp: RayCastComponent) {
        components.add(comp)
        comp.tile.component = comp
    }
    fun remove(comp: Component) = components.remove(comp)
    operator fun get(i: Int): RayCastComponent = components[i]

    fun updateComponents(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        components.forEach { component ->
            component.update(player, zBuffer, tileWidth, tileHeight)
        }
    }

    fun render(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        components.sort()
        components.asReversed().forEach { components ->
            components.render(shader, initialX, initialY, ratio)
        }
    }

    override fun draw(startX: Float, startY: Float, ratio: Float, batch: Batch) = components.forEach { it.draw(startX, startY, ratio, batch) }

    override fun dispose(){
        components.forEach {
            it.dispose()
        }
    }
}