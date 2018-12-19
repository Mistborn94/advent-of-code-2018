package day12

const val PADDING_SIZE = 4

fun calculatePlants(lines: List<String>, generations: Long): Long {
    var (currentPattern, leftmostPotIndex) = getInitialState(lines)

    val instructions = parseInstructions(lines.subList(2, lines.size))

    val seenStates = mutableListOf<TrimmedState>()
    var iteration = 0

    var currentState = TrimmedState.buildWithLeftmostIndex(leftmostPotIndex, currentPattern, iteration)

    do {
        iteration++

        //Transform
        val padResult = currentState.padded()
        val newState = processInstructions(currentPattern, instructions)
        leftmostPotIndex += pad(newState)
        currentPattern = newState.toString()
        seenStates.add(currentState)
        currentState = TrimmedState.buildWithLeftmostIndex(leftmostPotIndex, currentPattern, iteration)

    } while (iteration < generations && !seenStates.any(currentState::patternMatch))

    if (iteration < generations) {
        val startIndex = seenStates.indexOfFirst(currentState::patternMatch)

        val loopSize = iteration - startIndex

        val matchingIndex = ((generations - iteration) % loopSize + startIndex).toInt()
        val matchingState = seenStates[matchingIndex]

        val changePerGeneration = currentState.firstIndex - matchingState.firstIndex
        val totalChange = changePerGeneration * (generations - matchingState.iteration)

        currentState =
                TrimmedState.buildWithFirstPlantIndex(
                    totalChange + matchingState.firstIndex,
                    currentPattern,
                    0
                )

    }

    return currentState.getValue()
}

private fun getInitialState(lines: List<String>): Pair<String, Long> {
    val initialStateBuilder = StringBuilder(lines[0].substring(15))
    val leftmostPotIndex = pad(initialStateBuilder)

    return Pair(initialStateBuilder.toString(), leftmostPotIndex.toLong())
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

private fun pad(newState: StringBuilder): Int {
    val lastLivingIndex = newState.lastIndex - newState.lastIndexOf('#')
    if (lastLivingIndex < PADDING_SIZE) {
        val addCount = PADDING_SIZE - lastLivingIndex
        newState.append(".".repeat(addCount))
    }

    val firstLivingIndex = newState.indexOf('#')
    if (firstLivingIndex < PADDING_SIZE) {
        val addCount = PADDING_SIZE - firstLivingIndex
        newState.insert(0, ".".repeat(addCount))
        return -addCount
    }

    return 0
}


data class TrimmedState(val firstIndex: Long, val pattern: String, val iteration: Int) {

    companion object {
        fun buildWithLeftmostIndex(leftmostIndex: Long, untrimmedPattern: String, iteration: Int): TrimmedState {
            val firstPlantIndex = findPlantIndex(untrimmedPattern.indexOf('#'), leftmostIndex)

            return TrimmedState(firstPlantIndex, untrimmedPattern.trim('.'), iteration)
        }

        fun buildWithFirstPlantIndex(firstPlantIndex: Long, untrimmedPattern: String, iteration: Int): TrimmedState {
            return TrimmedState(firstPlantIndex, untrimmedPattern.trim('.'), iteration)
        }

        fun findPlantIndex(listIndex: Int, firstPlantIndex: Long) = listIndex + firstPlantIndex
    }

    fun patternMatch(other: TrimmedState): Boolean {
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

    fun padded(): PaddedState = PaddedState.buildAndPad(firstIndex, pattern, iteration)
}

data class PaddedState constructor(val firstIndex: Long, val pattern: String, val iteration: Int) {

    companion object {
        fun buildAndPad(firstIndex: Long, pattern: String, iteration: Int): PaddedState {
            val newStateBuilder = StringBuilder(pattern)

            val added = pad(newStateBuilder)
            return PaddedState(added + firstIndex, newStateBuilder.toString(), iteration)
        }
    }

    fun trimmed(): TrimmedState = TrimmedState.buildWithLeftmostIndex(firstIndex, pattern, iteration)
}