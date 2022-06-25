package trabalhofinal.components

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.app.clearScreen
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.general.IMapDrawable
import trabalhofinal.utils.Button
import trabalhofinal.utils.RayCaster
import trabalhofinal.utils.graphics.MeshGroup

class ShipRenderer(
    private val renderer: ShapeRenderer,
    private val batch: Batch,
    private val font: BitmapFont,
    private val camera: Camera,
    private val shader: ShaderProgram,
    val minimapRatio: Float
) {

    val mapRatio = 1 - minimapRatio

    fun renderShip(
        rayCastIsMinimap: Boolean,
        rayCaster: RayCaster,
        ship: Ship,
        selectedPlayer: Player,
        endTurnButton: Button
    ){
        clearScreen(0f, 0f, 0f, 1f)
        if (!rayCastIsMinimap){
            drawRayCast(rayCaster.meshes, false, rayCaster.floorLevel)
            ship.components.render(shader)
            drawTileMap(selectedPlayer, ship.players, ship.tiles, rayCaster.collisionPoints,
                true,
                WIDTH - WIDTH*minimapRatio - 5f,
                HEIGHT - HEIGHT*minimapRatio - 5f)
        } else {
            val minimapX = WIDTH*mapRatio
            val minimapY = HEIGHT*mapRatio
            drawRayCast(rayCaster.meshes, true, rayCaster.floorLevel, minimapX, minimapY)
            ship.components.render(shader, minimapX, minimapY, minimapRatio)
            drawSideThings(minimapY, endTurnButton, selectedPlayer)
            drawTileMap(selectedPlayer, ship.players, ship.tiles, rayCaster.collisionPoints, false)
        }
    }

    private fun drawSideThings(initialY: Float, button: Button, selectedPlayer: Player) {
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(WIDTH - WIDTH*minimapRatio, 0f, WIDTH*minimapRatio, initialY)
            button.drawRect(renderer)
        }
        batch.use(camera.combined){
            button.drawMessage(batch, font)
            val fungusText = GlyphLayout(font, "Fungus: $0/$10")
            font.draw(batch, fungusText, button.x + button.width + 5f, (1 - minimapRatio/3) * HEIGHT + fungusText.height/2)
            val eggText = GlyphLayout(font, "Eggs: $0/$10")
            font.draw(batch, eggText, button.x + button.width + 5f, (1 - 2*minimapRatio/3) * HEIGHT + eggText.height/2)

            val name = GlyphLayout(font, selectedPlayer.name)
            val nameY = (1 - minimapRatio) * HEIGHT - name.height/2 - 10f
            font.draw(batch, name, WIDTH*(mapRatio + minimapRatio/2) - name.width/2, nameY)

            font.data.setScale(2f)

            val energy = GlyphLayout(font, "Current energy: ${selectedPlayer.energy}")
            val energyY = nameY - 80f
            font.draw(batch, energy, WIDTH*mapRatio + 5f, energyY)

            val maxE = GlyphLayout(font, "Max energy: ${selectedPlayer.maxEnergy}")
            val maxEY = energyY - energy.height - 20f
            font.draw(batch, maxE, WIDTH*mapRatio + 5f, maxEY)

            val eggE = GlyphLayout(font, "Egg energy: ${selectedPlayer.eggEnergy}")
            val eggEY = maxEY - maxE.height- 20f
            font.draw(batch, eggE, WIDTH*mapRatio + 5f, eggEY)

            val webE = GlyphLayout(font, "Web energy: ${selectedPlayer.webEnergy}")
            val webEY = eggEY - eggE.height - 20f
            font.draw(batch, webE, WIDTH*mapRatio + 5f, webEY)

            val fungE = GlyphLayout(font, "Web energy: ${selectedPlayer.fungusEnergy}")
            val fungEY = webEY - webE.height - 20f
            font.draw(batch, fungE, WIDTH*mapRatio + 5f, fungEY)

            font.data.setScale(3f)

        }

    }

    private fun drawRayCast(meshes: MeshGroup, isMinimap: Boolean, floor: Float, initialX: Float = 0f, initialY: Float = 0f) {
        val ratio = if (isMinimap) minimapRatio else 1f
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            renderer.color = Color.WHITE
            renderer.rect(initialX, initialY, WIDTH*ratio, floor*ratio)
        }
        meshes.render(camera, shader, initialX, initialY, ratio)
        meshes.dispose()
    }

    private fun drawTileMap(
        selectedPlayer: Player,
        players: List<Player>,
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
                    mirroredX - selectedPlayer.x*ratio,
                    minimapRect.y + selectedPlayer.y*ratio,
                    mirroredX - it.x*ratio,
                    minimapRect.y + it.y*ratio, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(mirroredX, minimapRect.y, ratio, renderer)
                }
            }
            renderer.color = Color.BROWN
            players.forEach {
                it.draw(mirroredX, minimapRect.y, ratio, renderer)
            }
        }
    }


}