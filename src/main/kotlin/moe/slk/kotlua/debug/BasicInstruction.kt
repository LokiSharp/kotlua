package moe.slk.kotlua.debug

import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.api.LuaType.*
import moe.slk.kotlua.binchunk.types.Prototype
import moe.slk.kotlua.binchunk.unDump
import moe.slk.kotlua.state.LuaStateImpl
import moe.slk.kotlua.vm.Instruction
import moe.slk.kotlua.vm.OpCode
import java.nio.file.Files
import java.nio.file.Paths

fun testBasicInstructions(path: String) {
    val data = Files.readAllBytes(Paths.get(path))
    val proto = unDump(data)
    luaMain(proto)
}

private fun luaMain(proto: Prototype) {
    val vm = LuaStateImpl(proto)
    vm.top = proto.maxStackSize.toInt()

    while (true) {
        val pc = vm.getPC()
        val i = Instruction(vm.fetch())
        val opCode = i.opCode
        if (opCode !== OpCode.RETURN) {
            opCode.action?.invoke(i, vm)

            System.out.printf("[%02d] %-8s ", pc + 1, opCode.name)
            printStack(vm)
        } else {
            break
        }
    }
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