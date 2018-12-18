package day18

import helper.countType
import helper.performTicksLooped

enum class Acre(val symbol: Char) {
    OPEN('.') {
        override fun getNext(adjacent: List<Acre>): Acre {
            val count = adjacent.count { it == TREES }
            return if (count >= 3) TREES else OPEN
        }

    },
    TREES('|') {
        override fun getNext(adjacent: List<Acre>): Acre {
            val count = adjacent.count { it == LUMBER }
            return if (count >= 3) LUMBER else TREES
        }
    },
    LUMBER('#') {
        override fun getNext(adjacent: List<Acre>): Acre {
            val treeCount = adjacent.count { it == TREES }
            val lumberCount = adjacent.count { it == LUMBER }
            return if (treeCount == 0 || lumberCount == 0) OPEN else LUMBER
        }
    };

    abstract fun getNext(adjacent: List<Acre>): Acre

    companion object {
        fun ofSymbol(symbol: Char): Acre {
            return when (symbol) {
                OPEN.symbol -> OPEN
                TREES.symbol -> TREES
                LUMBER.symbol -> LUMBER
                else -> throw IllegalArgumentException("Unknown symbol $symbol")
            }
        }
    }
}

private typealias Map = List<List<Acre>>

class ConstructionProject(val initialState: Map) {

    fun partA(): Int {
        val state = performTicksLooped(10, initialState, this::transformMap)

        return resourceValue(state)
    }

    fun partB(): Int {
        val state = performTicksLooped(1_000_000_000, initialState, this::transformMap)

        return resourceValue(state)
    }

    private fun printState(i: Int, state: Map) {
        println("After $i minutes:")

        state.forEach { line ->
            println(line.joinToString(separator = "") { it.symbol.toString() })
        }

        println("")
    }

    private fun resourceValue(state: Map) =
        state.countType(Acre.TREES) * state.countType(Acre.LUMBER)

    private fun transformMap(state: Map): Map {
        val newState = mutableListOf<List<Acre>>()
        for ((y, row) in state.withIndex()) {
            val newRow = mutableListOf<Acre>()

            for ((x, acre) in row.withIndex()) {
                val adjacent = state.getAdjacent(y, x)
                newRow.add(acre.getNext(adjacent))
            }

            newState.add(newRow)
        }
        return newState
    }

    companion object {
        fun build(lines: List<String>): ConstructionProject {
            return ConstructionProject(lines.map { line ->
                line.map { Acre.ofSymbol(it) }
            })
        }
    }
}

private fun <T> List<List<T>>.getAdjacent(first: Int, second: Int): List<T> {
    val adjacentIndices = listOf(
        first - 1 to second - 1,
        first - 1 to second,
        first - 1 to second + 1,
        first to second - 1,
        first to second + 1,
        first + 1 to second - 1,
        first + 1 to second,
        first + 1 to second + 1
    ).filter { (first, second) ->
        first >= 0
                && second >= 0
                && first < this.size
                && second < this[first].size
    }
    return adjacentIndices.map { (first, second) -> this[first][second] }
}
