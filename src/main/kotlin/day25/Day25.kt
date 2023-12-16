package day25

import helper.HyperspacePoint
import helper.removeFirst


fun solve(text: String): Int {
    val coordinates = text.trim().lines().map { line ->
        HyperspacePoint(line.split(",").map { it.toInt() })
    }

    val distances = coordinates.associateWith { a ->
        coordinates.filter { it != a }
            .associateWith { b -> a.manhattanDistance(b) }
    }

    val globalToVisit = coordinates.toMutableSet()
    var count = 0

    val visited = mutableSetOf<HyperspacePoint>()
    while (globalToVisit.isNotEmpty()) {
        val currentToVisit = mutableSetOf(globalToVisit.removeFirst())

        while (currentToVisit.isNotEmpty()) {
            val current = currentToVisit.removeFirst()
            val next = distances[current]!!.filter { (key, value) -> key !in visited && value <= 3 }.keys
            globalToVisit.removeAll(next)
            currentToVisit.addAll(next)
            visited.add(current)
        }
        count++
    }

    return count
}