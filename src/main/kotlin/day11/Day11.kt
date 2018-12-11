package day11

import kotlin.math.max

class PowerGrid(val serialNumber: Int, val gridSize: Int = 300) {

    private val powerGrid = List(gridSize * gridSize) {
        val (x, y) = getCoordinates(it)
        FuelCell.build(x, y, serialNumber)
    }

    private fun getCoordinates(index: Int, size: Int = gridSize) = Pair((index % size) + 1, (index / size) + 1)
    private fun getIndex(x: Int, y: Int, size: Int = gridSize) = size * (y - 1) + (x - 1)
    private fun fuelCellAt(x: Int, y: Int) = powerGrid[getIndex(x, y)]

    fun findBestSubsquare(size: Int = 3): SubSquare {
        val valueGridSize = gridSize - size + 1
        var bestSquare = SubSquare.MIN

        for (i in 0 until (valueGridSize * valueGridSize)) {
            val (x, y) = getCoordinates(i, valueGridSize)
            val cellSquare = getTotalPowerInSubSquare(size, x, y)
            bestSquare = bestSubSquare(bestSquare, x, y, size, cellSquare)
        }

        return bestSquare
    }

    private fun bestSubSquare(bestSquare: SubSquare, x: Int, y: Int, size: Int, summedPower: Int): SubSquare {
        val newSquare = SubSquare(x = x, y = y, value = summedPower, size = size)
        return maxOf(newSquare, bestSquare)
    }

    private fun getTotalPowerInSubSquare(size: Int, x: Int, y: Int): Int {
        var totalPower: Int = 0
        for (xa in 0 until size) {
            for (ya in 0 until size) {
                totalPower += fuelCellAt(x + xa, y + ya).powerLevel
            }
        }
        return totalPower
    }

    fun findBestSubsquareOfAnySize(): SubSquare {
        var bestSquare = SubSquare.MIN
        for (fuelCell in powerGrid) {
            val x = fuelCell.x
            val y = fuelCell.y
            val maxSize = gridSize - max(x, y) + 1

            var totalPower: Int = 0
            for (size in 1 .. maxSize) {
                //corner cell
                totalPower += fuelCellAt(x + size - 1, y + size - 1).powerLevel

                //new edges
                (1 until size).forEach {
                    totalPower += fuelCellAt(x + size - 1, y + it - 1).powerLevel
                    totalPower += fuelCellAt(x + it - 1, y + size - 1).powerLevel
                }

                bestSquare = bestSubSquare(bestSquare, x, y, size, totalPower)
            }
        }

        return bestSquare
    }

}

data class FuelCell constructor(val x: Int, val y: Int, val powerLevel: Int) {

    companion object {

        private fun calculatePowerLevel(x: Int, y: Int, serialNumber: Int): Int {
            val rackId = x + 10

            val basePower = (rackId * y + serialNumber) * rackId
            val hundredsDigit = basePower / 100 - basePower / 1000 * 10

            return hundredsDigit - 5
        }

        fun build(x: Int, y: Int, serialNumber: Int): FuelCell = FuelCell(x, y, calculatePowerLevel(x, y, serialNumber))
    }
}

data class SubSquare(val x: Int, val y: Int, val value: Int, val size: Int) : Comparable<SubSquare> {
    override fun compareTo(other: SubSquare): Int = value.compareTo(other.value)

    companion object {
        val MIN: SubSquare = SubSquare(-1, -1, value = Int.MIN_VALUE, size = -1)
    }
}