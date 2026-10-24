package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.registration.impl.ContainerTypeDeferredRegister
import mekanism.common.registration.impl.ContainerTypeRegistryObject
import net.minecraftforge.eventbus.api.IEventBus

object ModContainerTypes {
    val CONTAINER_TYPES: ContainerTypeDeferredRegister =
        ContainerTypeDeferredRegister(ExpandedMekanism.MOD_ID)

    val OIL_PUMPJACK: ContainerTypeRegistryObject<MekanismTileContainer<OilPumpJackBlockEntity>> =
        CONTAINER_TYPES.register(ModBlocks.OIL_PUMPJACK, OilPumpJackBlockEntity::class.java)

    fun register(bus: IEventBus) {
        CONTAINER_TYPES.register(bus)
    }
}