package trabalhofinal.components

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.utils.RayCaster

class ShipRenderer(
    private val renderer: ShapeRenderer,
    private val camera: Camera,
    private val shader: ShaderProgram,
    private val minimapRatio: Float
) {

    fun renderShip(
        rayCastIsMinimap: Boolean,
        rayCaster: RayCaster,
        player: Player,
        tiles: List<List<IMapDrawable>>,
        alien: Component,
    ){
        if (!rayCastIsMinimap){
            val collisionPoints = drawRayCastAndGetCollisions(rayCaster)
            alien.render(shader, ratio = minimapRatio)
            drawTileMinimap(player, tiles, collisionPoints)

        }
    }

    private fun drawRayCastAndGetCollisions(rayCaster: RayCaster): List<Vector2>{

        rayCaster.meshes.render(camera, shader, ratio = minimapRatio)
        rayCaster.meshes.dispose()

        return rayCaster.collisionPoints

    }

    private fun drawTileMinimap(player: Player, tiles: List<List<IMapDrawable>>, collisionPoints: List<Vector2>){
        val minimapRect = Rectangle(5f, HEIGHT - HEIGHT *minimapRatio - 5f, WIDTH *minimapRatio, HEIGHT *minimapRatio)
        val mirroredX = minimapRect.x + minimapRect.width
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(minimapRect.x, minimapRect.y, minimapRect.width, minimapRect.height)
            renderer.color = Color.LIGHT_GRAY
            collisionPoints.forEach{
                renderer.rectLine(
                    mirroredX - player.x*minimapRatio,
                    minimapRect.y + player.y*minimapRatio,
                    mirroredX - it.x*minimapRatio,
                    minimapRect.y + it.y*minimapRatio, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    if (tile.color != Color.BLACK){
                        tile.draw(mirroredX, minimapRect.y, minimapRatio, renderer)
                    }
                }
            }
            renderer.color = Color.BROWN
            //TODO("Make player I minimapDrawable")
            renderer.circle(mirroredX - player.x*minimapRatio, minimapRect.y + player.y*minimapRatio, player.radius*minimapRatio)
        }
    }


}