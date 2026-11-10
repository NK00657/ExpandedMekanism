package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import net.minecraft.data.PackOutput
import net.minecraft.server.packs.PackType
import net.minecraft.world.level.block.Block
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.client.model.generators.ConfiguredModel
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.ForgeRegistries

class ModBlockStateProvider(
    output: PackOutput,
    private val exFileHelper: ExistingFileHelper
) : BlockStateProvider(output, ExpandedMekanism.MOD_ID, exFileHelper) {

    override fun registerStatesAndModels() {

        allowMissingBlockTexture("well_casing")
        allowMissingBlockTexture("oil_pumpjack")
        allowMissingBlockTexture("distillation_controller")
        allowMissingBlockTexture("kerosene_heater")

        simpleBlockWithItem(ModBlocks.WELL_CASING.block, cubeAll(ModBlocks.WELL_CASING.block))

        registerSimpleMachineBlock(ModBlocks.OIL_PUMPJACK.block, "oil_pumpjack")
        registerSimpleMachineBlock(ModBlocks.DISTILLATION_CONTROLLER.block, "distillation_controller")
        simpleBlockWithItem(ModBlocks.KEROSENE_HEATER.block, cubeAll(ModBlocks.KEROSENE_HEATER.block))

        ModFluids.FLUIDS.allFluids.forEach { fluidRO ->
            val fluidBlock = fluidRO.block
            val fluidName = ForgeRegistries.BLOCKS.getKey(fluidBlock)?.path ?: return@forEach
            val model = models().getBuilder("block/fluid/$fluidName")
                .texture("particle", mcLoc("block/water_still"))
            getVariantBuilder(fluidBlock).partialState().setModels(ConfiguredModel(model))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun registerSimpleMachineBlock(block: Block, name: String) {
        val model = cubeAll(block)
        getVariantBuilder(block).partialState().setModels(ConfiguredModel(model))
        simpleBlockItem(block, model)
    }

    private fun allowMissingBlockTexture(name: String){
        exFileHelper.trackGenerated(
            modLoc("block/$name"),
            PackType.CLIENT_RESOURCES,
            ".png",
            "textures"
        )
    }
}