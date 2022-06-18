package trabalhofinal.components

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

class RayCastCompList(private val components: MutableList<RayCastComponent>): Disposable {
    constructor() : this(mutableListOf())

    fun add(comp: RayCastComponent) = components.add(comp)
    fun remove(comp: RayCastComponent) = components.remove(comp)
    fun remove(comp: Component) = components.remove(comp)
    operator fun get(i: Int): RayCastComponent = components[i]

    fun createMeshes(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        components.forEach { component ->
            component.createMesh(player, zBuffer, tileWidth, tileHeight)
        }
    }

    fun render(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        components.sort()
        components.asReversed().forEach { components ->
            components.render(shader, initialX, initialY, ratio)
        }
    }

    override fun dispose(){
        components.forEach {
            it.dispose()
        }
    }
}