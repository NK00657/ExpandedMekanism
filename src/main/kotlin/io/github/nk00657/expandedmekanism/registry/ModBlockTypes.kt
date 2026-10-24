package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.common.ExpandedLang
import io.github.nk00657.expandedmekanism.common.tile.machine.KeroseneHeaterBlockEntity
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.api.Upgrade
import mekanism.api.math.FloatingLong
import mekanism.common.block.attribute.AttributeStateFacing
import mekanism.common.block.attribute.Attributes
import mekanism.common.content.blocktype.BlockType
import mekanism.common.content.blocktype.BlockType.BlockTypeBuilder
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder
import java.util.*

object ModBlockTypes {
    private val PUMPJACK_ENERGY_USAGE: FloatingLong = FloatingLong.createConst(1_000.0)
    private val PUMPJACK_ENERGY_STORAGE: FloatingLong = FloatingLong.createConst(100_000.0)

    val WELL_CASING: BlockType = BlockTypeBuilder
        .createBlock(ExpandedLang.DESCRIPTION_WELL_CASING)
        .build()

    val OIL_PUMPJACK: BlockTypeTile<OilPumpJackBlockEntity> = BlockTileBuilder
        .createBlock({ ModBlockEntities.OIL_PUMPJACK }, ExpandedLang.DESCRIPTION_OIL_PUMPJACK)
        .withGui { ModContainerTypes.OIL_PUMPJACK }
        .withEnergyConfig({ PUMPJACK_ENERGY_USAGE }, { PUMPJACK_ENERGY_STORAGE })
        .with(
            Attributes.ACTIVE_LIGHT,
            AttributeStateFacing(),
            Attributes.INVENTORY,
            Attributes.SECURITY,
            Attributes.REDSTONE,
            Attributes.COMPARATOR
        )
        .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING))
        .build() as BlockTypeTile<OilPumpJackBlockEntity>


    val KEROSENE_HEATER: BlockTypeTile<KeroseneHeaterBlockEntity> = BlockTileBuilder
        .createBlock({ ModBlockEntities.KEROSENE_HEATER}, ExpandedLang.DESCRIPTION_KEROSENE_HEATER)
        .with(Attributes.SECURITY, Attributes.ACTIVE_MELT_LIGHT, AttributeStateFacing())
        .build() as BlockTypeTile<KeroseneHeaterBlockEntity>

}