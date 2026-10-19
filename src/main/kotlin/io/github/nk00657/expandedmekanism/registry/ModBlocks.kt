package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.block.KeroseneHeaterBlock
import io.github.nk00657.expandedmekanism.common.block.OilPumpJackBlock
import mekanism.common.registration.impl.BlockDeferredRegister
import mekanism.common.registration.impl.BlockRegistryObject
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.eventbus.api.IEventBus

object ModBlocks {

    val BLOCKS: BlockDeferredRegister = BlockDeferredRegister(ExpandedMekanism.MOD_ID)

    val WELL_CASING: BlockRegistryObject<Block, BlockItem> = BLOCKS.register("well_casing") {
        Block(BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops())
    }

    val OIL_PUMPJACK: BlockRegistryObject<OilPumpJackBlock, BlockItem> = BLOCKS.register(
        "oil_pumpjack"
    ) { OilPumpJackBlock(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops()) }

    val KEROSENE_HEATER: BlockRegistryObject<KeroseneHeaterBlock, BlockItem> = BLOCKS.register("kerosene_heater") {
        KeroseneHeaterBlock(BlockBehaviour.Properties.of().strength(4.0f).requiresCorrectToolForDrops())
    }

    fun register(bus: IEventBus) {
        BLOCKS.register(bus)
    }
}