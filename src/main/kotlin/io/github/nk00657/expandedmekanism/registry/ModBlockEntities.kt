package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.tile.machine.KeroseneHeaterBlockEntity
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister
import mekanism.common.registration.impl.TileEntityTypeRegistryObject
import net.minecraftforge.eventbus.api.IEventBus

object ModBlockEntities {

    val BLOCK_ENTITIES: TileEntityTypeDeferredRegister =
        TileEntityTypeDeferredRegister(ExpandedMekanism.MOD_ID)

    val OIL_PUMPJACK: TileEntityTypeRegistryObject<OilPumpJackBlockEntity> =
        BLOCK_ENTITIES.register(ModBlocks.OIL_PUMPJACK, ::OilPumpJackBlockEntity)

    val KEROSENE_HEATER: TileEntityTypeRegistryObject<KeroseneHeaterBlockEntity> =
        BLOCK_ENTITIES.register(ModBlocks.KEROSENE_HEATER, ::KeroseneHeaterBlockEntity)

    fun register(bus: IEventBus) {
        BLOCK_ENTITIES.register(bus)
    }
}