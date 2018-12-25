package day15

import java.util.*
import java.util.Comparator.comparing

data class Point(val x: Int, val y: Int)

val PATH_COMPARATOR: Comparator<Pair<Cell, Int>> = comparing { path: Pair<Cell, Int> -> path.second }
    .thenComparing { path: Pair<Cell, Int> -> path.first.position.y }
    .thenComparing { path: Pair<Cell, Int> -> path.first.position.x }

val ENEMY_COMPARATOR: Comparator<UnitCell> = comparing(UnitCell::hp)
    .thenComparing { it -> it.position.y }
    .thenComparing { it -> it.position.x }

fun findBestElvenAttackScore(boardLines: List<String>): Int {

    var score = 3
    var result: Pair<Int, Char>
    do {
        score += 1
        val combat = Combat(boardLines, score)
        result = combat.simulate()
    } while (result.second == 'G' || combat.elves.any { !it.isAlive })

    return result.first
}

class Combat(boardLines: List<String>, elvenAttackScore: Int = 3) {

    private val width = boardLines[0].length
    private val height = boardLines.size

    val board: MutableList<Cell>
    private var rounds = 0
    val elves = mutableSetOf<UnitCell>()

    init {
        board = boardLines.mapIndexed { y, line ->
            line.mapIndexed { x, char ->
                when (char) {
                    '#' -> WallCell(Point(x, y))
                    '.' -> EmptyCell(Point(x, y))
                    'G' -> {
                        val unit = UnitCell(Point(x, y), char, combat = this)
                        unit
                    }
                    'E' -> {
                        val unit = UnitCell(Point(x, y), char, elvenAttackScore, combat = this)
                        elves.add(unit)
                        unit
                    }
                    else -> throw IllegalArgumentException("Unknown cell $char at ($x,$y)")
                }
            }
        }.flatten().toMutableList()
    }

    private fun round(): Boolean = board
        .filter { it is UnitCell }
        .map { it as UnitCell }
        .asSequence()
        .filter { it.isAlive }
        .fold(true) { result, unit -> result && unit.takeTurn() }

    fun simulate(): Pair<Int, Char> {
        do {
            val canContinue = round()
            rounds++
        } while (canContinue)

        val winningTeam = board.first { it is UnitCell }.type
        return Pair(score(), winningTeam)
    }

    private fun score(): Int {
        val hpTotal = board.filter { it is UnitCell }.sumBy { (it as UnitCell).hp }

        return hpTotal * (rounds - 1)
    }

    operator fun get(x: Int, y: Int): Cell = board[index(x, y)]
    operator fun get(point: Point): Cell = board[index(point.x, point.y)]

    private fun index(x: Int, y: Int) = y * width + x

    fun set(point: Point, cell: Cell) {
        board[index(point.x, point.y)] = cell
    }

    fun adjacentCells(point: Point): List<Cell> {
        val adjacent = mutableListOf<Cell>()
        if (point.y > 0) {
            adjacent.add(this[point.x, point.y - 1])
        }

        if (point.x > 0) {
            adjacent.add(this[point.x - 1, point.y])
        }

        if (point.x < width - 1) {
            adjacent.add(this[point.x + 1, point.y])
        }

        if (point.y < height - 1) {
            adjacent.add(this[point.x, point.y + 1])
        }

        return adjacent
    }

    override fun toString(): String {
        return "Round $rounds\n" + board.chunked(width).joinToString("\n") { it.joinToString("") } + "\n"
    }

    fun bfs(origin: UnitCell): Pair<Cell, Int>? {
        val pathInfo = mutableMapOf<Cell, Cell>()
        val visited = mutableSetOf<Cell>()
        val unvisited = sortedSetOf(PATH_COMPARATOR)

        for (cell in adjacentCells(origin.position)) {
            pathInfo[cell] = origin
            unvisited.add(Pair(cell, 1))
        }

        while (unvisited.isNotEmpty()) {
            val first = unvisited.first()
            unvisited.remove(first)
            val (subtreeRoot, distance) = first

            if (origin.isEnemy(subtreeRoot)) {
                return buildPath(pathInfo, subtreeRoot, origin)
            }

            if (subtreeRoot is EmptyCell) {
                for (adjacentCell in adjacentCells(subtreeRoot.position)) {
                    if (adjacentCell !in visited && !unvisited.any { it.first == adjacentCell }) {
                        pathInfo[adjacentCell] = subtreeRoot
                        unvisited.add(Pair(adjacentCell, distance + 1))
                    }
                }
            }

            visited.add(subtreeRoot)
        }

        return null
    }

    private fun buildPath(
        pathInfo: MutableMap<Cell, Cell>,
        subtreeRoot: Cell,
        origin: UnitCell
    ): Pair<Cell, Int> {
        var pathLength = 0
        var currentRoot: Cell = subtreeRoot

        while (pathInfo[currentRoot] != origin) {
            if (!pathInfo.contains(currentRoot)) {
                throw IllegalStateException("unable to reconstruct path from ${currentRoot.position}")
            } else {
                currentRoot = pathInfo[currentRoot]!!
            }
            pathLength++
        }

        return Pair(currentRoot, pathLength)
    }

}

