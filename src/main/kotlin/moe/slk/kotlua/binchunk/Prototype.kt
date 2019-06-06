package moe.slk.kotlua.binchunk

import java.nio.ByteBuffer

/**
 * 函数原型定义
 *
 * 函数原型主要包含函数基本信息、指令表、常量表、upvalue 表、子函数原型表以及调试信息；基本信息又包括源文件名、起止行号、
 * 固定参数个数、是否是 vararg 函数以及运行函数所必要的寄存器数量；调试信息又包括行号表、局部变量表以及 upvalue 名列表。
 *
 * @property source 源文件名
 * @property lineDefined 函数起始行号
 * @property lastLineDefined 函数休止行号
 * @property numParams 固定参数个数
 * @property isVararg 是否为 Vararg 函数
 * @property maxStackSize 寄存器数量
 * @property code 指令表
 * @property constants 常量表
 * @property upvalues Upvalue 表
 * @property protos 子函数原型表
 * @property lineInfo 行号表
 * @property localVars 局部变量表
 * @property upvalueNames Upvalue 名表
 */
class Prototype {
    var source: String = ""
    var lineDefined: Int = 0
    var lastLineDefined: Int = 0
    var numParams: Byte = 0
    var isVararg: Byte = 0
    var maxStackSize: Byte = 0
    val code: MutableList<Int> = mutableListOf()
    val constants: MutableList<Any?> = mutableListOf()
    val upvalues: MutableList<Upvalue> = mutableListOf()
    val protos: MutableList<Prototype> = mutableListOf()
    val lineInfo: MutableList<Int> = mutableListOf()
    val localVars: MutableList<LocVar> = mutableListOf()
    val upvalueNames: MutableList<String> = mutableListOf()

    /**
     * 从字节流中读取函数原型
     *
     * @param buf 待读取的字节流
     * @param parentSource 父函数的源文件名
     */
    fun read(buf: ByteBuffer, parentSource: String = "") {
        source = BinaryChunk.getString(buf)
        if (source.isEmpty()) {
            source = parentSource
        }
        lineDefined = buf.int
        lastLineDefined = buf.int
        numParams = buf.get()
        isVararg = buf.get()
        maxStackSize = buf.get()
        readCode(buf)
        readConstants(buf)
        readUpvalues(buf)
        readProtos(buf, source)
        readLineInfo(buf)
        readLocVars(buf)
        readUpvalueNames(buf)
    }

    /**
     * 从字节流中读取指令表
     */
    private fun readCode(buf: ByteBuffer) {
        val length = buf.int

        repeat(length) {
            this.code.add(buf.int)
        }
    }

    /**
     * 从字节流中读取常量表
     */
    private fun readConstants(buf: ByteBuffer): List<Any?> {
        val length = buf.int

        repeat(length) {
            constants.add(readConstant(buf))
        }

        return constants
    }

    /**
     * 从字节流中读取常量
     */
    private fun readConstant(buf: ByteBuffer): Any? {
        return when (buf.get().toInt()) {
            TAG_NIL -> null
            TAG_BOOLEAN -> buf.get().toInt() != 0
            TAG_INTEGER -> buf.long
            TAG_NUMBER -> buf.double
            TAG_SHORT_STR, TAG_LONG_STR -> BinaryChunk.getString(buf)
            else -> throw RuntimeException("corrupted!")
        }
    }


    /**
     * 从字节流中读取 Upvalue 表
     */
    private fun readUpvalues(buf: ByteBuffer) {
        val length = buf.int

        repeat(length) {
            upvalues.add(
                Upvalue(
                    inStack = buf.get(),
                    idx = buf.get()
                )
            )
        }
    }

    /**
     * 从字节流中读取函数原型表
     */
    private fun readProtos(buf: ByteBuffer, parentSource: String = "") {
        val length = buf.int

        repeat(length) {
            read(buf, parentSource)
        }
    }

    /**
     * 从字节流中读取行号表
     */
    private fun readLineInfo(buf: ByteBuffer) {
        val size = buf.int

        repeat(size) {
            lineInfo.add(buf.int)
        }
    }

    /**
     * 从字节流中读取局部变量表
     */
    private fun readLocVars(buf: ByteBuffer) {
        val size = buf.int

        repeat(size) {
            localVars.add(
                LocVar(
                    varName = BinaryChunk.getString(buf),
                    startPC = buf.int,
                    endPC = buf.int
                )
            )
        }
    }

    /**
     * 从字节流中读取 Upvalue 名表
     */
    private fun readUpvalueNames(buf: ByteBuffer) {
        val size = buf.int

        repeat(size) {
            upvalueNames.add(BinaryChunk.getString(buf))
        }
    }
}