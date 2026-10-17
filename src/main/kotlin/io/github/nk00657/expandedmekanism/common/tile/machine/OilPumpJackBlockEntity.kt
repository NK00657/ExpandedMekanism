package io.github.nk00657.expandedmekanism.common.tile.machine

import io.github.nk00657.expandedmekanism.common.tile.base.ExpandedTileEntity
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.math.FloatingLong
import mekanism.common.capabilities.energy.BasicEnergyContainer
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.capabilities.holder.fluid.FluidTankHelper
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.Tags
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

class OilPumpJackBlockEntity(
    pos: BlockPos,
    state: BlockState
) : ExpandedTileEntity(pos, state) {

    private var isValidWell: Boolean = false

    private var checkTimer: Int = 0

    private val requireEnergy: FloatingLong = FloatingLong.create(1000.0)

    lateinit var energyContainer: BasicEnergyContainer
    lateinit var crudeOilTank: BasicFluidTank
    lateinit var waterTank: BasicFluidTank

    override fun getInitialEnergyContainers(listener: IContentsListener): IEnergyContainerHolder {
        val builder = EnergyContainerHelper.forSide(this::getDirection)
        // 100_000.0: 内部バッファ最大容量 (J)
        energyContainer = BasicEnergyContainer.input(FloatingLong.create(100_000.0), listener)
        builder.addContainer(energyContainer)
        return builder.build()
    }

    override fun getInitialFluidTanks(listener: IContentsListener): IFluidTankHolder {
        val builder = FluidTankHelper.forSide(this::getDirection)
        // 10_000: 内部タンク容量 (10 バケツ分 = 10,000 mB)
        crudeOilTank = BasicFluidTank.output(10_000, listener)
        waterTank = BasicFluidTank.output(10_000, listener)

        builder.addTank(crudeOilTank, mekanism.api.RelativeSide.TOP)
        builder.addTank(
            waterTank,
            mekanism.api.RelativeSide.FRONT,
            mekanism.api.RelativeSide.BACK,
            mekanism.api.RelativeSide.LEFT,
            mekanism.api.RelativeSide.RIGHT
        )
        return builder.build()
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        val currentLevel = level ?: return

        if (checkTimer++ >= 60) {
            checkTimer = 0
            isValidWell = checkWellIntegrity(currentLevel, worldPosition)
        }
        if (!isValidWell) return

        if (energyContainer.energy.smallerThan(requireEnergy)) return

        val crudeToFill = FluidStack(ModFluids.CRUDE_OIL.stillFluid, 5)
        val waterToFill = FluidStack(Fluids.WATER, 1)

        val crudeFilled = crudeOilTank.insert(crudeToFill, Action.SIMULATE, AutomationType.INTERNAL)
        val waterFilled = waterTank.insert(waterToFill, Action.SIMULATE, AutomationType.INTERNAL)

        if (crudeOilTank.needed >= 5 && waterTank.needed >= 1) {
            energyContainer.extract(requireEnergy, Action.EXECUTE, AutomationType.INTERNAL)
            crudeOilTank.insert(crudeToFill, Action.EXECUTE, AutomationType.INTERNAL)
            waterTank.insert(waterToFill, Action.EXECUTE, AutomationType.INTERNAL)
        }

        ejectFluids(currentLevel, worldPosition)
    }

    private fun ejectFluids(level: Level, pos: BlockPos) {
        for (side in Direction.entries) {
            if (side == Direction.DOWN) continue

            val neighborTile = level.getBlockEntity(pos.relative(side)) ?: continue
            neighborTile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.opposite).ifPresent { target ->
                // 200: 1 tick あたりに外部へ押し出す最大流量 (200 mB/t)
                val maxDrain = 200
                if (side == Direction.UP) {
                    if (!crudeOilTank.isEmpty) {
                        val toDrain = crudeOilTank.extract(maxDrain, Action.SIMULATE, AutomationType.INTERNAL)
                        if (!toDrain.isEmpty) {
                            val filled = target.fill(toDrain, IFluidHandler.FluidAction.EXECUTE)
                            if (filled > 0) crudeOilTank.extract(filled, Action.EXECUTE, AutomationType.INTERNAL)
                        }
                    }
                } else {
                    if (!waterTank.isEmpty) {
                        val toDrain = waterTank.extract(maxDrain, Action.SIMULATE, AutomationType.INTERNAL)
                        if (!toDrain.isEmpty) {
                            val filled = target.fill(toDrain, IFluidHandler.FluidAction.EXECUTE)
                            if (filled > 0) waterTank.extract(filled, Action.EXECUTE, AutomationType.INTERNAL)
                        }
                    }
                }
            }
        }
    }

    private fun checkWellIntegrity(level: Level, startPos: BlockPos): Boolean {
        val biome = level.getBiome(startPos)
        if (!biome.`is`(BiomeTags.IS_OVERWORLD)) return false

        var currentY = startPos.y - 1
        val minBuildHeight = level.minBuildHeight

        while (currentY >= minBuildHeight) {
            val checkPos = BlockPos(startPos.x, currentY, startPos.z)
            val blockState = level.getBlockState(checkPos)
            if (blockState.`is`(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE) || blockState.`is`(Blocks.BEDROCK)) {
                return true
            }
            if (!blockState.`is`(ModBlocks.WELL_CASING.block)) {
                return false
            }
            currentY--
        }
        return false
    }
}