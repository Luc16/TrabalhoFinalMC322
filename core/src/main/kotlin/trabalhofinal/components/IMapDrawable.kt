package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface IMapDrawable {
    fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}