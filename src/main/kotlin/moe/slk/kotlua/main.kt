package moe.slk.kotlua

import moe.slk.kotlua.debug.printBinChunk
import moe.slk.kotlua.debug.testLuaState

fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        printBinChunk(args[0])
    }
    testLuaState()
}
