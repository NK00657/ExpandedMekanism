package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.tile.machine.DistillationControllerBlockEntity
import io.github.nk00657.expandedmekanism.common.tile.machine.KeroseneHeaterBlockEntity
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.common.block.prefab.BlockBase
import mekanism.common.block.prefab.BlockTile
import mekanism.common.content.blocktype.BlockType
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.item.block.ItemBlockTooltip
import mekanism.common.item.block.machine.ItemBlockMachine
import mekanism.common.registration.impl.BlockDeferredRegister
import mekanism.common.registration.impl.BlockRegistryObject
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus

object ModBlocks {

    val BLOCKS: BlockDeferredRegister = BlockDeferredRegister(ExpandedMekanism.MOD_ID)

    val WELL_CASING: BlockRegistryObject<BlockBase<BlockType>, ItemBlockTooltip<BlockBase<BlockType>>> =
        BLOCKS.register(
            "well_casing",
            {
                // 3.5f: ブロックの硬さ(destroyTime), 9.0f: 爆破耐性(explosionResistance)
                BlockBase(ModBlockTypes.WELL_CASING) { props -> props.strength(3.5f, 9.0f).requiresCorrectToolForDrops() }
            },
            { block -> ItemBlockTooltip(block, Item.Properties()) }
        )

    // ModBlocks.kt 内の OIL_PUMPJACK の型定義
    val OIL_PUMPJACK: BlockRegistryObject<BlockTile.BlockTileModel<OilPumpJackBlockEntity, BlockTypeTile<OilPumpJackBlockEntity>>, ItemBlockMachine> =
        BLOCKS.register(
            "oil_pumpjack",
            {
                // 5.0f: ブロックの硬さ(destroyTime), 16.0f: 爆破耐性(explosionResistance)
                BlockTile.BlockTileModel(ModBlockTypes.OIL_PUMPJACK) { props ->
                    props.strength(5.0f, 16.0f).requiresCorrectToolForDrops()
                }
            },
            ::ItemBlockMachine
        )

    val KEROSENE_HEATER: BlockRegistryObject<BlockTile.BlockTileModel<KeroseneHeaterBlockEntity, BlockTypeTile<KeroseneHeaterBlockEntity>>, ItemBlockMachine> =
        BLOCKS.register(
            "kerosene_heater",
            {
                // 4.0f: ブロックの硬さ, 12.0f: 爆破耐性
                BlockTile.BlockTileModel(ModBlockTypes.KEROSENE_HEATER) { props ->
                    props.strength(4.0f, 12.0f).requiresCorrectToolForDrops()
                }
            },
            ::ItemBlockMachine
        )

    val DISTILLATION_CONTROLLER: BlockRegistryObject<BlockTile.BlockTileModel<DistillationControllerBlockEntity, BlockTypeTile<DistillationControllerBlockEntity>>, ItemBlockMachine> =
        BLOCKS.register(
            "distillation_controller",
            {
                BlockTile.BlockTileModel(ModBlockTypes.DISTILLATION_CONTROLLER) { props ->
                    props.strength(4.0f,12.0f).requiresCorrectToolForDrops()
                }
            },
            ::ItemBlockMachine
        )

    fun register(bus: IEventBus) {
        BLOCKS.register(bus)
    }
}