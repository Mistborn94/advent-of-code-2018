package day20

import helper.Point
import java.util.*

class BuildingMap(path: String) {
    private val initialRoom = Room(0, 0, this)
    private val allRooms = mutableListOf(initialRoom)

    init {
        initialRoom.build(path, 1)
    }

    fun addRoom(room: Room) {
        allRooms.add(room)
    }

    fun roomAt(position: Point) = allRooms.firstOrNull { it.position == position }

    override fun toString(): String {
//        val rows = allRooms.sortedWith(comparing<Room, Int>{it.position.y}).groupBy { it.position.x }

        val minX = allRooms.map { it.position.x }.min()!!
        val maxX = allRooms.map { it.position.x }.max()!!
        val width = maxX - minX + 2

        val minY = allRooms.map { it.position.y }.min()!!
        val maxY = allRooms.map { it.position.y }.max()!!
        val height = maxY - minY + 2

        val map = Array(height * 2 + 1) { Array(width * 2 + 1) { ' ' } }

        for (room in allRooms) {
            val adjustedX = (room.position.x - minX) * 2 + 1
            val adjustedY = (room.position.y - minY) * 2 + 1

            val sides = listOf(
                Point(adjustedX - 1, adjustedY - 1),
                Point(adjustedX - 1, adjustedY),
                Point(adjustedX - 1, adjustedY + 1),

                Point(adjustedX + 1, adjustedY - 1),
                Point(adjustedX + 1, adjustedY),
                Point(adjustedX + 1, adjustedY + 1),

                Point(adjustedX, adjustedY - 1),
                Point(adjustedX, adjustedY + 1)
            )

            sides.forEach { (x, y) -> map[y][x] = '#' }

            room.reachable.forEach{(direction, _) ->
                val location = Point(adjustedX, adjustedY).plus(direction.direction)

                map[location.y][location.x] = if (direction == Direction.WEST || direction == Direction.EAST) '|' else '-'
            }
        }

        map[-minY * 2 + 1][-minX * 2 + 1] = 'X'

        return map.joinToString("\n") { it.joinToString("") }
    }

    fun bfs(): Pair<Int, Int> {
        var longestDistance = 0
        var atLeastThousand = 0
        val visited = mutableSetOf<Room>()
        val toVisit = mutableListOf(Pair(initialRoom, 0))

        while (!toVisit.isEmpty()) {
            val (room, distance) = toVisit.removeAt(0)
            visited.add(room)

            if (distance > longestDistance) {
                longestDistance = distance
            }

            if (distance >= 1_000) {
                atLeastThousand += 1
            }

            room.reachableRooms.forEach { nextRoom ->
                if (!visited.contains(nextRoom) && toVisit.none { it.first == nextRoom }) {
                    toVisit.add(Pair(nextRoom, distance + 1))
                }
            }
        }

        return Pair(longestDistance, atLeastThousand)
    }
}

enum class Direction(val direction: Point) {
    NORTH(Point(0, -1)) {
        override val opposite
            get() = SOUTH
    },
    EAST(Point(+1, 0)) {
        override val opposite
            get() = WEST
    },
    SOUTH(Point(0, 1)) {
        override val opposite
            get() = NORTH
    },
    WEST(Point(-1, 0)) {
        override val opposite
            get() = EAST
    };

    fun get(room: Room): RoomRef? = room.neighbours[this]

    abstract val opposite: Direction

}

data class RoomRef(
    val room: Room,
    val door: Boolean
)

class Room(val position: Point, val map: BuildingMap) {

    constructor(x: Int, y: Int, map: BuildingMap) : this(Point(x, y), map)

    val neighbours = EnumMap<Direction, RoomRef>(Direction::class.java)
    val reachable : Map<Direction, RoomRef>
        get() = neighbours.filter { (_, ref) -> ref.door }
    val reachableRooms: List<Room>
        get() = neighbours.values.filter { it.door }.map { it.room }

    private fun addDoor(direction: Direction): Room {
        return if (neighbours.containsKey(direction)) {
            val targetRoom = neighbours[direction]!!.room
            link(this, targetRoom, direction, true)
            targetRoom
        } else {
            val newRoom = Room(position + direction.direction, map)

            Direction.values().forEach {
                link(newRoom, it, it == direction.opposite)
            }

            map.addRoom(newRoom)
            newRoom
        }
    }

    private fun link(from: Room, direction: Direction, door: Boolean) {
        val targetPosition = from.position + direction.direction
        val targetRoom = map.roomAt(targetPosition)

        if (targetRoom != null) {
            link(from, targetRoom, direction, door)
        }
    }


    private fun link(from: Room, to: Room, direction: Direction, door: Boolean) {
        from.neighbours[direction] = RoomRef(to, door)
        to.neighbours[direction.opposite] = RoomRef(from, door)
    }

    fun build(path: String, index: Int): Pair<Int, Set<Room>> {
        var currentIndex = index
        var currentPaths = setOf(this)

        val instruction = { path[currentIndex] }

        while (instruction() !in listOf('$', '|', ')')) {
            when (instruction()) {
                'N' -> {
                    currentPaths = currentPaths.mapTo(mutableSetOf()) { it.addDoor(Direction.NORTH) }
                    currentIndex += 1
                }
                'S' -> {
                    currentPaths = currentPaths.mapTo(mutableSetOf()) {it.addDoor(Direction.SOUTH) }
                    currentIndex += 1
                }
                'E' -> {
                    currentPaths = currentPaths.mapTo(mutableSetOf()) {it.addDoor(Direction.EAST) }
                    currentIndex += 1
                }
                'W' -> {
                    currentPaths = currentPaths.mapTo(mutableSetOf()){ it.addDoor(Direction.WEST) }
                    currentIndex += 1
                }
                '(' -> {
                    var newCurrent = emptySet<Room>()
                    do {
                        val newPaths = currentPaths.map { it.build(path, currentIndex + 1) }

                        newCurrent += newPaths.flatMap { it.second }
                        currentIndex = newPaths.last().first
                    } while (instruction() != ')')
                    currentPaths = newCurrent
                    currentIndex += 1
                }
                else -> throw IllegalArgumentException("Unknown instruction $instruction")
            }
        }

        return Pair(currentIndex, currentPaths)
    }

    override fun toString(): String {
        return "Room(${position.x}, ${position.y})"
    }


}