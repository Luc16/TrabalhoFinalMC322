package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.clearScreen
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Player
import trabalhofinal.utils.IVector2
import kotlin.math.roundToInt


class MovementTestScreen(game: MyGame): CustomScreen(game){
    private val players = mutableListOf<Player>()
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 1f
    private var tileHeight = 1f
    init{
        val pl = Player(50f, 50f, 10f)
        pl.pos = IVector2(1,1)
        players.add(pl)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) mouseController(players[0]) //TODO: implementar qual o player atual
        clearScreen(1f, 1f, 1f, 1f)

        for (player in players){
            update(player)
        }

        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(0f, 0f, WIDTH, HEIGHT)
            renderer.color = Color.LIGHT_GRAY
            println("${players[0].x*(tileWidth) + 0.5f*tileWidth} ${players[0].y*(tileHeight) + 0.5f*tileHeight}")
            renderer.circle(players[0].x*(tileWidth) + 0.5f*tileWidth, players[0].y*(tileHeight) + 0.5f*tileHeight, 10f)
        }
    }

    private fun mouseController(currPlayer: Player) {
        val xPos = Gdx.input.x
        val yPos = HEIGHT.toInt() - Gdx.input.y
        if (currPlayer.x.roundToInt() != xPos || currPlayer.y.roundToInt() != yPos){ //adaptar para checar se esta no mesmo tile
            currPlayer.isMoving = true
            currPlayer.currxdest = xPos
            currPlayer.currydest = yPos
        }

        // if (!p.moving) path++ ; moveDnv
//        fun setDest(i, j)
//        fun move(){
//            if (p.pos != dest){
//                p.y += movY
//                p.x += movX
//            }
//        }

    }

    private fun update(player: Player){
        if (!player.isMoving) return
        if (player.x.roundToInt() == player.currxdest && player.y.roundToInt() == player.currydest){
            player.isMoving = false
        } else{
            if (player.x.roundToInt() != player.currxdest){
                if (player.x > player.currxdest){
                    player.x -= 1f
                    return
                } else{
                    player.x += 1f
                    return
                }
            } else{
                if (player.y.roundToInt() > player.currydest){
                    player.y -= 1f
                    return
                } else{
                    player.y += 1f
                    return
                }
            }
        }
    }
}