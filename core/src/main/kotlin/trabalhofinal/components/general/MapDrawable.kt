package trabalhofinal.components.general

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface MapDrawable {
    fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}