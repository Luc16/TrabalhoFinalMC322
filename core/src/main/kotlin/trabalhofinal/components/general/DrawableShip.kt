package trabalhofinal.components.general

import com.badlogic.gdx.graphics.glutils.ShaderProgram


interface DrawableShip {
    val drawableTiles: List<List<DrawableTile>>
    val components: MapBatchDrawable
    var numFungi: Int
    val maxFungi: Int
    val numEggs: Int

    fun renderComponents(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f)

}