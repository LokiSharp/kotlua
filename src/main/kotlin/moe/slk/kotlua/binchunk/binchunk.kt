package moe.slk.kotlua.binchunk

import moe.slk.kotlua.binchunk.types.LocVar
import moe.slk.kotlua.binchunk.types.Prototype
import moe.slk.kotlua.binchunk.types.Upvalue
import java.nio.ByteBuffer
import java.nio.ByteOrder


private class Reader(val buf: ByteBuffer) {
    fun checkHeader() {
        when {
            !LUA_SIGNATURE.contentEquals(readBytes(4)) -> throw RuntimeException("not a precompiled chunk!")
            buf.get().toInt() != LUAC_VERSION -> throw RuntimeException("version mismatch!")
            buf.get().toInt() != LUAC_FORMAT -> throw RuntimeException("format mismatch!")
            !LUAC_DATA.contentEquals(readBytes(6)) -> throw RuntimeException("corrupted!")
            buf.get().toInt() != CINT_SIZE -> throw RuntimeException("int size mismatch!")
            buf.get().toInt() != CSIZET_SIZE -> throw RuntimeException("size_t size mismatch!")
            buf.get().toInt() != INSTRUCTION_SIZE -> throw RuntimeException("instruction size mismatch!")
            buf.get().toInt() != LUA_INTEGER_SIZE -> throw RuntimeException("lua_Integer size mismatch!")
            buf.get().toInt() != LUA_NUMBER_SIZE -> throw RuntimeException("lua_Number size mismatch!")
            buf.long != LUAC_INT.toLong() -> throw RuntimeException("endianness mismatch!")
            buf.double != LUAC_NUM -> throw RuntimeException("float format mismatch!")
        }
    }

    fun readProto(parentSource: String = ""): Prototype {
        var source = readString()
        if (source.isEmpty()) {
            source = parentSource
        }

        return Prototype(
            source = source,
            lineDefined = buf.int,
            lastLineDefined = buf.int,
            numParams = buf.get(),
            isVararg = buf.get(),
            maxStackSize = buf.get(),
            code = readCode(),
            constants = readConstants(),
            upvalues = readUpvalues(),
            protos = readProtos(),
            lineInfo = readLineInfo(),
            localVars = readLocVars(),
            upvalueNames = readUpvalueNames()
        )
    }

    fun readCode(): List<Int> {
        val length = buf.int
        val code = ArrayList<Int>(length)

        repeat(length) {
            code.add(buf.int)
        }

        return code
    }

    fun readConstants(): List<Any?> {
        val length = buf.int
        val constants = ArrayList<Any?>(length)

        repeat(length) {
            constants.add(readConstant())
        }

        return constants
    }

    fun readConstant(): Any? {
        return when (buf.get().toInt()) {
            TAG_NIL -> null
            TAG_BOOLEAN -> buf.get().toInt() != 0
            TAG_INTEGER -> buf.long
            TAG_NUMBER -> buf.double
            TAG_SHORT_STR, TAG_LONG_STR -> readString()
            else -> throw RuntimeException("corrupted!")
        }
    }

    fun readString(): String {
        var size = buf.get().toInt()

        if (size == 0) {
            return ""
        }

        if (size == 0xFF) {
            size = buf.long.toInt()
        }

        return String(readBytes(size - 1))
    }

    fun readUpvalues(): List<Upvalue> {
        val length = buf.int
        val upvalues = ArrayList<Upvalue>(length)

        repeat(length) {
            upvalues.add(
                Upvalue(
                    inStack = buf.get(),
                    idx = buf.get()
                )
            )
        }

        return upvalues
    }

    fun readProtos(parentSource: String = ""): List<Prototype> {
        val length = buf.int
        val protos = ArrayList<Prototype>(length)

        repeat(length) {
            protos.add(readProto(parentSource))
        }

        return protos
    }

    fun readLineInfo(): List<Int> {
        val size = buf.int
        val lineInfo = ArrayList<Int>()

        repeat(size) {
            lineInfo.add(buf.int)
        }

        return lineInfo
    }

    fun readLocVars(): List<LocVar> {
        val size = buf.int
        val locVars = ArrayList<LocVar>(size)

        repeat(size) {
            locVars.add(
                LocVar(
                    varName = readString(),
                    startPC = buf.int,
                    endPC = buf.int
                )
            )
        }

        return locVars
    }

    fun readUpvalueNames(): List<String> {
        val size = buf.int
        val upvalueNames = ArrayList<String>(size)

        repeat(size) {
            upvalueNames.add(readString())
        }

        return upvalueNames
    }

    fun readByte() = buf.get()

    fun readBytes(n: Int): ByteArray {
        val a = ByteArray(n)
        buf.get(a)
        return a
    }
}


fun unDump(data: ByteArray): Prototype {
    val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
    val reader = Reader(buf)

    reader.checkHeader()
    reader.readByte()
    return reader.readProto()
}