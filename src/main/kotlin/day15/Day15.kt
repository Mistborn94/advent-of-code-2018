package day15

import java.util.Comparator.comparing

data class Point(val x: Int, val y: Int)

val PATH_COMPARATOR: Comparator<List<Combat.Cell>> = comparing { path: List<Combat.Cell> -> path.size }
    .thenComparing { path: List<Combat.Cell> -> path[0].position.y }
    .thenComparing { path: List<Combat.Cell> -> path[0].position.x }

class Combat(boardLines: List<String>) {

    private val width = boardLines[0].length
    private val height = boardLines.size
    val board: MutableList<Cell>
    var rounds = 0

    init {
        board = boardLines.mapIndexed { y, line ->
            line.mapIndexed { x, char ->
                when (char) {
                    '#' -> WallCell
                    '.' -> EmptyCell(Point(x, y))
                    'G', 'E' -> Unit(Point(x, y), char)
                    else -> throw IllegalArgumentException("Unknown cell $char at ($x,$y)")
                }
            }
        }.flatten().toMutableList()
    }

    private fun round(): Boolean = board
        .filter { it is Unit }
        .map { it as Unit }
        .asSequence()
        .filter { it.isAlive }
        .fold(true) { result, unit -> result && unit.takeTurn() }

    fun simulate(): Int {

        do {
            println(this)
            val canContinue = round()
            rounds++
        } while (canContinue)

        println(this)
        return score(rounds)
    }

    private fun score(rounds: Int): Int {
        val hpTotal = board.filter { it is Unit }.sumBy { (it as Unit).hp }
        val score = hpTotal * (rounds - 1)

        println("$hpTotal x ${rounds - 1} = $score")

        return score
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

    abstract class Cell {
        abstract val position: Point
        abstract fun moveTowards(possibleEnemies: List<Cell>): List<List<Cell>>
    }

    inner class EmptyCell(override val position: Point) : Cell() {
        override fun toString(): String = "."
        var inPath = false

        //TODO: Optimise? I don't think I actually need the entire path here. Since we only actually care about the first step
        override fun moveTowards(possibleEnemies: List<Cell>): List<List<Cell>> {
            if (inPath) {
                return emptyList()
            }

            inPath = true

            val adjacentCells = adjacentCells(position)
            val paths = mutableListOf<List<Cell>>()

            for (adjacentCell in adjacentCells) {
                val subPaths = adjacentCell.moveTowards(possibleEnemies)

                paths.addAll(subPaths.map { listOf(this).plus(it) })
            }

            inPath = false
            return paths
        }
    }

    object WallCell : Cell() {
        override val position get() = throw NotImplementedError("Wall position not supported")

        override fun moveTowards(possibleEnemies: List<Cell>): List<List<Cell>> {
            return emptyList()
        }

        override fun toString(): String = "#"
    }

    inner class Unit(override var position: Point, var type: Char) : Cell() {
        var hp = 200
        private val attackScore = 3

        val isAlive get() = hp > 0

        override fun toString(): String = "$type"

        private fun enemies(): List<Cell> = board.filter(this::isEnemy)

        private fun isEnemy(it: Cell) = it is Unit && it.type != type

        fun takeTurn(): Boolean {

            val consideredEnemies = enemies()

            if (consideredEnemies.isEmpty()) {
                return false
            }

            if (adjacentEnemies().isEmpty() && consideredEnemies.isNotEmpty()) {
                move(consideredEnemies)
            }

            if (adjacentEnemies().isNotEmpty()) {
                attack(adjacentEnemies()[0])
            }

            return true
        }

        private fun move(consideredEnemies: List<Cell>) {

            val path = adjacentCells(position)
                .map { it.moveTowards(consideredEnemies) }
                .flatten()
                .sortedWith(PATH_COMPARATOR)
                .firstOrNull()

            if (path != null) {
                val target = path[0]
                set(position, EmptyCell(position))
                position = target.position
                set(position, this)
            }
        }

        override fun moveTowards(possibleEnemies: List<Cell>): List<List<Cell>> {
            return if (possibleEnemies.contains(this)) {
                listOf(listOf(this))
            } else {
                emptyList()
            }
        }

        private fun adjacentEnemies() = adjacentCells(position).filter(this::isEnemy).map { it as Unit }

        private fun attack(enemy: Unit) {
            enemy.hp -= attackScore
            if (!enemy.isAlive) {
                set(enemy.position, EmptyCell(enemy.position))
            }
        }
    }

}

//Identify all possible targets (all enemy units)
// -> No enemies = combat ends
//Identify open squares adjacent to each target
// -> if not in range + no open squares = turn ends

//If a target is in range = attack
//Else = move

//To Move:
//Find reachable enemy-adjacent squares
//Order by distance from current location
//Move towards enemy
//(On tie - use reading order)

//To Attack:
//Select adjacent unit with fewest HP
//Deal damage = attack power
//HP <= 0: Die


