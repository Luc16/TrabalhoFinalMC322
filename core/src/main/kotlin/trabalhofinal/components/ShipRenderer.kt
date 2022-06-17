package trabalhofinal.components

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.app.clearScreen
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.utils.RayCaster
import trabalhofinal.utils.graphics.MeshGroup

class ShipRenderer(
    private val renderer: ShapeRenderer,
    private val camera: Camera,
    private val shader: ShaderProgram,
    private val minimapRatio: Float
) {

    private val mapRatio = 0.75f

    fun renderShip(
        rayCastIsMinimap: Boolean,
        rayCaster: RayCaster,
        player: Player,
        tiles: List<List<IMapDrawable>>,
        components: RayCastCompList,
    ){
        clearScreen(0f, 0f, 0f, 1f)
        if (!rayCastIsMinimap){
            drawRayCast(rayCaster.meshes, false, rayCaster.floorLevel)
            components.render(shader)
            drawTileMap(player, tiles, rayCaster.collisionPoints, true, 5f, HEIGHT - HEIGHT*minimapRatio - 5f)
        } else {
            val minimapX = WIDTH*mapRatio
            val minimapY = HEIGHT*mapRatio
            drawRayCast(rayCaster.meshes, true, rayCaster.floorLevel, minimapX, minimapY)
            components.render(shader, minimapX, minimapY, 0.25f)
            drawSideThings(minimapY)
            drawTileMap(player, tiles, rayCaster.collisionPoints, false)
        }
    }

    private fun drawSideThings(initialY: Float) {
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(WIDTH - WIDTH*0.25f, 0f, WIDTH*0.25f, initialY)
        }

    }

    private fun drawRayCast(meshes: MeshGroup, isMinimap: Boolean, floor: Float, initialX: Float = 0f, initialY: Float = 0f) {
        val ratio = if (isMinimap) 0.25f else 1f
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.WHITE
            renderer.rect(initialX, initialY, WIDTH*ratio, floor*ratio)
        }
        meshes.render(camera, shader, initialX, initialY, ratio)
        meshes.dispose()
    }

    private fun drawTileMap(
        player: Player,
        tiles: List<List<IMapDrawable>>,
        collisionPoints: List<Vector2>,
        isMinimap: Boolean,
        initialX: Float = 0f,
        initialY: Float = 0f
    ){
        val ratio = if (isMinimap) minimapRatio else mapRatio
        val minimapRect = Rectangle(initialX, initialY, WIDTH*ratio, HEIGHT*ratio)
        val mirroredX = minimapRect.x + minimapRect.width
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(minimapRect.x, minimapRect.y, minimapRect.width, minimapRect.height)
            renderer.color = Color.LIGHT_GRAY
            collisionPoints.forEach{
                renderer.rectLine(
                    mirroredX - player.x*ratio,
                    minimapRect.y + player.y*ratio,
                    mirroredX - it.x*ratio,
                    minimapRect.y + it.y*ratio, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(mirroredX, minimapRect.y, ratio, renderer)
                }
            }
            renderer.color = Color.BROWN
            //TODO("Make player I minimapDrawable")
            renderer.circle(mirroredX - player.x*ratio, minimapRect.y + player.y*ratio, player.radius*ratio)
        }
    }


}