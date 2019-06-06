package kotlua.test

import moe.slk.kotlua.binchunk.BinaryChunk
import moe.slk.kotlua.state.LuaStateImpl
import moe.slk.kotlua.vm.Instruction
import moe.slk.kotlua.vm.OpCode
import org.junit.Assert
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class BasicInstructions {
    @Test
    fun testBasicInstructions() {
        val data = Files.readAllBytes(
            Paths.get(
                this.javaClass.getResource("/sum.luac").toURI()
            )
        )
        val proto = BinaryChunk.unDump(data)

        val vm = LuaStateImpl(proto)
        vm.top = proto.maxStackSize.toInt()

        while (true) {
            val i = Instruction(vm.fetch())
            val opCode = i.opCode
            if (opCode !== OpCode.RETURN) {
                opCode.action?.invoke(i, vm)
            } else {
                break
            }
        }
        var sum = 0L
        for (i in (1..100)) {
            if (i % 2 == 0) {
                sum += i
            }
        }
        Assert.assertEquals(vm.toInteger(1), sum)
    }
}