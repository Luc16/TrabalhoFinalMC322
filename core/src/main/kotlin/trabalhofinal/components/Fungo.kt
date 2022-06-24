package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import ktx.graphics.color
import trabalhofinal.components.general.Component

class Fungo(
    override val isWall: Boolean = true,
    override val texture: Texture? = null, //TODO COLOCAR TEXTURA
    override var color: Color = Color.ORANGE,
    override val type: ComponentType = ComponentType.FUNGUS
): Component{


    //a ideia é que tenha uma lista de fungos na ship (?) e chame o metodo de cada
    // um dos fungos passando a coordenada i e j deles
    fun spread(tiles: List<List<Tile>>, i: Int, j: Int){
        if(i - 1 >= 0){
            if (tiles[i-1][j].component!!.type == ComponentType.WALL)
                tiles[i-1][j].component = Fungo()
        }
        if(i + 1 < tiles.size){
            if (tiles[i+1][j].component!!.type == ComponentType.WALL)
                tiles[i+1][j].component = Fungo()
        }
        if(j - 1 >= 0){
            if (tiles[i][j-1].component!!.type == ComponentType.WALL)
                tiles[i][j-1].component = Fungo()
        }
        if(j + 1 < tiles[0].size){
            if (tiles[i][j+1].component!!.type == ComponentType.WALL)
                tiles[i][j+1].component = Fungo()
        }
    }
}