package day23

import kotlin.math.abs

data class Nanobot(val x: Int, val y: Int, val z: Int, var r: Int) {

    fun distance(other: Nanobot): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    fun distance(oX: Int, oY: Int, oZ: Int): Int = abs(x - oX) + abs(y - oY) + abs(z - oZ)

    fun inRange(other: Nanobot): Boolean = distance(other) <= r

    fun intersects(other: Nanobot): Boolean = distance(other) < this.r + other.r
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

    fun solvePartB(): Int {
        val overlap = buildOverlapMap()

        //Not correct. What if I handle it like a graph search maybe?
        val overlappingBots = overlap.values.maxBy { it.size }!!

        val startingDistance = overlappingBots.map { it.distance(0, 0, 0) }.min()
        val endingDistance = overlappingBots.map { it.distance(0, 0, 0) }.max()

        val testBot = Nanobot(0, 0, 0, startingDistance!!)

        while (!overlappingBots.all { testBot.inRange(it) }) {
            testBot.r += 1

            if (testBot.r > endingDistance!!) {
                throw IllegalStateException("No valid range found between $startingDistance and $endingDistance")
            }
        }

        return testBot.r
    }

    private fun buildOverlapMap(): Map<Nanobot, Set<Nanobot>> {
        val overlap = mutableMapOf<Nanobot, MutableSet<Nanobot>>()

        for ((index, first) in swarm.subList(0, swarm.size - 1).withIndex()) {
            for (second in swarm.subList(index + 1, swarm.size)) {
                if (first.intersects(second)) {
                    overlap.getOrPut(first) { mutableSetOf(first) }.add(second)
                    overlap.getOrPut(second) { mutableSetOf(second) }.add(first)
                }
            }
        }
        return overlap
    }
}

