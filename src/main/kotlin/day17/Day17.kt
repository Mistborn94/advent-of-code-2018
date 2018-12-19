package day17

import kotlin.math.max

enum class Cell(val symbol: Char, val isBlocking: Boolean, val isWater: Boolean) {
    CLAY('#', true, false),
    SAND('.', false, false),
    FLOWING_WATER('|', false, true),
    SETTLED_WATER('~', true, true);

    override fun toString(): String {
        return "$symbol"
    }
}

data class ClayVein(val x: IntRange, val y: IntRange) {

    companion object {
        fun buildFromString(line: String): ClayVein {
            val (a, b, c) = pattern.matchEntire(line)!!.destructured.toList().map { it.toInt() }
            return if (line.startsWith("x")) {
                ClayVein(a..a, b..c)
            } else {
                ClayVein(b..c, a..a)
            }
        }
    }

    fun coordinates(): List<Pair<Int, Int>> = x.flatMap { x ->
        y.map { y -> Pair(x, y) }
    }
}

val pattern = """[xy]=(\d+), [xy]=(\d+)..(\d+)""".toRegex()

class Day17(lines: List<String>) {
    private val veins = buildClayVeins(lines)
    val ground = GroundSlice(veins)

    init {
        water()
    }

    private fun buildClayVeins(lines: List<String>): List<ClayVein> {
        val veins = mutableListOf<ClayVein>()

        for (line in lines) {
            val clayVein = ClayVein.buildFromString(line)
            veins.add(clayVein)
        }

        return veins
    }

    fun solvePartA(): Int {
        return ground.countWater()
    }

    fun solvePartB(): Int {
        return ground.count(Cell.SETTLED_WATER)
    }

    private fun water() {
        val startY = max(0, ground.yRange.start)
        val startX = 500

        if (!ground.xRange.contains(startX) || ground[startX, startY] == Cell.CLAY) {
            throw IllegalStateException("Spring out of range")
        }

        moveDown(startX, startY)
    }

    private fun moveDown(x: Int, y: Int): Boolean {
        if (!ground.contains(x, y)) {
            return false
        }

        if (ground[x, y].isBlocking) {
            return true
        }

        if (ground[x, y] == Cell.FLOWING_WATER) {
            return false
        }

        ground[x, y] = Cell.FLOWING_WATER
        var stopped = moveDown(x, y + 1)

        if (stopped) {
            val stoppedLeft = moveLeft(x - 1, y)
            val stoppedRight = moveRight(x + 1, y)
            stopped = stoppedLeft && stoppedRight

            if (!stoppedRight) {
                markHorizontalFlow(x - 1, y, change = -1)
            }

            if (!stoppedLeft) {
                markHorizontalFlow(x + 1, y, change = 1)
            }
        }

        if (stopped) {
            ground[x, y] = Cell.SETTLED_WATER
        }

        return stopped
    }

    private tailrec fun markHorizontalFlow(x: Int, y: Int, change: Int = 0) {
        if (ground[x, y] == Cell.SETTLED_WATER) {
            ground[x, y] = Cell.FLOWING_WATER
            markHorizontalFlow(x + change, y, change)
        }
    }

    private fun moveLeft(x: Int, y: Int): Boolean {
        if (ground[x, y] == Cell.CLAY) {
            return true
        }

        return if (ground[x, y + 1].isBlocking) {
            ground[x, y] = Cell.FLOWING_WATER
            val stopped = moveLeft(x - 1, y)
            if (stopped) {
                ground[x, y] = Cell.SETTLED_WATER
            }
            stopped
        } else {
            moveDown(x, y)
        }
    }

    private fun moveRight(x: Int, y: Int): Boolean {
        if (ground[x, y] == Cell.CLAY) {
            return true
        }

        return if (ground[x, y + 1].isBlocking) {
            ground[x, y] = Cell.FLOWING_WATER
            val stopped = moveRight(x + 1, y)
            if (stopped) {
                ground[x, y] = Cell.SETTLED_WATER
            }
            stopped
        } else {
            moveDown(x, y)
        }
    }
}

class GroundSlice(veins: List<ClayVein>) {

    private val groundSlice: Array<Array<Cell>>

    val yRange = veins.map { it.y.start }.min()!!..veins.map { it.y.endInclusive }.max()!!
    val xRange = veins.map { it.x.start }.min()!! - 1..veins.map { it.x.endInclusive }.max()!! + 1

    init {
        val height = yRange.count() + 1
        val width = xRange.count() + 1

        groundSlice = Array(width) { Array(height) { Cell.SAND } }

        veins.flatMap { it.coordinates() }
            .forEach { (x, y) -> this[x, y] = Cell.CLAY }
    }

    operator fun get(x: Int, y: Int): Cell = groundSlice[x - xRange.start][y - yRange.start]

    operator fun set(x: Int, y: Int, value: Cell) {
        groundSlice[x - xRange.start][y - yRange.start] = value
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (y in yRange) {
            for (x in xRange) {
                builder.append(this[x, y])
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    fun contains(x: Int, y: Int): Boolean {
        return xRange.contains(x) && yRange.contains(y)
    }

    fun countWater(): Int {
        return groundSlice
            .sumBy { it.count { cell -> cell.isWater } }
    }

    fun count(cellType: Cell): Int {
        return groundSlice
            .sumBy { it.count { cell -> cell == cellType } }
    }
}