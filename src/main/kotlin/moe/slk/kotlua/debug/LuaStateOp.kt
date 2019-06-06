package moe.slk.kotlua.debug

import moe.slk.kotlua.api.ArithOp.LUA_OPADD
import moe.slk.kotlua.api.ArithOp.LUA_OPBNOT
import moe.slk.kotlua.api.CmpOp.LUA_OPEQ
import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.api.LuaType.*
import moe.slk.kotlua.binchunk.BinaryChunk
import moe.slk.kotlua.state.LuaStateImpl
import java.nio.file.Files
import java.nio.file.Paths

fun testOp(path: String) {
    val data = Files.readAllBytes(Paths.get(path)) as ByteArray
    val proto = BinaryChunk.unDump(data)
    val ls = LuaStateImpl(proto)
    ls.pushInteger(1)
    ls.pushString("2.0")
    ls.pushString("3.0")
    ls.pushNumber(4.0)
    printStack(ls)

    ls.arith(LUA_OPADD)
    printStack(ls)
    ls.arith(LUA_OPBNOT)
    printStack(ls)
    ls.len(2)
    printStack(ls)
    ls.concat(3)
    printStack(ls)
    ls.pushBoolean(ls.compare(1, 2, LUA_OPEQ))
    printStack(ls)
}

private fun printStack(ls: LuaState) {
    val top = ls.top

    repeat(top) {
        val i = it + 1

        when (val t = ls.type(i)) {
            LUA_TBOOLEAN -> System.out.printf("[%b]", ls.toBoolean(i))

            LUA_TNUMBER -> {
                if (ls.isInteger(i)) {
                    print("[${ls.toInteger(i)}]")
                } else {
                    print("[${ls.toNumber(i)}]")
                }
            }
            LUA_TSTRING -> print("[${ls.toString(i)}]")

            else -> print("[${ls.typeName(t)}]")
        }
    }

    println()
}