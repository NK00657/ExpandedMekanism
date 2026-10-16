package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.block.OilPumpJackBlock
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier

object ModBlocks {
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ExpandedMekanism.MOD_ID)

    val WELL_CASING: RegistryObject<Block> = registerBlock("well_casing"){
        Block(BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops())
    }

    val OIL_PUMPJACK: RegistryObject<Block> = registerBlock("oil_pumpjack") {
        OilPumpJackBlock(BlockBehaviour.Properties.of().strength(5.0f).requiresCorrectToolForDrops())
    }

    val KEROSENE_HEATER: RegistryObject<Block> = registerBlock("kerosene_heater"){
        TODO("Implement Kerosene Heater Block")
    }


    private fun <T : Block> registerBlock(
        name: String,
        itemProperties: Item.Properties = Item.Properties(),
        blockSupplier: Supplier<T>
    ): RegistryObject<T>{
        val blockObj = BLOCKS.register(name, blockSupplier)

        ModItems.ITEMS.register(name){
            BlockItem(blockObj.get(), itemProperties)
        }
        return blockObj
    }
}