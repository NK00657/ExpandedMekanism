package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.ForgeRegistries

class ModItemModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, ExpandedMekanism.MOD_ID,existingFileHelper){

    @Suppress("DEPRECATION")
    override fun registerModels() {
        ModItems.PETROLEUM_COKE?.get().let { item ->
            val itemName = ForgeRegistries.ITEMS.getKey(item)?.path ?: "petroleum_coke"
            existingFileHelper.trackGenerated(
                modLoc("item/$itemName"),
                PackType.CLIENT_RESOURCES,
                ".png",
                "textures"
            )
        }
        ModFluids.FLUIDS.allFluids.forEach { fluidRO ->
            val bucket = fluidRO.bucket
            val bucketPath = ForgeRegistries.ITEMS.getKey(bucket)?.path ?: return@forEach
            withExistingParent(bucketPath, ResourceLocation("forge","item/bucket"))
                .customLoader { parent, helper ->
                    DynamicFluidContainerModelBuilder.begin(parent, helper)
                }
                .fluid(fluidRO.stillFluid)
        }
    }
}