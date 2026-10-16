package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object ModCreativeTabs {
    val TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExpandedMekanism.MOD_ID)

    val MAIN_TAB: RegistryObject<CreativeModeTab> = TABS.register("main"){
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.${ExpandedMekanism.MOD_ID}"))
            .icon{ ItemStack(ModFluids.CRUDE_OIL.bucket) }
            .displayItems{ _, output ->
                ModFluids.FLUIDS.allFluids.forEach { fluidRegistryObject ->
                    output.accept(fluidRegistryObject.bucket)
                }
                ModItems.ITEMS
            }
            .build()
    }


    fun register(bus: IEventBus) {
        TABS.register(bus)
    }
}