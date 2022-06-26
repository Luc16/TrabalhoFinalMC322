package trabalhofinal.components.general

import com.badlogic.gdx.graphics.glutils.ShaderProgram
import trabalhofinal.components.Egg
import trabalhofinal.components.Player
import trabalhofinal.components.RayCastCompList

interface ComponentShip {
    val tileWidth: Float
    val tileHeight: Float
    var numFungi: Int
    val numEggs: Int
    val players: MutableList<Player>
    val drawableTiles: List<List<DrawableTile>>
    val components: RayCastCompList

    operator fun get(i: Int, j: Int): RayCastTile

    fun renderComponents(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f)
    fun addEgg(egg: Egg)
}