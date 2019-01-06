package day23

import helper.Grid
import kotlin.math.abs

data class Nanobot(val x: Int, val y: Int, val z: Int, val r: Int) {

    fun distance(other: Nanobot): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    fun distance(oX: Int, oY: Int, oZ: Int): Int = abs(x - oX) + abs(y - oY) + abs(z - oZ)

    fun inRange(other: Nanobot): Boolean = distance(other) <= r
    fun inRange(x: Int, y: Int, z: Int): Boolean = distance(x,y,z) <= r
}

class Day23(input: List<String>) {

    val regex = "pos=<([-0-9]+),([-0-9]+),([-0-9]+)>, r=([-0-9]+)".toRegex()
    val swarm: List<Nanobot>

    init {
        swarm = input.map(regex::matchEntire)
            .map { it!!.destructured }
            .map { (x, y, z, r) -> Nanobot(x.toInt(), y.toInt(), z.toInt(), r.toInt()) }
    }

    fun solvePartA(): Int {
        val powerfulBot = swarm.maxBy { it.r }!!
        return swarm.count { powerfulBot.inRange(it) }
    }

    private fun initGirdArea(nanobot: Nanobot, grid: Grid<Int>) {
        for (xo in -nanobot.r..nanobot.r) {
            val yRange = nanobot.r - abs(xo)
            for (yo in -yRange..yRange) {
                val zRange = nanobot.r - abs(xo) - abs(yo)
                for (zo in -zRange..nanobot.r) {
                    val x = nanobot.x + xo
                    val y = nanobot.y + yo
                    val z = nanobot.z + zo
                    if (grid.inRange(x, y, z)) {
                        grid[x, y, z] += 1
                    }
                }
            }
        }
    }

    fun solvePartB(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

