package moe.slk.kotlua.vm

import moe.slk.kotlua.api.ArithOp
import moe.slk.kotlua.api.ArithOp.*
import moe.slk.kotlua.api.CmpOp
import moe.slk.kotlua.api.CmpOp.*
import moe.slk.kotlua.api.LuaType.LUA_TSTRING
import moe.slk.kotlua.api.LuaVM

/* misc */

/**
 * 用于把源寄存器里的值移动到目标寄存器里
 *
 * R(A) := R(B)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun move(i: Instruction, vm: LuaVM) {
    vm.copy(i.b + 1, i.a + 1)
}

/**
 * 用于执行无条件跳转
 *
 * pc+=sBx; if (A) close all upvalues >= R(A - 1)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun jmp(i: Instruction, vm: LuaVM) {
    vm.addPC(i.sBx)
    if (i.a != 0) {
        throw Exception("todo: jmp!")
    }
}

/* load */

/**
 * 用于给连续n个寄存器放置 nil 值
 *
 * R(A), R(A+1), ..., R(A+B) := nil
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun loadNil(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b

    vm.pushNil()

    repeat(b) {
        vm.copy(-1, a + it)
    }

    vm.pop(1)
}

/**
 * 用于给单个寄存器设置布尔值
 *
 * R(A) := (bool)B; if (C) pc++
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun loadBool(i: Instruction, vm: LuaVM) {
    vm.pushBoolean(i.b != 0)

    vm.replace(i.a + 1)
    if (i.c != 0) {
        vm.addPC(1)
    }
}

/**
 * 用于将常量表里的某个常量加载到指定寄存器
 *
 * R(A) := Kst(Bx)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun loadK(i: Instruction, vm: LuaVM) {
    vm.getConst(i.bx)
    vm.replace(i.a + 1)
}

/**
 * 用于将常量表里的某个常量加载到指定寄存器
 *
 * R(A) := Kst(extra arg)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun loadKx(i: Instruction, vm: LuaVM) {
    val ax = Instruction(vm.fetch()).ax

    vm.getConst(ax)
    vm.replace(i.a + 1)
}

/* arith */

/**
 * 加
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun add(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPADD)
} // +

/**
 * 减
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun sub(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPSUB)
} // -

/**
 * 乘
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun mul(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPMUL)
} // *

/**
 * 模
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun mod(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPMOD)
} // %

/**
 * 乘
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun pow(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPPOW)
} // ^

/**
 * 无符号除
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun div(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPDIV)
} // /

/**
 * 有符号除
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun idiv(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPIDIV)
} // //

/**
 * 按位与
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun band(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPBAND)
} // &

/**
 * 按位或
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun bor(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPBOR)
} // |

/**
 * 按位异或
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun bxor(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPBXOR)
} // ~

/**
 * 左位移
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun shl(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPSHL)
} // <<

/**
 * 右位移
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun shr(i: Instruction, vm: LuaVM) {
    binaryArith(i, vm, LUA_OPSHR)
} // >>

/**
 * 取反
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun unm(i: Instruction, vm: LuaVM) {
    unaryArith(i, vm, LUA_OPUNM)
} // -

/**
 * 按位取反
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun bnot(i: Instruction, vm: LuaVM) {
    unaryArith(i, vm, LUA_OPBNOT)
} // ~


/**
 * 二进制运算
 *
 * R(A) := RK(B) op RK(C)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 * @param op 运算符
 */
private fun binaryArith(i: Instruction, vm: LuaVM, op: ArithOp) {
    vm.getRK(i.b)
    vm.getRK(i.c)
    vm.arith(op)

    vm.replace(i.a + 1)
}

/**
 * 一元运算
 *
 * R(A) := op R(B)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 * @param op 运算符
 */
private fun unaryArith(i: Instruction, vm: LuaVM, op: ArithOp) {
    vm.pushValue(i.b + 1)
    vm.arith(op)
    vm.replace(i.a + 1)
}

/* compare */
/**
 * 等于
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun eq(i: Instruction, vm: LuaVM) {
    compare(i, vm, LUA_OPEQ)
} // ==

/**
 * 小于
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun lt(i: Instruction, vm: LuaVM) {
    compare(i, vm, LUA_OPLT)
} // <

/**
 * 小于等于
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun le(i: Instruction, vm: LuaVM) {
    compare(i, vm, LUA_OPLE)
} // <=

/**
 * 比较运算
 *
 * if ((RK(B) op RK(C)) ~= A) then pc++
 *
 * @param i 指令
 * @param vm 虚拟机对象
 * @param op 运算符
 */
private fun compare(i: Instruction, vm: LuaVM, op: CmpOp) {
    vm.getRK(i.b)
    vm.getRK(i.c)
    if (vm.compare(-2, -1, op) != (i.a != 0)) {
        vm.addPC(1)
    }
    vm.pop(2)
}

/* logical */
/**
 * 逻辑非
 *
 * R(A) := not R(B)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun not(i: Instruction, vm: LuaVM) {
    vm.pushBoolean(!vm.toBoolean(i.b + 1))
    vm.replace(i.a + 1)
}

/**
 * 判断寄存器 A 中的值转换为布尔值之后是否和操作数C表示的布尔值一致，如果一致，则跳过下一条指令
 *
 * if not (R(A) <=> C) then pc++
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun test(i: Instruction, vm: LuaVM) {
    if (vm.toBoolean(i.a + 1) != (i.c != 0)) {
        vm.addPC(1)
    }
}

/**
 * 判断寄存器 B 中的值转换为布尔值之后是否和操作数 C 表示的布尔值一致，如果一致则将寄存器 B 中的值复制到寄
 * 存器 A 中，否则跳过下一条指令
 *
 * if (R(B) <=> C) then R(A) := R(B) else pc++
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun testSet(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b + 1
    if (vm.toBoolean(b) == (i.c != 0)) {
        vm.copy(b, a)
    } else {
        vm.addPC(1)
    }
}

/* len & concat */

/**
 * 长度运算符
 *
 * R(A) := length of R(B)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun length(i: Instruction, vm: LuaVM) {
    vm.len(i.b + 1)
    vm.replace(i.a + 1)
}

/**
 * 将连续 n 个寄存器里的值拼接，将结果放入另一个寄存器
 *
 * R(A) := R(B).. ... ..R(C)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun concat(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b + 1
    val c = i.c + 1
    val n = c - b + 1

    vm.checkStack(n)
    for (j in b..c) {
        vm.pushValue(j)
    }
    vm.concat(n)
    vm.replace(a)
}

/* for */

/**
 * 数值形式 for 循环
 *
 * R(A)-=R(A+2); pc+=sBx
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun forPrep(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val sBx = i.sBx

    if (vm.type(a) === LUA_TSTRING) {
        vm.pushNumber(vm.toNumber(a))
        vm.replace(a)
    }
    if (vm.type(a + 1) === LUA_TSTRING) {
        vm.pushNumber(vm.toNumber(a + 1))
        vm.replace(a + 1)
    }
    if (vm.type(a + 2) === LUA_TSTRING) {
        vm.pushNumber(vm.toNumber(a + 2))
        vm.replace(a + 2)
    }

    vm.pushValue(a)
    vm.pushValue(a + 2)
    vm.arith(LUA_OPSUB)
    vm.replace(a)
    vm.addPC(sBx)
}

/**
 * 通用形式 for 循环
 *
 * R(A)+=R(A+2);
 * if R(A) <?= R(A+1) then {
 *   pc+=sBx; R(A+3)=R(A)
 * }
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun forLoop(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val sBx = i.sBx

    // R(A)+=R(A+2);
    vm.pushValue(a + 2)
    vm.pushValue(a)
    vm.arith(LUA_OPADD)
    vm.replace(a)

    val isPositiveStep = vm.toNumber(a + 2) >= 0
    if (isPositiveStep && vm.compare(a, a + 1, LUA_OPLE)
        ||
        !isPositiveStep && vm.compare(a + 1, a, LUA_OPLE)
    ) {
        // pc+=sBx; R(A+3)=R(A)
        vm.addPC(sBx)
        vm.copy(a, a + 3)
    }
}


/**
 * 创建新表
 *
 * R(A) := {} (size = B,C)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun newTable(i: Instruction, vm: LuaVM) {
    vm.createTable(fb2int(i.b), fb2int(i.c))
    vm.replace(i.a + 1)
}


/**
 * 表索引取值
 *
 * R(A) := R(B)[RK(C)]
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun getTable(i: Instruction, vm: LuaVM) {
    vm.getRK(i.c)
    vm.getTable(i.b + 1)
    vm.replace(i.a + 1)
}

/**
 * 根据键往表里赋值
 *
 * R(A)[RK(B)] := RK(C)
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun setTable(i: Instruction, vm: LuaVM) {
    vm.getRK(i.b)
    vm.getRK(i.c)
    vm.setTable(i.a + 1)
}

/**
 * 根据键往数组里赋值
 *
 * R(A)[(C-1)*FPF+i] := R(A+i), 1 <= i <= B
 *
 * @param i 指令
 * @param vm 虚拟机对象
 */
fun setList(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    var b = i.b
    var c = i.c
    c = if (c > 0) c - 1 else Instruction(vm.fetch()).ax

    val bIsZero = b == 0
    if (bIsZero) {
        b = vm.toInteger(-1).toInt() - a - 1
        vm.pop(1)
    }

    vm.checkStack(1)
    var idx = c * LFIELDS_PER_FLUSH
    for (j in 1..b) {
        idx++
        vm.pushValue(a + j)
        vm.setI(a, idx.toLong())
    }

    if (bIsZero) {
        for (j in vm.registerCount() + 1..vm.top) {
            idx++
            vm.pushValue(j)
            vm.setI(a, idx.toLong())
        }

        // clear stack
        vm.top = vm.registerCount()
    }
}

/**
 * 浮点字节编码
 *
 * @param i 整型
 * @return 浮点编码整型
 */
fun int2fb(i: Int): Int {
    var x = i
    var e = 0 /* exponent */
    if (x < 8) {
        return x
    }
    while (x >= 8 shl 4) { /* coarse steps */
        x = x + 0xf shr 4 /* x = ceil(x / 16) */
        e += 4
    }
    while (x >= 8 shl 1) { /* fine steps */
        x = x + 1 shr 1 /* x = ceil(x / 2) */
        e++
    }
    return e + 1 shl 3 or x - 8
}

/**
 * 浮点字节解码
 *
 * @param i 浮点编码整型
 * @return 整型
 */
fun fb2int(i: Int): Int {
    return if (i < 8) {
        i
    } else {
        (i and 7) + 8 shl (i shr 3) - 1
    }
}

/* call */

// R(A+1) := R(B); R(A) := R(B)[RK(C)]
fun _self(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b + 1
    val c = i.c
    vm.copy(b, a + 1)
    vm.getRK(c)
    vm.getTable(b)
    vm.replace(a)
}

// R(A) := closure(KPROTO[Bx])
fun closure(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val bx = i.bx
    vm.loadProto(bx)
    vm.replace(a)
}

// R(A), R(A+1), ..., R(A+B-2) = vararg
fun vararg(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b
    if (b != 1) { // b==0 or b>1
        vm.loadVararg(b - 1)
        popResults(a, b, vm)
    }
}

// return R(A)(R(A+1), ... ,R(A+B-1))
fun tailCall(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b
    // todo: optimize tail call!
    val c = 0
    val nArgs = pushFuncAndArgs(a, b, vm)
    vm.call(nArgs, c - 1)
    popResults(a, c, vm)
}

// R(A), ... ,R(A+C-2) := R(A)(R(A+1), ... ,R(A+B-1))
fun call(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b
    val c = i.c
    val nArgs = pushFuncAndArgs(a, b, vm)
    vm.call(nArgs, c - 1)
    popResults(a, c, vm)
}

// return R(A), ... ,R(A+B-2)
fun _return(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val b = i.b
    when {
        b == 1 -> {
            // no return values
        }
        b > 1 -> {
            // b-1 return values
            vm.checkStack(b - 1)
            for (j in a..a + b - 2) {
                vm.pushValue(j)
            }
        }
        else -> fixStack(a, vm)
    }
}

private fun pushFuncAndArgs(a: Int, b: Int, vm: LuaVM): Int {
    return if (b >= 1) {
        vm.checkStack(b)
        for (i in a until a + b) {
            vm.pushValue(i)
        }
        b - 1
    } else {
        fixStack(a, vm)
        vm.top - vm.registerCount() - 1
    }
}

private fun fixStack(a: Int, vm: LuaVM) {
    val x = vm.toInteger(-1).toInt()
    vm.pop(1)

    vm.checkStack(x - a)
    for (i in a until x) {
        vm.pushValue(i)
    }
    vm.rotate(vm.registerCount() + 1, x - a)
}

private fun popResults(a: Int, c: Int, vm: LuaVM) {
    when {
        c == 1 -> {
            // no results
        }
        c > 1 -> for (i in a + c - 2 downTo a) {
            vm.replace(i)
        }
        else -> {
            // leave results on stack
            vm.checkStack(1)
            vm.pushInteger(a.toLong())
        }
    }
}

/* upvalues */

// R(A) := UpValue[B][RK(C)]
fun getTabUp(i: Instruction, vm: LuaVM) {
    val a = i.a + 1
    val c = i.c

    vm.pushGlobalTable()
    vm.getRK(c)
    vm.getTable(-2)
    vm.replace(a)
    vm.pop(1)
}