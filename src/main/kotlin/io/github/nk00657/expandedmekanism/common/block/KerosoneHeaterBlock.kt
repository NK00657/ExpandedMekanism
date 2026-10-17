package io.github.nk00657.expandedmekanism.common.block

import io.github.nk00657.expandedmekanism.common.tile.base.ExpandedTileBlock
import io.github.nk00657.expandedmekanism.common.tile.machine.KeroseneHeaterBlockEntity
import io.github.nk00657.expandedmekanism.registry.ModBlockEntities

class KeroseneHeaterBlock(properties: Properties) :
    ExpandedTileBlock<KeroseneHeaterBlockEntity>(
        properties,
        { ModBlockEntities.KEROSENE_HEATER }
    )