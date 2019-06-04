package moe.slk.kotlua

import moe.slk.kotlua.binchunk.types.Prototype
import moe.slk.kotlua.binchunk.unDump
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val data = Files.readAllBytes(Paths.get(args[0]))
        val proto = unDump(data)
        list(proto)
    }
}

private fun list(proto: Prototype) {
    printHeader(proto)
    printCode(proto)
    printDetail(proto)

    proto.protos.forEach(::list)
}

private fun printHeader(prototype: Prototype) {
    with(prototype) {
        val funcType = if (lineDefined > 0) "function" else "main"
        val varargFlag = if (isVararg > 0) "+" else ""

        println("$funcType <$source:$lineDefined,$lastLineDefined> (${code.size} instructions)")

        println(
            "$numParams$varargFlag params, " +
                    "$maxStackSize slots, " +
                    "${upvalueNames.size} upvalues, " +
                    "${localVars.size} locals, " +
                    "${constants.size} constants, " +
                    "${protos.size} functions"
        )
    }
}

private fun printCode(prototype: Prototype) {
    with(prototype) {
        for (i in code.indices) {
            val line = if (lineInfo.isNotEmpty()) lineInfo[i].toString() else "-"
            println("\t${i + 1}\t[$line]\t0x${code[i].formatHex(8)}")
        }
    }
}

private fun Int.formatHex(digits: Int) = java.lang.String.format("%0${digits}X", this)


private fun printDetail(prototype: Prototype) {
    with(prototype) {
        println("constants (${constants.size}):")
        var i = 1
        for (k in constants) {
            println("\t${i++}\t${constantToString(k)}")
        }

        i = 0
        println("locals (${localVars.size}):")
        for (locVar in localVars) {
            println("\t${i++}\t${locVar.varName}\t${locVar.startPC + 1}\t${locVar.endPC + 1}")
        }

        i = 0
        println("upvalues (${upvalues.size}):")
        for (upval in upvalues) {
            val name = if (upvalueNames.isNotEmpty()) upvalueNames[i] else "-"
            println("\t${i++}\t$name\t${upval.inStack}\t${upval.idx}")
        }
    }
}

private fun constantToString(k: Any?): String {
    return if (k == null) {
        "nil"
    } else if (k is String) {
        "\"" + k + "\""
    } else {
        k.toString()
    }
}