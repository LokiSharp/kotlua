package moe.slk.kotlua.vm

import moe.slk.kotlua.api.LuaVM

typealias OpAction = (Instruction, LuaVM) -> Unit