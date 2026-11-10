package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.tags.BlockTags
import net.minecraftforge.common.data.BlockTagsProvider
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModBlockTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output,lookupProvider, ExpandedMekanism.MOD_ID,existingFileHelper){
    override fun addTags(provider: HolderLookup.Provider) {
        val pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE)
        ModBlocks.BLOCKS.allBlocks.forEach { blockProvider ->
            pickaxeTag.add(blockProvider.block)
        }
    }
}