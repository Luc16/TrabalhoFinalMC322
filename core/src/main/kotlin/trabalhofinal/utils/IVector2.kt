package trabalhofinal.utils

class IVector2(var i: Int, var j: Int) {

    operator fun minus(other: IVector2): IVector2 = IVector2(i - other.i, j - other.j)
    
    override fun toString(): String {
        return "($i, $j)"
    }

}
