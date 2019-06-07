package moe.slk.kotlua.state

import moe.slk.kotlua.api.KFunction
import moe.slk.kotlua.binchunk.Prototype


internal data class Closure(
    val proto: Prototype? = null,
    val kFunc: KFunction? = null
)