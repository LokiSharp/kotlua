package moe.slk.kotlua.binchunk

/**
 * 函数原型定义
 *
 * 函数原型主要包含函数基本信息、指令表、常量表、upvalue 表、子函数原型表以及调试信息；基本信息又包括源文件名、起止行号、
 * 固定参数个数、是否是 vararg 函数以及运行函数所必要的寄存器数量；调试信息又包括行号表、局部变量表以及 upvalue 名列表。
 *
 * @param source 源文件名
 * @param lineDefined 函数起始行号
 * @param lastLineDefined 函数休止行号
 * @param numParams 固定参数个数
 * @param isVararg 是否为 Vararg 函数
 * @param maxStackSize 寄存器数量
 * @param code 指令表
 * @param constants 常量表
 * @param upvalues Upvalue 表
 * @param protos 子函数原型表
 * @param lineInfo 行号表
 * @param localVars 局部变量表
 * @param upvalueNames Upvalue 名表
 */
data class Prototype(
    val source: String,
    val lineDefined: Int,
    val lastLineDefined: Int,
    val numParams: Byte,
    val isVararg: Byte,
    val maxStackSize: Byte,
    val code: List<Int>,
    val constants: List<Any?>,
    val upvalues: List<Upvalue>,
    val protos: List<Prototype>,
    val lineInfo: List<Int>,
    val localVars: List<LocVar>,
    val upvalueNames: List<String>
)