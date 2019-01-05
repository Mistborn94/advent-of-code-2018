package day22

import helper.Point
import java.util.Comparator.comparing

enum class Tool {
    CLIMBING,
    TORCH,
    NEITHER
}

enum class RegionType(val riskLevel: Int, val tools: List<Tool>) {
    ROCKY(0, listOf(Tool.TORCH, Tool.CLIMBING)),
    WET(1, listOf(Tool.CLIMBING, Tool.NEITHER)),
    NARROW(2, listOf(Tool.NEITHER, Tool.TORCH));

    fun overlapTool(regionType: RegionType): Set<Tool> {
        return tools.intersect(regionType.tools)
    }
}

class CaveSystem(val target: Point, val depth: Int) {

    private val geologicalIndexCache = mutableMapOf<Point, Int>()
    private val erosionLevelCache = mutableMapOf<Point, Int>()

    fun geologicalIndex(point: Point): Int {
        val result = when {
            geologicalIndexCache.containsKey(point) -> geologicalIndexCache[point]!!
            point == Point(0, 0) -> 0
            point == target -> 0
            point.y == 0 -> point.x * 16807
            point.x == 0 -> point.y * 48271
            else -> erosionLevel(point + Point(-1, 0)) * erosionLevel(point + Point(0, -1))
        }
        geologicalIndexCache[point] = result
        return result
    }

    fun erosionLevel(point: Point): Int {
        return if (erosionLevelCache.contains(point)) {
            erosionLevelCache[point]!!
        } else {
            val result = (geologicalIndex(point) + depth) % 20183
            erosionLevelCache[point] = result
            result
        }
    }

    fun riskLevel(point: Point): Int = erosionLevel(point) % 3
    fun regionType(point: Point): RegionType = RegionType.values()[riskLevel(point)]

    fun totalRiskLevel(): Int {
        var total = 0

        for (x in 0..target.x) {
            for (y in 0..target.y) {
                total += riskLevel(Point(x, y))
            }
        }

        return total
    }

    fun neighbours(point: Point): Set<Point> {
        val neighbourSet = mutableSetOf(
            point + Point(0, 1),
            point + Point(1, 0)
        )

        if (point.x > 0) {
            neighbourSet.add(point + Point(-1, 0))
        }

        if (point.y > 0) {
            neighbourSet.add(point + Point(0, -1))
        }

        return neighbourSet
    }

    fun rescueTime(): Int {
        val toVisit = sortedSetOf(ToVisit(Point(0, 0), Tool.TORCH, 0))
        val visited = mutableSetOf<Pair<Point, Tool>>()
        val paths = mutableMapOf<Pair<Point, Tool>, Triple<Point, Tool, Int>>()

        while (!toVisit.isEmpty() && toVisit.first().position != target) {
            val current = toVisit.first()

            val currentType = regionType(current.position)

            toVisit.remove(current)
            val currentPathSegment = current.asPathSegment()
            visited.add(currentPathSegment)

            val possibleNeighbours = neighbours(current.position)
            for (neighbour in possibleNeighbours) {
                val type = regionType(neighbour)

                val newTool = when {
                    neighbour == target -> Tool.TORCH
                    type == currentType -> current.tool
                    else -> type.overlapTool(currentType).first()
                }

                if (!visited.contains(Pair(neighbour, newTool))) {

                    val newPath = if (newTool == current.tool) {
                        ToVisit(neighbour, newTool, current.time + 1)
                    } else {
                        ToVisit(neighbour, newTool, current.time + 8)
                    }

                    toVisit.add(newPath)

                    val newPathSegment = newPath.asPathSegment()
                    val oldPath = paths[newPathSegment]
                    paths[newPathSegment] = if (oldPath == null || oldPath.third > current.time) current.asCostedPathSegment() else oldPath
                }
            }
        }

//        println(rebuildPath(paths).joinToString("\n"))

        if (toVisit.isEmpty()) {
            throw IllegalStateException("Cannot reach target")
        }
        return toVisit.first().time
    }

    private fun rebuildPath(paths: MutableMap<Pair<Point, Tool>, Triple<Point, Tool, Int>>): List<String> {
        var current = paths.keys.first { it.first == target }
        var list = mutableListOf<String>()
        list.add("${current.first} with ${current.second}")

        while (current.first != Point(0, 0)) {
            val triple = paths[current]!!
            current = Pair(triple.first, triple.second)
            list.add("${triple.first} with ${triple.second}, time ${triple.third}")
        }

//        list.add("${current.first} with ${current.second}")

        return list.reversed()
    }
}

data class ToVisit(val position: Point, val tool: Tool, val time: Int) : Comparable<ToVisit> {

    fun asPathSegment() = Pair(position, tool)
    fun asCostedPathSegment() = Triple(position, tool, time)

    fun sameLocationTool(other: ToVisit): Boolean = this.position == other.position && this.tool == other.tool

    override fun compareTo(other: ToVisit): Int = comparator.compare(this, other)

    companion object {
        val comparator: Comparator<ToVisit> = comparing<ToVisit, Int>(ToVisit::time)
            .thenComparing<Tool>(ToVisit::tool)
            .thenComparing<Int> { it.position.x }
            .thenComparing<Int> { it.position.y }
    }
}