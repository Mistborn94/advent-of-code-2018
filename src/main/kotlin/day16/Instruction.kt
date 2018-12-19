package day16

data class Instruction(val operation: TimeTravelOpCode, val a: Int, val b: Int, val c: Int) {

    fun execute(registers: List<Int>): List<Int> {
        return operation.apply(registers, a, b, c)
    }

    fun executeMutable(registers: MutableList<Int>) {
        operation.applyMutable(registers, a, b, c)
    }

    companion object {
        fun parseString(instruction: String): Instruction {
            val components = instruction.split(" ")

            val a = components[1].toInt()
            val b = components[2].toInt()
            val c = components[3].toInt()
            val operation = TimeTravelOpCode.fromOperationString(components[0])
            return Instruction(operation, a, b, c)
        }
    }
}