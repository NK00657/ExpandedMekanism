package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlockEntities {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExpandedMekanism.MOD_ID)

    val OIL_PUMPJACK: RegistryObject<BlockEntityType<OilPumpJackBlockEntity>> =
        BLOCK_ENTITIES.register("oil_pumpjack") {
            BlockEntityType.Builder.of(
                ::OilPumpJackBlockEntity,
                ModBlocks.OIL_PUMPJACK.get()
            ).build(null)
        }
}