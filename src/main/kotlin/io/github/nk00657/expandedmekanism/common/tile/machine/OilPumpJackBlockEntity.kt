package io.github.nk00657.expandedmekanism.common.tile.machine

import io.github.nk00657.expandedmekanism.registry.ModBlockEntities
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.Tags
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

class OilPumpJackBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlockEntities.OIL_PUMPJACK.get(), pos, state) {

    private var isValidWell: Boolean = false
    private var checkTimer: Int = 0
    private val requireEnergy: Int = 1000 // 毎 tick 1000 FE (250 J 相当)


    // エネルギー: 最大 100,000 FE、最大受電 10,000 FE/t
    val energyStorage = EnergyStorage(100_000, 10_000, 0, 0)
    private val energyHandler = LazyOptional.of<IEnergyStorage> { energyStorage }

    // 原油タンク（天面用）: 10,000 mB
    val crudeOilTank = FluidTank(10_000) { it.fluid == ModFluids.CRUDE_OIL.stillFluid }
    private val crudeOilHandler = LazyOptional.of<IFluidHandler> { crudeOilTank }

    // 水タンク（側面用）: 10,000 mB
    val waterTank = FluidTank(10_000) { it.fluid == Fluids.WATER }
    private val waterHandler = LazyOptional.of<IFluidHandler> { waterTank }


    fun tickServer() {
        val currentLevel = level ?: return


        if (checkTimer++ >= 60) {
            checkTimer = 0
            isValidWell = checkWellIntegrity(currentLevel, worldPosition)
        }
        if (!isValidWell) return


        if (energyStorage.energyStored < requireEnergy) return

        val crudeToFill = FluidStack(ModFluids.CRUDE_OIL.stillFluid, 5)
        val waterToFill = FluidStack(Fluids.WATER, 1)

        val crudeFilled = crudeOilTank.fill(crudeToFill, IFluidHandler.FluidAction.SIMULATE)
        val waterFilled = waterTank.fill(waterToFill, IFluidHandler.FluidAction.SIMULATE)


        if (crudeFilled > 0 || waterFilled > 0) {

            energyStorage.extractEnergy(requireEnergy, false)

            // 液体注入
            if (crudeFilled > 0) crudeOilTank.fill(crudeToFill, IFluidHandler.FluidAction.EXECUTE)
            if (waterFilled > 0) waterTank.fill(waterToFill, IFluidHandler.FluidAction.EXECUTE)
            setChanged()
        }

        // 周囲への自動押し出し
        ejectFluids(currentLevel, worldPosition)
    }


    private fun ejectFluids(level: Level, pos: BlockPos) {
        for (side in Direction.values()) {
            if (side == Direction.DOWN) continue

            val neighborTile = level.getBlockEntity(pos.relative(side)) ?: continue
            neighborTile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.opposite).ifPresent { target ->
                // 天面 (UP) -> 原油のみ
                if (side == Direction.UP) {
                    if (!crudeOilTank.isEmpty) {
                        val toDrain = crudeOilTank.drain(200, IFluidHandler.FluidAction.SIMULATE)
                        if (!toDrain.isEmpty) {
                            val filled = target.fill(toDrain, IFluidHandler.FluidAction.EXECUTE)
                            if (filled > 0) crudeOilTank.drain(filled, IFluidHandler.FluidAction.EXECUTE)
                        }
                    }
                }
                // 水平側面 -> 水のみ
                else {
                    if (!waterTank.isEmpty) {
                        val toDrain = waterTank.drain(200, IFluidHandler.FluidAction.SIMULATE)
                        if (!toDrain.isEmpty) {
                            val filled = target.fill(toDrain, IFluidHandler.FluidAction.EXECUTE)
                            if (filled > 0) waterTank.drain(filled, IFluidHandler.FluidAction.EXECUTE)
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
            if (!blockState.`is`(ModBlocks.WELL_CASING.get())) {
                return false
            }
            currentY--
        }
        return false
    }


    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        // 電線からの給電は全方向から受け入れ
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast()
        }

        // 流体パイプの接続制限
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return when (side) {
                Direction.UP -> crudeOilHandler.cast()   // 天面は原油のみ
                Direction.DOWN -> LazyOptional.empty()   // 底面は接続不可
                else -> waterHandler.cast()              // 水平側面は水のみ
            }
        }

        return super.getCapability(cap, side)
    }


    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("Energy", energyStorage.energyStored)
        tag.put("CrudeOil", crudeOilTank.writeToNBT(CompoundTag()))
        tag.put("Water", waterTank.writeToNBT(CompoundTag()))
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        val energyStored = tag.getInt("Energy")
        // EnergyStorage の残量を復帰
        energyStorage.extractEnergy(energyStorage.energyStored, false)
        energyStorage.receiveEnergy(energyStored, false)

        crudeOilTank.readFromNBT(tag.getCompound("CrudeOil"))
        waterTank.readFromNBT(tag.getCompound("Water"))
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyHandler.invalidate()
        crudeOilHandler.invalidate()
        waterHandler.invalidate()
    }
}