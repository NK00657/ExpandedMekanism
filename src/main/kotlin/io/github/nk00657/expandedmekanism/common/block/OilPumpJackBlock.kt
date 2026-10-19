package io.github.nk00657.expandedmekanism.common.block

import io.github.nk00657.expandedmekanism.common.tile.base.ExpandedTileBlock
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import io.github.nk00657.expandedmekanism.registry.ModBlockEntities
import io.github.nk00657.expandedmekanism.registry.ModContainerTypes

class OilPumpJackBlock(properties: Properties) :
    ExpandedTileBlock<OilPumpJackBlockEntity>(
        properties,
        { ModBlockEntities.OIL_PUMPJACK },
        { ModContainerTypes.OIL_PUMPJACK }
    )