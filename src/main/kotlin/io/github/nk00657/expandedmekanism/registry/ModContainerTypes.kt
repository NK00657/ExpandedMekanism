package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.inventory.container.OilPumpJackContainer
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.common.registration.impl.ContainerTypeDeferredRegister
import mekanism.common.registration.impl.ContainerTypeRegistryObject
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.network.IContainerFactory

object ModContainerTypes {
    val CONTAINER_TYPES: ContainerTypeDeferredRegister =
        ContainerTypeDeferredRegister(ExpandedMekanism.MOD_ID)

    // OilPumpJackContainer を明示的に生成するファクトリで登録
    val OIL_PUMPJACK: ContainerTypeRegistryObject<OilPumpJackContainer> =
        CONTAINER_TYPES.register("oil_pumpjack", IContainerFactory { windowId, inv, data ->
            // クライアント側でパケットから BlockPos を読み取り、該当の BlockEntity を復元
            val pos = data.readBlockPos()
            val tile = inv.player.level().getBlockEntity(pos) as? OilPumpJackBlockEntity
                ?: throw IllegalStateException("Wrong block entity type at $pos")
            OilPumpJackContainer(windowId, inv, tile)
        })

    fun register(bus: IEventBus) {
        CONTAINER_TYPES.register(bus)
    }
}