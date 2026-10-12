package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.attribute.GasAttributes
import mekanism.api.math.FloatingLong
import mekanism.common.registration.impl.GasDeferredRegister
import mekanism.common.registration.impl.GasRegistryObject
import net.minecraftforge.eventbus.api.IEventBus

object ModGases {
    val GASES: GasDeferredRegister = GasDeferredRegister(ExpandedMekanism.MOD_ID)

    inline val Number.floatingLong: FloatingLong
        get() = FloatingLong.create(this.toDouble())

    val LPG: GasRegistryObject<Gas> = GASES.register("lpg",0xABABAB, GasAttributes.Fuel(80, 8000.floatingLong))
    val GASOLINE: GasRegistryObject<Gas> = GASES.register("gasoline",0xF0E050, GasAttributes.Fuel(5, 100000.floatingLong))

    fun register(bus: IEventBus) {
        GASES.register(bus)
    }
}