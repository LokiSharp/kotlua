package moe.slk.kotlua.vm

import moe.slk.kotlua.vm.OpArgMask.*
import moe.slk.kotlua.vm.OpMode.*

/*
 31       22       13       5    0
  +-------+^------+-^-----+-^-----
  |b=9bits |c=9bits |a=8bits|op=6|
  +-------+^------+-^-----+-^-----
  |    bx=18bits    |a=8bits|op=6|
  +-------+^------+-^-----+-^-----
  |   sbx=18bits    |a=8bits|op=6|
  +-------+^------+-^-----+-^-----
  |    ax=26bits            |op=6|
  +-------+^------+-^-----+-^-----
 31      23      15       7      0
*/

const val MAXARG_Bx = (1 shl 18) - 1   // 262143
const val MAXARG_sBx = MAXARG_Bx shr 1 // 131071

/**
 * 指令码解码器
 * @param content 指令码
 * @property
 */
class Instruction(content: Int) {

    val opCode = OpCode.values()[content and 0x3F]

    val a = content shr 6 and 0xFF

    val c = content shr 14 and 0x1FF

    val b = content shr 23 and 0x1FF

    val bx = content.ushr(14)

    val sBx = bx - MAXARG_sBx

    val ax = content.ushr(6)

    /**
     * 打印指令操作数
     */
    fun printOperands() {
        with(this) {
            when (opCode.opMode) {
                iABC -> {
                    print("iABC\t")
                    print("$a")
                    if (opCode.argBMode !== OpArgN) {
                        val b = b
                        print(" ${if (b > 0xFF) -1 - (b and 0xFF) else b}")
                    }
                    if (opCode.argCMode !== OpArgN) {
                        val c = c
                        print(" ${if (c > 0xFF) -1 - (c and 0xFF) else c}")
                    }
                }
                iABx -> {
                    print("iABx\t")
                    print("$a")
                    val bx = bx
                    if (opCode.argBMode === OpArgK) {
                        print(" ${-1 - bx}")
                    } else if (opCode.argBMode === OpArgU) {
                        print(" $bx")
                    }
                }
                iAsBx -> {
                    print("iAsBx\t")
                    print("$a $sBx")
                }
                iAx -> {
                    print("iAx\t")
                    print("${-1 - ax}")
                }
            }
        }

    }
}