package trabalhofinal.components.general

import trabalhofinal.components.Egg

interface ComponentShip {
    val tileWidth: Float
    val tileHeight: Float
    val players: MutableList<Player>
    var numFungi: Int

    operator fun get(i: Int, j: Int): RayCastTile
    fun removeComponent(comp: Component)
    fun addEgg(egg: Egg)
}