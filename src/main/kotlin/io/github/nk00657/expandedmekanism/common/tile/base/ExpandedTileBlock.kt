package io.github.nk00657.expandedmekanism.common.tile.base

import mekanism.common.block.interfaces.IHasTileEntity
import mekanism.common.registration.impl.TileEntityTypeRegistryObject
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class ExpandedTileBlock<BE : TileEntityMekanism>(
    properties: Properties,
    private val tileTypeSupplier: () -> TileEntityTypeRegistryObject<BE>
) : Block(properties), EntityBlock, IHasTileEntity<BlockEntity> {

    // IHasTileEntity の実装 (Mekanism の TileEntityTypeRegistryObject を返す)
    override fun getTileType(): TileEntityTypeRegistryObject<BE> = tileTypeSupplier()

    // ブロック設置時の BlockEntity 生成
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return tileType.get().create(pos, state)
    }

    // 毎 tick のサーバー更新呼び出し
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return BlockEntityTicker { sLevel, sPos, sState, blockEntity ->
            if (blockEntity is TileEntityMekanism) {
                // Mekanism 標準の static tickServer を呼ぶ
                TileEntityMekanism.tickServer(sLevel, sPos, sState, blockEntity)
            }
        }
    }
}