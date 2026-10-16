package io.github.nk00657.expandedmekanism.common.block

import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class OilPumpJackBlock(
    properties: Properties
) : Block(properties), EntityBlock{
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OilPumpJackBlockEntity(pos, state)
    }
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide) return null

        return BlockEntityTicker { _, _, _, blockEntity ->
            if (blockEntity is OilPumpJackBlockEntity) {
                blockEntity.tickServer()
            }
        }
    }
}