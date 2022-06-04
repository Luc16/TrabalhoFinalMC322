package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.utils.graphics.QuadGroup
import trabalhofinal.utils.graphics.Textured2DQuad
import trabalhofinal.utils.graphics.fragmentShader
import trabalhofinal.utils.graphics.vertexShader
import kotlin.math.max
import kotlin.math.min


class MeshTestScreen(game: MyGame) : CustomScreen(game) {
    private val t = Texture(Gdx.files.local("assets/wolftex/pics/mossy.png"))
    private val t2 = Texture(Gdx.files.local("assets/wolftex/pics/colorstone.png"))
    private val quads = QuadGroup(
        ShaderProgram(vertexShader, fragmentShader),
        listOf(
            Textured2DQuad(t, floatArrayOf(
                200f, 660f, 0f, 0f,//upper left
                280f, 300f, 0f, 1f,//lower left
                600f, 660f, 1f, 0f, //upper right
                520f, 300f, 1f, 1f //lower right
            )),
            Textured2DQuad(t2, floatArrayOf(
                500f, 300f, 0f, 0f,//upper left
                500f, 150f, 0f, 1f,//lower left
                750f, 300f, 1f, 0f, //upper right
                750f, 150f, 1f, 1f //lower right
            )),
        )
    )
    private val camera: Camera = viewport.camera
    private val circles = listOf(
        Circle(200f, 660f, 10f),
        Circle(280f, 300f, 10f),
        Circle(600f, 660f, 10f),
        Circle(520f, 300f, 10f),
    )
    private val textures = listOf(
        Texture(Gdx.files.local("assets/wolftex/pics/eagle.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/redbrick.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/purplestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/greystone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/bluestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/mossy.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/wood.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/colorstone.png")),
    )
    private var selectedIdx = -1
    private var idx = 0

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()
        idx = if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) (idx + 1)%textures.size else idx
        quads[0].run {
            texture = textures[idx]
            setVertices(floatArrayOf(
                circles[0].x, circles[0].y, 0f, 0f, //upper left
                circles[1].x, circles[1].y, 0f, 1f, //lower left
                circles[2].x, circles[2].y, 1f, 0f, //upper right
                circles[3].x, circles[3].y, 1f, 1f, //lower right
            ))
        }

        moveCircles()
        quads.render(camera)
        val tr = TextureRegion(t, 4, 4, 30, 30)

        batch.use {
            it.draw(tr.texture, 10f ,10f)
        }

        renderer.use(ShapeRenderer.ShapeType.Filled) {
            renderer.color = Color.WHITE
            circles.forEach {
                renderer.circle(it.x, it.y, it.radius)
            }
        }

    }

    override fun dispose() {
        quads.dispose()
        t.dispose()
        t2.dispose()
    }

    private fun moveCircles() {
        val mouse = Vector2(
            max(0f, min(Gdx.input.x.toFloat(), WIDTH)),
            max(0f, min(HEIGHT - Gdx.input.y.toFloat(), HEIGHT))
        )

        circles.forEachIndexed { i, it ->
            if (it.contains(mouse) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                selectedIdx = i
            }
        }
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) selectedIdx = -1
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && selectedIdx != -1) {
            circles[selectedIdx].x = mouse.x
            circles[selectedIdx].y = mouse.y
        }
    }

}