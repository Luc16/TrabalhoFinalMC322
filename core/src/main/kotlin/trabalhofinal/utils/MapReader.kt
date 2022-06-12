package trabalhofinal.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

class MapReader(filePath: String) {
    private val file: FileHandle = Gdx.files.local(filePath)

    // TODO melhorar

    fun contents(): List<String> {
        val r = file.reader()
        return r.readLines()
    }
}