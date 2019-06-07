package moe.slk.kotlua.binchunk

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