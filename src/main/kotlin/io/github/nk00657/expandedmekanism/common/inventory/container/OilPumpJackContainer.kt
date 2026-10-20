package io.github.nk00657.expandedmekanism.common.inventory.container

import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import io.github.nk00657.expandedmekanism.registry.ModContainerTypes
import mekanism.common.inventory.container.tile.MekanismTileContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player

class OilPumpJackContainer(
    id: Int,
    inv: Inventory,
    tile: OilPumpJackBlockEntity
) : MekanismTileContainer<OilPumpJackBlockEntity>(
    ModContainerTypes.OIL_PUMPJACK,
    id,
    inv,
    tile
) {

    override fun getInventoryYOffset(): Int = 84

    override fun stillValid(player: Player): Boolean {
        return !tile.isRemoved && player.distanceToSqr(
            tile.blockPos.x + 0.5,
            tile.blockPos.y + 0.5,
            tile.blockPos.z + 0.5
        ) <= 64.0
    }
}