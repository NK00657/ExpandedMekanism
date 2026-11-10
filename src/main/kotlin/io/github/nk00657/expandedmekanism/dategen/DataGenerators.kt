package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = ExpandedMekanism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object DataGenerators {

    @SubscribeEvent
    fun gatherDate(event: GatherDataEvent){
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        val includeClient = event.includeClient()
        generator.addProvider(includeClient, ModBlockStateProvider(packOutput, existingFileHelper))
        generator.addProvider(includeClient, ModItemModelProvider(packOutput, existingFileHelper))
        generator.addProvider(includeClient, ModLanguageProvider(packOutput, "en_us"))
        generator.addProvider(includeClient, ModLanguageProvider(packOutput, "ja_jp"))

        val includeServer = event.includeServer()
        generator.addProvider(includeServer, ModRecipeProvider(packOutput))
        generator.addProvider(includeServer, ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper))
    }
}