package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import ktx.graphics.color
import trabalhofinal.components.general.Component
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.utils.Node
import kotlin.random.Random

class Fungo(
    tile: IRayCastTile,
    texture: Texture? = null, //TODO COLOCAR TEXTURA
    color: Color = Color.ORANGE,
): Wall(tile, color, texture) {
    override val type: ComponentType = ComponentType.FUNGUS
    override val isWall: Boolean = true


    fun spread(ship: Ship){
        forEachNeighbor(ship) {neighbor ->
            if ((neighbor?.type ?: ComponentType.EMPTY) == ComponentType.WALL && Random.nextInt(1,100) <= 10){
                var surrounded = true
                (neighbor as Wall).forEachNeighbor(ship){
                    if (!surrounded) return@forEachNeighbor
                    surrounded = it?.isWall == true
                }
                if (!surrounded) ship.numFungus++
                neighbor.tile.component = Fungo(neighbor.tile)
            }

        }
    }

    override fun die(){
        tile.component = Wall(tile, Color.WHITE, texture)
    }
}