package io.github.nk00657.expandedmekanism

import io.github.nk00657.expandedmekanism.common.event.ModFuelTweaks
import io.github.nk00657.expandedmekanism.registry.ModCreativeTabs
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModGases
import io.github.nk00657.expandedmekanism.registry.ModItems
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ExpandedMekanism.MOD_ID)
object ExpandedMekanism {
    const val MOD_ID = "expandedmekanism"

    init {
        MOD_BUS.register(this)
        ModGases.register(MOD_BUS)
        ModItems.register(MOD_BUS)
        ModFluids.register(MOD_BUS)
        ModCreativeTabs.register(MOD_BUS)
        MOD_BUS.addListener<FMLCommonSetupEvent> { event ->
            event.enqueueWork {
                ModFuelTweaks.nerfEthylene()
            }
        }
    }
}