package day12

const val PADDING_SIZE = 4

fun calculatePlants(lines: List<String>, generations: Long): Long {
    var (currentPattern, leftmostPotIndex) = getInitialState(lines)

    val instructions = parseInstructions(lines.subList(2, lines.size))

    val seenStates = mutableListOf<SeenState>()
    var iteration = 0L

    var currentState = SeenState.buildWithLeftmostIndex(leftmostPotIndex, currentPattern, iteration)

    while (iteration < generations && !seenStates.any(currentState::patternMatch)) {
        seenStates.add(currentState)
        iteration++

        val newState = processInstructions(currentPattern, instructions)

        leftmostPotIndex = pad(newState, leftmostPotIndex)

        currentPattern = newState.toString()
        currentState = SeenState.buildWithLeftmostIndex(leftmostPotIndex, currentPattern, iteration)
    }

    if (iteration < generations) {
        val startIndex = seenStates.indexOfFirst(currentState::patternMatch)

        val loopSize = iteration - startIndex

        val loopPosition = (generations - iteration) % loopSize

        val matchingIndex = loopPosition + startIndex
        val matchingState = seenStates[matchingIndex.toInt()]

        val changePerGeneration = currentState.firstIndex - matchingState.firstIndex
        val totalChange = changePerGeneration * (generations - matchingState.iteration)

        currentState =
                SeenState.buildWithFirstPlantIndex(totalChange + matchingState.firstIndex, currentPattern, generations)

    }

    return currentState.getValue()
}

private fun getInitialState(lines: List<String>): Pair<String, Long> {
    val initialStateBuilder = StringBuilder(lines[0].substring(15))
    val leftmostPotIndex = pad(initialStateBuilder, 0)

    return Pair(initialStateBuilder.toString(), leftmostPotIndex)
}

private fun parseInstructions(list: List<String>): MutableMap<String, Char> {
    val instructions = mutableMapOf<String, Char>()
    for (s in list) {
        val (pattern, result) = s.split(" => ")
        instructions[pattern] = result[0]
    }
    return instructions
}

private fun processInstructions(
    currentPattern: String,
    instructions: MutableMap<String, Char>
): StringBuilder {
    val newState = StringBuilder("..")

    for (plantId in 2..currentPattern.lastIndex - 2) {
        val substring = currentPattern.substring(plantId - 2, plantId + 3)
        val newChar = instructions.getOrDefault(substring, '.')
        newState.append(newChar)
    }
    newState.append("..")
    return newState
}

private fun pad(newState: StringBuilder, leftmostPotIndex: Long): Long {
    val lastLivingIndex = newState.lastIndex - newState.lastIndexOf('#')
    if (lastLivingIndex < PADDING_SIZE) {
        val addCount = PADDING_SIZE - lastLivingIndex
        newState.append(".".repeat(addCount))
    }

    val firstLivingIndex = newState.indexOf('#')
    if (firstLivingIndex < PADDING_SIZE) {
        val addCount = PADDING_SIZE - firstLivingIndex
        newState.insert(0, ".".repeat(addCount))
        return leftmostPotIndex - addCount
    }

    return leftmostPotIndex
}


data class SeenState constructor(val iteration: Long, val firstIndex: Long, val pattern: String) {

    companion object {
        fun buildWithLeftmostIndex(leftmostIndex: Long, untrimmedPattern: String, iteration: Long): SeenState {
            val firstPlantIndex = findPlantIndex(untrimmedPattern.indexOf('#'), leftmostIndex)

            return SeenState(iteration, firstPlantIndex, untrimmedPattern.trim('.'))
        }

        fun buildWithFirstPlantIndex(firstPlantIndex: Long, untrimmedPattern: String, iteration: Long): SeenState {
            return SeenState(iteration, firstPlantIndex, untrimmedPattern.trim('.'))
        }

        fun findPlantIndex(listIndex: Int, firstPlantIndex: Long) = listIndex + firstPlantIndex
    }

    fun patternMatch(other: SeenState): Boolean {
        return pattern == other.pattern
    }

    fun getValue(): Long {
        var total = 0L
        for (i in pattern.indices) {
            if (pattern[i] == '#') {
                total += findPlantIndex(i, firstIndex)
            }
        }

        return total
    }
}