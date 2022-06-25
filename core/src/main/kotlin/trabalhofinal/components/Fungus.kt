package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.IRayCastTile
import kotlin.random.Random

class Fungus(
    tile: IRayCastTile,
    texture: Texture,
    private val wallTexture: Texture,
    color: Color = Color.ORANGE,
): Wall(tile, color, texture) {
    override val type: ComponentType = ComponentType.FUNGUS
    override val isWall = true

    fun spread(ship: Ship){
        forEachNeighbor(ship) {neighbor ->
            if ((neighbor?.type ?: ComponentType.EMPTY) == ComponentType.WALL && Random.nextInt(1,100) <= 10){
                var surrounded = true
                (neighbor as Wall).forEachNeighbor(ship){
                    if (!surrounded) return@forEachNeighbor
                    surrounded = it?.isWall == true
                }
                if (!surrounded) ship.numFungus++
                neighbor.tile.component = texture?.let {
                    val fungus = Fungus(neighbor.tile, it, wallTexture)
                    ship.addFungusLazy(fungus)
                    fungus
                }
            }

        }
    }

    override fun die(){
        tile.component = Wall(tile, Color.WHITE, wallTexture)
    }
}