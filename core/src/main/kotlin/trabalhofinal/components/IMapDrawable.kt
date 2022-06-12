package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface IMapDrawable: IPositioned {
    val color: Color
    val width: Float
    val height: Float

    fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}