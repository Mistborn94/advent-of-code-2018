package day16

typealias Registers = List<Int>

enum class TimeTravelOpCode(val description: String) {
    ADDR("Add Register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] + registers[b]
    },
    ADDI("Add Immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] + b
    },
    MULR("Multiply Register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] * registers[b]
    },
    MULI("Multiply Immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] * b
    },
    BANR("bitwise AND register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] and registers[b]
    },
    BANI("bitwise AND immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] and b
    },
    BORR("bitwise AND register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] or registers[b]
    },
    BORI("bitwise AND immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a] or b
    },
    SETR("set register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = registers[a]
    },
    SETI("set immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = a
    },
    GTIR("greater-than immediate/register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (a > registers[b]) 1 else 0
    },
    GTRI("greater-than register/immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (registers[a] > b) 1 else 0
    },
    GTRR("greater-than register/register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (registers[a] > registers[b]) 1 else 0
    },
    ETIR("equal immediate/register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (a == registers[b]) 1 else 0
    },
    ETRI("equal register/immediate") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (registers[a] == b) 1 else 0
    },
    ETRR("equal register/register") {
        override fun getResult(registers: Registers, a: Int, b: Int): Int = if (registers[a] == registers[b]) 1 else 0
    };

    protected abstract fun getResult(registers: Registers, a: Int, b: Int): Int

    fun apply(registers: Registers, a: Int, b: Int, c: Int): Registers {
        val newRegisters = registers.toMutableList()
        newRegisters[c] = getResult(registers, a, b)
        return newRegisters
    }
}

fun findMatchingOpCodes(before: Registers, after: Registers, instruction: List<Int>): Set<TimeTravelOpCode> {
    val a = instruction[1]
    val b = instruction[2]
    val c = instruction[3]

    return TimeTravelOpCode.values()
        .filter {
            it.apply(before, a, b, c) == after
        }.toSet()
}

fun solvePartA(lines: List<String>): Int {
    val possibleOpCodes = parseInputSamples(lines)

    return possibleOpCodes.count { it.possibleCodes.size >= 3 }
}

private fun parseInstructions(line: String) = line.split(" ").map { it.trim().toInt() }
private fun parseRegisterState(s: String) = s.substring(9, 19).split(", ").map { it.trim().toInt() }

fun solvePartB(lines: List<String>): Int {

    val instructionSamples = parseInputSamples(lines)
    val instructionCodes = findInstructionNumbers(instructionSamples)
    val programSubList = lines.subList(instructionSamples.size * 4 + 2, lines.size)
    val program = Program(instructionCodes, parseProgram(programSubList))

    return program.execute()[0]
}

fun parseProgram(subList: List<String>): List<List<Int>> {
    return subList.map { line ->
        line.split(" ").map { it.toInt() }
    }
}

private fun findInstructionNumbers(possibleOpCodes: List<InstructionSample>): Map<Int, TimeTravelOpCode> {
    var remainingSamples = possibleOpCodes.toSet()
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

            instructionCodes[solvedCode.instructionNumber] = solvedCode.possibleCodes.first()
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
    possibleOpCodes.filter { it.possibleCodes.size == 1 }

private fun parseInputSamples(lines: List<String>): List<InstructionSample> {
    val possibleOpCodes = mutableListOf<InstructionSample>()
    var i = 0
    while (lines[i].startsWith("Before")) {
        val before = parseRegisterState(lines[i])
        val instructions = parseInstructions(lines[i + 1])
        val after = parseRegisterState(lines[i + 2])

        val validCodes = findMatchingOpCodes(before, after, instructions)
        possibleOpCodes.add(InstructionSample(instructions[0], validCodes))

        i += 4
    }
    return possibleOpCodes
}

data class InstructionSample(val instructionNumber: Int, val possibleCodes: Collection<TimeTravelOpCode>) {

    fun withRemoved(solvedCodes: Collection<TimeTravelOpCode>): InstructionSample {
        return InstructionSample(instructionNumber, possibleCodes.subtract(solvedCodes))
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
            Instruction(a, b, c, operation!!)
        }
    }

    fun execute(initialState: Registers = listOf(0, 0, 0, 0)): Registers {

        var currentRegister = initialState
        for (instruction in instructions) {
            currentRegister = instruction.execute(currentRegister)
        }

        return currentRegister
    }
}

data class Instruction(val a: Int, val b: Int, val c: Int, val operation: TimeTravelOpCode) {

    fun execute(registers: Registers): Registers {
        return operation.apply(registers, a, b, c)
    }
}



