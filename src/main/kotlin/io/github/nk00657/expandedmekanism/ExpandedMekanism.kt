package io.github.nk00657.expandedmekanism

import io.github.nk00657.expandedmekanism.common.event.ModFuelTweaks
import io.github.nk00657.expandedmekanism.registry.ModBlockEntities
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModCreativeTabs
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModGases
import io.github.nk00657.expandedmekanism.registry.ModItems
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Consumer

@Mod(ExpandedMekanism.MOD_ID)
class ExpandedMekanism {
    companion object {
        const val MOD_ID: String = "expandedmekanism"
    }

    init {
        val bus = MOD_BUS

        ModBlocks.BLOCKS.register(bus)
        ModItems.ITEMS.register(bus)
        ModBlockEntities.BLOCK_ENTITIES.register(bus)
        ModBlockEntities.register(bus)
        ModFluids.register(bus)
        ModGases.register(bus)
        ModCreativeTabs.register(bus)

        // エチレン弱体化のセットアップイベント
        bus.addListener(Consumer<FMLCommonSetupEvent> { event ->
            event.enqueueWork {
                ModFuelTweaks.nerfEthylene()
            }
        })
    }
}