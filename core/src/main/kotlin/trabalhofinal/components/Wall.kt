package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.Component
import trabalhofinal.components.general.ComponentType
import trabalhofinal.components.general.RayCastTile

open class Wall(
    var tile: RayCastTile,
    override var color: Color,
    override val texture: Texture?,
    ): Component {
    override val isWall = true
    override val type = ComponentType.WALL
    private val i = tile.i
    private val j = tile.j

    fun forEachNeighbor(ship: Ship, func: (Component?) -> Unit){
        listOf(-1, 1).forEach { idx ->
            if (idx + j in 0 until  ship.sizeJ)
                func(ship[i, idx + j].component)
            if (idx + i in 0 until ship.sizeI)
                func(ship[idx + i, j].component)
        }
    }
}