package trabalhofinal.components.general

import com.badlogic.gdx.graphics.g2d.Batch

interface MapBatchDrawable {
    fun draw(startX: Float, startY: Float, ratio: Float, batch: Batch)

}