package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import ktx.graphics.color
import trabalhofinal.components.general.Component
import kotlin.random.Random

class Fungo(
    override val isWall: Boolean = true,
    override val texture: Texture? = null, //TODO COLOCAR TEXTURA
    override var color: Color = Color.ORANGE,
    override val type: ComponentType = ComponentType.FUNGUS
): Component{


    //a ideia é que tenha uma lista de fungos na ship (?) e chame o metodo de cada
    // um dos fungos passando a coordenada i e j deles
    fun spread(ship: Ship, i: Int, j: Int){
        val value = Random.nextInt(1, 100)
        if (value <= 10){
            if(i - 1 >= 0){
                if (ship.tiles[i-1][j].component!!.type == ComponentType.WALL)
                    ship.tiles[i-1][j].component = Fungo()
            }
            if(i + 1 < ship.tiles.size){
                if (ship.tiles[i+1][j].component!!.type == ComponentType.WALL)
                    ship.tiles[i+1][j].component = Fungo()
            }
            if(j - 1 >= 0){
                if (ship.tiles[i][j-1].component!!.type == ComponentType.WALL)
                    ship.tiles[i][j-1].component = Fungo()
            }
            if(j + 1 < ship.tiles[0].size){
                if (ship.tiles[i][j+1].component!!.type == ComponentType.WALL)
                    ship.tiles[i][j+1].component = Fungo()
            }
        }
    }

    override fun die(){ //setar WALL como componente do tile

    }
}