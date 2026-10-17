package io.github.nk00657.expandedmekanism.common.tile.machine

import io.github.nk00657.expandedmekanism.common.tile.base.ExpandedTileEntity
import io.github.nk00657.expandedmekanism.registry.ModFluids
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.heat.HeatAPI
import mekanism.api.heat.IHeatHandler
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.capabilities.heat.BasicHeatCapacitor
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.holder.fluid.FluidTankHelper
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import org.slf4j.event.Level


class KeroseneHeaterBlockEntity(
    pos: BlockPos,
    state: BlockState
) : ExpandedTileEntity(pos, state){
    lateinit var keroseneTank: BasicFluidTank

    lateinit var heatCapacitor: BasicHeatCapacitor

    var lastEnvironmentLoss: Double = 0.0
    var lastTransferLoss: Double = 0.0

    override fun getInitialFluidTanks(listener: IContentsListener?): IFluidTankHolder? {
        val builder = FluidTankHelper.forSide(this::getDirection)

        keroseneTank = BasicFluidTank.input(10_000, { it.fluid == ModFluids.KEROSENE.stillFluid }, listener)
        builder.addTank(keroseneTank)
        return builder.build()
    }

    override fun getInitialHeatCapacitors(
        listener: IContentsListener?,
        ambientTemperature: CachedAmbientTemperature?
    ): IHeatCapacitorHolder? {
        val builder = HeatCapacitorHelper.forSide(this::getDirection)

        heatCapacitor = BasicHeatCapacitor.create(
            10.0,
            5.0,
            10.0,
            { HeatAPI.getAmbientTemp(level, worldPosition) },
            listener
        )
        builder.addCapacitor(heatCapacitor)
        return builder.build()
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        val maxTemp = 1500.0
        if (heatCapacitor.temperature < maxTemp && !keroseneTank.isEmpty){
            val drained = keroseneTank.extract(1, Action.EXECUTE, AutomationType.INTERNAL)
            if(!drained.isEmpty){
                heatCapacitor.handleHeat(1000.0)
            }
        }
        val loss = simulate()
        lastEnvironmentLoss = loss.environmentTransfer
        lastTransferLoss = loss.adjacentTransfer
    }



}