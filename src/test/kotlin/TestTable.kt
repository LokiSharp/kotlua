package kotlua.test

import moe.slk.kotlua.binchunk.BinaryChunk
import moe.slk.kotlua.state.LuaStateImpl
import moe.slk.kotlua.vm.Instruction
import moe.slk.kotlua.vm.OpCode
import org.junit.Assert
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class TestTable {
    @Test
    fun testTable() {
        val data = Files.readAllBytes(
            Paths.get(
                this.javaClass.getResource("/table.luac").toURI()
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

        Assert.assertEquals(vm.toString(2), "cBaBar3")
    }
}