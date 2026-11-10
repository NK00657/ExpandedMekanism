package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModGases
import io.github.nk00657.expandedmekanism.registry.ModItems
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.registries.ForgeRegistries

class ModLanguageProvider(
    output: PackOutput,
    locale: String
) : LanguageProvider(output, ExpandedMekanism.MOD_ID, locale){
    override fun addTranslations() {
        add("itemGroup.${ExpandedMekanism.MOD_ID}","Expanded Mekanism")
        add("itemGroup.${ExpandedMekanism.MOD_ID}.main", "Expanded Mekanism")

        ModBlocks.BLOCKS.allBlocks.forEach { blockProvider ->
            val block = blockProvider.block
            val path = ForgeRegistries.BLOCKS.getKey(block)?.path ?: return@forEach
            val englishName = toTitleCase(path)

            add(block, englishName)
            add("description.${ExpandedMekanism.MOD_ID}.$path", "Description for $englishName")
        }
        ModItems.ITEMS.entries.forEach { itemRO ->
            val item = itemRO.get()
            val path = itemRO.id.path
            add(item, toTitleCase(path))
        }
        ModFluids.FLUIDS.allFluids.forEach { fluidRO ->
            val fluidBlock = fluidRO.block
            val path = ForgeRegistries.BLOCKS.getKey(fluidBlock)?.path ?: return@forEach
            val englishName = toTitleCase(path)

            add(fluidBlock, englishName)
            add("fluid.${ExpandedMekanism.MOD_ID}.$path", englishName)
            add(fluidRO.bucket, "$englishName Bucket")
        }


    }
    private fun toTitleCase(registryPath: String): String {
        return registryPath.split("_")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { char -> char.uppercaseChar() }
            }
    }
}