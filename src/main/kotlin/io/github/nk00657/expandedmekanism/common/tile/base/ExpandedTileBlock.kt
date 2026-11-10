package io.github.nk00657.expandedmekanism.common.tile.base

import mekanism.common.block.interfaces.IHasTileEntity
import mekanism.common.registration.impl.TileEntityTypeRegistryObject
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks


abstract class ExpandedTileBlock<BE : TileEntityMekanism>(
    properties: Properties,
    private val tileTypeSupplier: () -> TileEntityTypeRegistryObject<BE>,
    private val menuFactory: ((Int, Inventory, BE) -> AbstractContainerMenu)? = null
) : Block(properties), EntityBlock, IHasTileEntity<BlockEntity> {

    override fun getTileType(): TileEntityTypeRegistryObject<BE> = tileTypeSupplier()

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return tileType.get().create(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return BlockEntityTicker { sLevel, sPos, sState, blockEntity ->
            if (blockEntity is TileEntityMekanism) {
                TileEntityMekanism.tickServer(sLevel, sPos, sState, blockEntity)
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        val factory = menuFactory ?: return InteractionResult.PASS
        if (player.isShiftKeyDown) return InteractionResult.PASS

        if (!level.isClientSide && player is ServerPlayer) {
            val tile = level.getBlockEntity(pos) as? BE ?: return InteractionResult.PASS
            NetworkHooks.openScreen(player, object : MenuProvider {
                override fun getDisplayName(): Component = Component.translatable(descriptionId)

                override fun createMenu(id: Int, inv: Inventory, p: Player): AbstractContainerMenu {
                    return factory(id, inv, tile)
                }
            }, pos)
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }
}