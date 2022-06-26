package trabalhofinal.components.general

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

interface DrawableTile: MapShapeDrawable {

    fun drawOutline(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}