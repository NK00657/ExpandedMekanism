package io.github.nk00657.expandedmekanism.common.tile.base

import mekanism.api.providers.IBlockProvider
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

abstract class ExpandedTileEntity(
    pos: BlockPos,
    state: BlockState
) : TileEntityMekanism(


    object : IBlockProvider {
        override fun getBlock(): Block = state.block
        override fun asItem(): Item = state.block.asItem()

    },
    pos,
    state
){


    override fun getName(): Component {
        return super.getName()
    }

    fun tickServer() {
        this.onUpdateServer()
    }

}