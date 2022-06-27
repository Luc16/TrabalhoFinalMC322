package trabalhofinal.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import trabalhofinal.exceptions.NumFungiNotSpecifiedException

class MapReader(filePath: String) {
    private val file: FileHandle = Gdx.files.local(filePath)

    fun contents(): List<String> {
        val r = file.reader()
        val mapString = r.readLines()
        try {
            mapString[0].toInt()
        } catch (e: NumberFormatException){
            throw NumFungiNotSpecifiedException(mapString[0])
        }

        if (mapString[0].length > 3) throw NumFungiNotSpecifiedException(mapString[0])
        return mapString.reversed()

    }
}