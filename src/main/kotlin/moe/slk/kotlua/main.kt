package moe.slk.kotlua

import moe.slk.kotlua.debug.printBinChunk
import moe.slk.kotlua.state.LuaStateImpl
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        printBinChunk(args[0])
        val data = Files.readAllBytes(Paths.get(args[0]))
        val ls = LuaStateImpl()
        ls.load(data, args[0], "b")
        ls.call(0, 0)
    }
}
