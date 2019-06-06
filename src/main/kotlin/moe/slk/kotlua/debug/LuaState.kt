package moe.slk.kotlua.debug

import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.api.LuaType.*
import moe.slk.kotlua.binchunk.BinaryChunk
import moe.slk.kotlua.state.LuaStateImpl
import java.nio.file.Files
import java.nio.file.Paths

fun testLuaState(path: String) {
    val data = Files.readAllBytes(Paths.get(path)) as ByteArray
    val proto = BinaryChunk.unDump(data)
    val ls = LuaStateImpl(proto)

    ls.pushBoolean(true)
    printStack(ls)
    ls.pushInteger(10)
    printStack(ls)
    ls.pushNil()
    printStack(ls)
    ls.pushString("hello")
    printStack(ls)
    ls.pushValue(-4)
    printStack(ls)
    ls.replace(3)
    printStack(ls)
    ls.top = 6
    printStack(ls)
    ls.remove(-3)
    printStack(ls)
    ls.top = -5
    printStack(ls)
}

private fun printStack(ls: LuaState) {
    for (i in 1..ls.top) {
        val t = ls.type(i)
        when (t) {
            LUA_TBOOLEAN -> System.out.printf("[%b]", ls.toBoolean(i))
            LUA_TNUMBER -> if (ls.isInteger(i)) {
                System.out.printf("[%d]", ls.toInteger(i))
            } else {
                System.out.printf("[%f]", ls.toNumber(i))
            }
            LUA_TSTRING -> System.out.printf("[\"%s\"]", ls.toString(i))
            else // other values
            -> System.out.printf("[%s]", ls.typeName(t))
        }
    }
    println()
}