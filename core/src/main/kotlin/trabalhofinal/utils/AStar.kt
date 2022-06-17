package trabalhofinal.utils

import kotlin.math.abs



class AStar (private val grid: List<List<Node>>) {

    private val changed = mutableListOf<Node>()

    private fun IVector2.isOutOfRange(): Boolean = i < 0 || i >= grid.size || j < 0 || j >= grid[0].size
    private fun IVector2.isBlocked(): Boolean = grid[i][j].notTraversable
    private fun getNode(pos: IVector2): Node = grid[pos.i][pos.j]


    private fun MutableList<Node>.nextNode(): Node{
        var smaller = this[0]
        for (i in 1 until this.size) {
            if (this[i].f < smaller.f){
                smaller = this[i]
            }
        }
        return smaller
    }

    private fun Node.forEachNeighbor(func: (Node) -> Unit){
        listOf(-1, 1).forEach {idx ->
            if (idx + j in 0 until  grid[0].size)
                func(grid[i][idx + j])
            if (idx + i in grid.indices)
                func(grid[idx + i][j])
        }
    }

    private fun resultPath(end: Node): List<IVector2>? {
        var node: Node? = end.parent
        if (node == null){
            resetGrid()
            return null
        }
        val path = mutableListOf(end.pos, node.pos)

        while (node?.parent != null) {
            node = node.parent
            if (node != null) path.add(node.pos)
        }
        resetGrid()
        return path.reversed()
    }

    private fun resetGrid(){
        grid.forEach { line ->
            line.forEach { node ->
                node.reset()
            }
        }
    }

    fun findPath(source: IVector2, dest: IVector2): List<IVector2>?{
        // ver se a posicao destino eh valida
        if (dest.isOutOfRange() || source.isOutOfRange() || dest.isBlocked() || source.isBlocked()) return null


        val src = getNode(source)
        val end = getNode(dest)
        src.g = 0
        src.h = 0
        val open = mutableListOf(src)
        changed.add(src)

        while (open.isNotEmpty()){
            val node = open.nextNode()
            if (node == end) {
                return resultPath(node)
            }

            open.remove(node)
            node.wasVisited = true

            node.forEachNeighbor { neighbor ->
                // se eh parede ou visinho possuir componente bloqueador, return
                if (neighbor.notTraversable || neighbor.wasVisited) return@forEachNeighbor

                if (neighbor.parent == null || neighbor.parent?.let {it.g > node.g} == true){
                    neighbor.parent = node
                    neighbor.g = node.g + 1
                    open.add(neighbor)
                    changed.add(node)
                }
                if (neighbor.h == Int.MAX_VALUE){
                    neighbor.h = abs(neighbor.i - end.i) + abs(neighbor.j - end.j)
                }
            }
        }
        println("Nao achei caminho")
        return null
    }

}
