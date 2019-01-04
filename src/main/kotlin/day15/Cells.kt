package day15

import helper.Point

abstract class Cell(var position: Point, val type: Char) {
    override fun toString(): String = type.toString()
}

class WallCell(point: Point) : Cell(point, '#')
class EmptyCell(position: Point) : Cell(position, '.')

// Turn Order:
// -----------
// Identify all possible targets (all enemy units)
// -> No enemies = combat ends
// Identify open squares adjacent to each target
// -> if not in range + no open squares = turn ends

// If a target is in range = attack
// Else = move

// To Move:
// Find reachable enemy-adjacent squares
// Order by distance from current location
// Move towards enemy
// (On tie - use reading order)

// To Attack:
// Select adjacent unit with fewest HP
// Deal damage = attack power
// HP <= 0: Die
class UnitCell(position: Point, type: Char, private val attackScore: Int = 3, private val combat: Combat) :
    Cell(position, type) {
    var hp =  200
    val isAlive get() = hp > 0

    override fun toString(): String = "$type"

    private fun enemies(): List<UnitCell> = combat.board.filter(this::isEnemy).map { it as UnitCell }

    fun isEnemy(it: Cell) = it is UnitCell && it.type != type

    fun takeTurn(): Boolean {
        val allEnemies = enemies()

        if (allEnemies.isEmpty()) {
            return false
        }

        val consideredEnemies = allEnemies.filter { combat.adjacentCells(it.position)
            .any { cell -> cell is EmptyCell } }

        if (adjacentEnemies().isEmpty() && consideredEnemies.isNotEmpty()) {
            move()
        }

        if (adjacentEnemies().isNotEmpty()) {
            attack(adjacentEnemies()[0])
        }

        return true
    }

    private fun move() {
        val path = combat.bfs(this)

        if (path != null) {
            moveTo(path.first)
        }
    }

    private fun moveTo(target: Cell) {
        combat.set(position, EmptyCell(position))
        position = target.position
        combat.set(position, this)
    }

    private fun adjacentEnemies() = combat.adjacentCells(position)
        .filter(this::isEnemy)
        .map { it as UnitCell }
        .sortedWith(ENEMY_COMPARATOR)

    private fun attack(enemy: UnitCell) {
        enemy.hp -= attackScore
        if (!enemy.isAlive) {
            combat.set(enemy.position, EmptyCell(enemy.position))
        }
    }
}