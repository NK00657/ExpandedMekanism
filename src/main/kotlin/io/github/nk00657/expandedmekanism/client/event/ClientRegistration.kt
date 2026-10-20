package io.github.nk00657.expandedmekanism.client.event

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.client.gui.GuiOilPumpJack
import io.github.nk00657.expandedmekanism.registry.ModContainerTypes
import io.github.nk00657.expandedmekanism.registry.ModFluids
import mekanism.client.ClientRegistrationUtil
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = ExpandedMekanism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientRegistration {
    @SubscribeEvent
    fun onClientSetup(event : FMLClientSetupEvent){
        event.enqueueWork {
            MenuScreens.register(ModContainerTypes.OIL_PUMPJACK.get(), ::GuiOilPumpJack)
        }
    }
    @SubscribeEvent
    fun registerItemColorHandlers(event: RegisterColorHandlersEvent.Item) {
        ClientRegistrationUtil.registerBucketColorHandler(event, ModFluids.FLUIDS)
    }
}