package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.*
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.*
import trabalhofinal.utils.graphics.fragmentShader
import trabalhofinal.utils.graphics.vertexShader
import kotlin.math.PI


class RayCastingTestScreen(game: MyGame): CustomScreen(game), InputProcessor {

    private val shader = ShaderProgram(vertexShader, fragmentShader)
    private val shipRenderer = ShipRenderer(renderer, batch, font, viewport.camera, shader, 0.25f)
    private val textures = listOf(
        Texture(Gdx.files.local("assets/wolftex/pics/eagle.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/redbrick.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/purplestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/greystone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/trig.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/mossy.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/wood.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/colorstone.png")),
    )
    private val ship = Ship("assets/maps/test.map", textures)
    private val rayCaster = RayCaster(ship.tiles, ship.tileWidth, ship.tileHeight)
    private var players = mutableListOf<Player>()
    private var selectedPlayer: Player
    private val components = RayCastCompList()
    private var rayCastIsMinimap = true
    private val endTurnButton: Button = Button(
        "End Turn",
        (shipRenderer.mapRatio + shipRenderer.minimapRatio/2) * WIDTH,
        (shipRenderer.mapRatio) * HEIGHT - 80f/2 - 10f,
        WIDTH*shipRenderer.mapRatio - 10f,
        80f,
        { endTurn() }
    )

    init {
        val p1 = Player(ship[21, 4], 10f, ship.tileWidth, ship.tileHeight,
            Texture(Gdx.files.local("assets/wolftex/pics/alien.png")),
        )
        selectedPlayer = p1
        players.add(p1)
        val p2 = Player(ship[18, 4],10f, ship.tileWidth, ship.tileHeight,
            Texture(Gdx.files.local("assets/wolftex/pics/alien.png")),
        )
        players.add(p2)

        // adiciona os componentes
        run {
            components.add(p1)
            components.add(p2)
            components.add(
                RayCastComponent(
                    Texture(Gdx.files.local("assets/wolftex/pics/alien.png")),
                    ship.tileWidth * 21 + ship.tileWidth / 2, ship.tileHeight * 12 + ship.tileHeight / 2,
                    Color.BROWN, ship[21, 12], ComponentType.ALIEN
                )
            )

            components.add(
                RayCastComponent(
                    Texture(Gdx.files.local("assets/wolftex/pics/barrel-no-bg.png")),
                    ship.tileWidth * 20 + ship.tileWidth / 2, ship.tileHeight * 10 + ship.tileHeight / 2,
                    Color.BROWN, ship[20, 10], ComponentType.EGG
                )
            )

            components.add(
                RayCastComponent(
                    Texture(Gdx.files.local("assets/wolftex/pics/squareweb.png")),
                    ship.tileWidth * 22 + ship.tileWidth / 2, ship.tileHeight * 2 + ship.tileHeight / 2,
                    Color.BROWN, ship[22, 2], ComponentType.DOOR
                )
            )
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Keys.Q)) Gdx.app.exit()
        if (Gdx.input.isKeyJustPressed(Keys.M)) game.setScreen<MenuScreen>()

        // sempre fazer o raycast antes de criar as meshes dos componentes!
        rayCaster.multipleRayCast3D(selectedPlayer)
        components.createMeshes(selectedPlayer, rayCaster.zBuffer, ship.tileWidth, ship.tileHeight)
        val playerPos = ship.getTilePos(
            selectedPlayer.y - selectedPlayer.dir.y*ship.tileHeight/2,
            selectedPlayer.x - selectedPlayer.dir.x*ship.tileWidth/2
        )

        selectedPlayer.update(ship.tileWidth, ship.tileHeight, shipRenderer.mapRatio, ship[playerPos.i, playerPos.j])
        players.forEach {
            if (it != selectedPlayer) it.update(ship.tileWidth, ship.tileHeight, shipRenderer.mapRatio)
        }

        // TODO isso é agir do player
        if (selectedPlayer.targetComponent.type == ComponentType.DOOR && Gdx.input.isButtonPressed(Buttons.LEFT)){
            selectedPlayer.targetComponent.color = Color.BLACK
            (selectedPlayer.targetComponent.component as RayCastComponent).die()
            components.remove(selectedPlayer.targetComponent.component)
        }

        shipRenderer.renderShip(
            rayCastIsMinimap,
            rayCaster,
            players,
            selectedPlayer,
            ship.tiles,
            components,
            endTurnButton
        )

    }


    // TODO colocar essa função numa classe "SelectedPlayer"
    private fun changePlayer(player: Player){
        selectedPlayer.color = Color.LIGHT_GRAY
        selectedPlayer = player
        player.color = Color.RED
    }

    private fun endTurn(){
        if (!endTurnButton.hovered) return

        players.forEach { it.reset() }
    }

    private fun toggleViewMode(){
        rayCastIsMinimap = !rayCastIsMinimap
        Gdx.input.isCursorCatched = !rayCastIsMinimap
    }

    private fun mouseController(activePlayer: Player?, mouse: Vector2) {
        if (activePlayer == null || activePlayer.isMoving) return

        val xPos = WIDTH - mouse.x/shipRenderer.mapRatio
        val yPos = mouse.y/shipRenderer.mapRatio

        val dest = ship.getTilePos(yPos, xPos)

        val currTile = ship[dest.i, dest.j]

        if (currTile.component == null){
            val graph = AStar(ship.tiles)
            selectedPlayer.calculatePath(dest, graph)
        } else if (currTile.component?.let { it.type == ComponentType.PLAYER } == true){
            changePlayer(currTile.component as Player)
        }
    }

    override fun dispose() {
        shader.dispose()
        textures.forEach { it.dispose() }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.SPACE) toggleViewMode()
        return true
    }
    override fun keyUp(keycode: Int): Boolean = true
    override fun keyTyped(character: Char): Boolean = true
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val mouse = unprojectedMouse(screenX, screenY)
        if (mouse.x >= shipRenderer.mapRatio * WIDTH && mouse.y >= shipRenderer.mapRatio * HEIGHT)
            toggleViewMode()
        if (rayCastIsMinimap){
            mouseController(selectedPlayer, mouse)
            endTurnButton.onPress()
        }

        return true
    }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        endTurnButton.onRelease()
        return true
    }
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val mouse = unprojectedMouse(screenX, screenY)
        if (!rayCastIsMinimap) {
            val theta = (2 * PI / 180).toFloat()
            // mouse invisivel
            val deltaX = (mouse.x - WIDTH/2) / 600

            selectedPlayer.rotate(deltaX * theta)
            Gdx.input.setCursorPosition((WIDTH / 2).toInt(), (HEIGHT / 2).toInt())
        }
        return true
    }
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val mouse = unprojectedMouse(screenX, screenY)
        if (!rayCastIsMinimap) {
            val theta = (2 * PI / 180).toFloat()
            // mouse invisivel
            val deltaX = (mouse.x - WIDTH/2) / 100

            selectedPlayer.rotate(deltaX * theta)
            Gdx.input.setCursorPosition((WIDTH / 2).toInt(), (HEIGHT / 2).toInt())
        } else {
            endTurnButton.checkHover(mouse)
        }
        return true
    }
    override fun scrolled(amountX: Float, amountY: Float): Boolean = true

}