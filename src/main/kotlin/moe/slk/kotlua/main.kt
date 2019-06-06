package moe.slk.kotlua

import moe.slk.kotlua.debug.printBinChunk
import moe.slk.kotlua.debug.testBasicInstructions


fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        printBinChunk(args[0])
        testBasicInstructions(args[0])
    }
}
