package moe.slk.kotlua

import moe.slk.kotlua.debug.printBinChunk
import moe.slk.kotlua.debug.testLuaState
import moe.slk.kotlua.debug.testOp

fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        printBinChunk(args[0])
    }
    testLuaState()
    testOp()
}
