package day12

const val PADDING_SIZE = 4

fun calculatePlants(lines: List<String>, generations: Long): Long {
    val paddingString = ".".repeat(PADDING_SIZE)
    val initialState = paddingString + lines[0].substring(15) + paddingString
    var currentPattern = initialState

    var leftmostPlantIndex: Long = -PADDING_SIZE.toLong()

    val instructions = mutableMapOf<String, Char>()
    for (s in lines.subList(2, lines.size)) {
        val (pattern, result) = s.split(" => ")
        instructions[pattern] = result[0]
    }

    //This might not be sufficient. What if the same but just shifted?? Is that possible
    val seenStates = mutableListOf<SeenState>()
    var iteration = 0L

    var currentState = SeenState.buildWithLeftmostIndex(leftmostPlantIndex, currentPattern, iteration)

    while (iteration < generations && !seenStates.any(currentState::patternMatch)) {
        seenStates.add(currentState)
        iteration++

        val newState = StringBuilder("..")

        for (plantId in 2..currentPattern.lastIndex - 2) {
            val substring = currentPattern.substring(plantId - 2, plantId + 3)
            val newChar = instructions.getOrDefault(substring, '.')
            newState.append(newChar)
        }
        newState.append("..")

        val firstLivingIndex = newState.indexOf('#')
        if (firstLivingIndex < PADDING_SIZE) {
            val addCount = PADDING_SIZE - firstLivingIndex
            newState.insert(0, ".".repeat(addCount))
            leftmostPlantIndex -= addCount
        }

        val lastLivingIndex = currentPattern.lastIndex - newState.lastIndexOf('#')
        if (lastLivingIndex < PADDING_SIZE) {
            val addCount = PADDING_SIZE - lastLivingIndex
            newState.append(".".repeat(addCount))
        }

        currentPattern = newState.toString()
        currentState = SeenState.buildWithLeftmostIndex(leftmostPlantIndex, currentPattern, iteration)
    }

    //println("$iteration: $trimmedState")
    if (iteration < generations) {

        val startIndex = seenStates.indexOfFirst(currentState::patternMatch)

        val loopSize = iteration - startIndex
        val startState = seenStates[startIndex]

        val loopPosition = (generations - iteration) % loopSize

        val matchingIndex = loopPosition + startIndex
        val matchingState = seenStates[matchingIndex.toInt()]
        println("Match  : $matchingState")
        println("Current: $currentState")

        if (currentState.firstIndex == startState.firstIndex) {
            return currentState.getValue()
        } else {
            val changePerGeneration = currentState.firstIndex - matchingState.firstIndex
            val generationCount = generations - matchingState.iteration
            val totalChange = generationCount * changePerGeneration

            currentState = SeenState.buildWithFirstPlantIndex(totalChange + matchingState.firstIndex, currentPattern, generations)
            println("New    : $currentState")

            println("Loop size: $loopSize")
            println("Change: $changePerGeneration x $generationCount generations => $totalChange")
        }
    }

    return currentState.getValue()
}


data class SeenState private constructor(val iteration: Long, val firstIndex: Long, val pattern: String) {
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