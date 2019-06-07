package moe.slk.kotlua

import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.state.LuaStateImpl
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
//        printBinChunk(args[0])
        val data = Files.readAllBytes(Paths.get(args[0]))
        val ls = LuaStateImpl()
        ls.register("print", ::printLs)
        ls.load(data, args[0], "b")
        ls.call(0, 0)
    }
}

fun printLs(ls: LuaState): Int {
    val nArgs = ls.top
    for (i in 1..nArgs) {
        when {
            ls.isBoolean(i) -> print(ls.toBoolean(i))
            ls.isString(i) -> print(ls.toString(i))
            else -> print(ls.typeName(ls.type(i)))
        }
        if (i < nArgs) {
            print("\t")
        }
        println()
    }
    return 0
}