package day16

class Day16(lines: List<String>) {

    private val instructionSamples: List<InstructionSample> = parseInputSamples(lines)
    private val parsedProgram: List<List<Int>>

    init {
        val programSubList = lines.subList(instructionSamples.size * 4 + 2, lines.size)
        parsedProgram = parseProgram(programSubList)
    }

    fun solvePartA(): Int {
        return instructionSamples.count { it.possibleOperations.size >= 3 }
    }

    fun solvePartB(): Int {
        val instructionCodes = solveOperationNumbers()
        val program = Program(instructionCodes, parsedProgram)

        return program.execute()[0]
    }

    private fun parseInstruction(line: String) = line.split(" ").map { it.trim().toInt() }
    private fun parseRegisterState(s: String) = s.substring(9, 19).split(", ").map { it.trim().toInt() }
    private fun parseProgram(subList: List<String>): List<List<Int>> = subList.map { line ->
        line.split(" ").map { it.toInt() }
    }

    private fun parseInputSamples(lines: List<String>): List<InstructionSample> {
        val possibleOpCodes = mutableListOf<InstructionSample>()
        var i = 0
        while (lines[i].startsWith("Before")) {
            val before = parseRegisterState(lines[i])
            val instruction = parseInstruction(lines[i + 1])
            val after = parseRegisterState(lines[i + 2])

            possibleOpCodes.add(InstructionSample.fromInput(before, after, instruction))

            i += 4
        }
        return possibleOpCodes
    }

    private fun solveOperationNumbers(): Map<Int, TimeTravelOpCode> {
        var remainingSamples = instructionSamples.toSet()
        var solvedCodes = findSolved(remainingSamples)

        val instructionCodes = mutableMapOf<Int, TimeTravelOpCode>()
        while (instructionCodes.size < TimeTravelOpCode.values().size) {
            if (solvedCodes.isEmpty() || remainingSamples.isEmpty()) {
                throw IllegalStateException("No samples left")
            }

            for (solvedCode in solvedCodes) {
                if (instructionCodes.containsKey(solvedCode.instructionNumber)) {
                    throw IllegalStateException("Duplicate Instruction Code ${solvedCode.instructionNumber}")
                }

                instructionCodes[solvedCode.instructionNumber] = solvedCode.possibleOperations.first()
            }

            remainingSamples = remainingSamples
                .subtract(solvedCodes)
                .filter { !instructionCodes.containsKey(it.instructionNumber) }
                .map { it.withRemoved(instructionCodes.values) }
                .toSet()

            solvedCodes = findSolved(remainingSamples)
        }
        return instructionCodes
    }

    private fun findSolved(possibleOpCodes: Set<InstructionSample>) =
        possibleOpCodes.filter { it.possibleOperations.size == 1 }

}

data class InstructionSample(val instructionNumber: Int, val possibleOperations: Collection<TimeTravelOpCode>) {

    fun withRemoved(solvedCodes: Collection<TimeTravelOpCode>): InstructionSample {
        return InstructionSample(instructionNumber, possibleOperations.subtract(solvedCodes))
    }

    companion object {
        fun fromInput(before: List<Int>, after: List<Int>, instruction: List<Int>): InstructionSample {
            val a = instruction[1]
            val b = instruction[2]
            val c = instruction[3]

            val possibleOperations = TimeTravelOpCode.values()
                .filter {
                    it.apply(before, a, b, c) == after
                }.toSet()
            return InstructionSample(instruction[0], possibleOperations)
        }
    }
}

class Program(instructionCodes: Map<Int, TimeTravelOpCode>, instructions: List<List<Int>>) {
    private val instructions: List<Instruction>

    init {
        this.instructions = instructions.map {
            val a = it[1]
            val b = it[2]
            val c = it[3]
            val operation = instructionCodes[it[0]]
            Instruction(operation!!, a, b, c)
        }
    }

    fun execute(initialState: List<Int> = listOf(0, 0, 0, 0)): List<Int> {

        var currentRegister = initialState
        for (instruction in instructions) {
            currentRegister = instruction.execute(currentRegister)
        }

        return currentRegister
    }
}



