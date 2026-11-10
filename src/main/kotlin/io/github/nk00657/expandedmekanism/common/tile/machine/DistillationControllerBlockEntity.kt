package io.github.nk00657.expandedmekanism.common.tile.machine

import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModItems
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.Upgrade
import mekanism.api.heat.HeatAPI
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.capabilities.heat.BasicHeatCapacitor
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.holder.fluid.FluidTankHelper
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder
import mekanism.common.capabilities.holder.slot.InventorySlotHelper
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableBoolean
import mekanism.common.inventory.container.sync.SyncableDouble
import mekanism.common.inventory.slot.BasicInventorySlot
import mekanism.common.inventory.slot.OutputInventorySlot
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tile.component.TileComponentConfig
import mekanism.common.tile.component.TileComponentEjector
import mekanism.common.tile.prefab.TileEntityConfigurableMachine
import mekanism.common.util.MekanismUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * 熱駆動式の蒸留塔コントローラーBlockEntity。
 * 600K以上で原油を6種の液体へ分留し、700K以上では熱分解により石油コークスを生成する。
 */
class DistillationControllerBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : TileEntityConfigurableMachine(ModBlocks.DISTILLATION_CONTROLLER, pos, state) {

    var isFormed: Boolean = false
        private set

    var lastEnvironmentLoss: Double = 0.0
        private set
    var lastTransferLoss: Double = 0.0
        private set

    lateinit var outputSlot: BasicInventorySlot
        private set

    private var structureCheckTimer: Int = 0

    private var speedMultiplier: Int = 1

    lateinit var heatCapacitor: BasicHeatCapacitor
        private set
    lateinit var inputTank: BasicFluidTank
        private set
    lateinit var asphaltTank: BasicFluidTank
        private set
    lateinit var heavyOilTank: BasicFluidTank
        private set
    lateinit var lightOilTank: BasicFluidTank
        private set
    lateinit var keroseneTank: BasicFluidTank
        private set
    lateinit var naphthaTank: BasicFluidTank
        private set
    lateinit var gasolineTank: BasicFluidTank
        private set

    private val outputTanksByLevel: List<BasicFluidTank> by lazy {
        listOf(asphaltTank, heavyOilTank, lightOilTank, keroseneTank, naphthaTank, gasolineTank)
    }

    init {

        configComponent = TileComponentConfig(this, TransmissionType.FLUID, TransmissionType.ITEM)
        configComponent.setupInputConfig(TransmissionType.FLUID, inputTank)

        configComponent.setupOutputConfig(TransmissionType.ITEM, outputSlot, RelativeSide.BOTTOM)

        ejectorComponent = TileComponentEjector(this)
        ejectorComponent.setOutputData(configComponent, TransmissionType.ITEM)
    }

    override fun getInitialInventory(listener: IContentsListener?): IInventorySlotHolder? {
        val builder = InventorySlotHelper.forSideWithConfig(this::getDirection, this::getConfig)
        val slotXPosition = 150
        val slotYPosition = 55

        outputSlot = OutputInventorySlot.at(
            listener,
            slotXPosition,
            slotYPosition
        )
        builder.addSlot(outputSlot)

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

    override fun getInitialFluidTanks(listener: IContentsListener?): IFluidTankHolder? {
        val builder = FluidTankHelper.forSideWithConfig(this::getDirection, this::getConfig)

        val inputCapacity = 20_000
        val outputCapacity = 20_000

        inputTank = BasicFluidTank.input(
            inputCapacity,
            { stack -> stack.fluid == ModFluids.CRUDE_OIL.stillFluid },
            listener
        )
        asphaltTank = BasicFluidTank.output(outputCapacity, listener)
        heavyOilTank = BasicFluidTank.output(outputCapacity, listener)
        lightOilTank = BasicFluidTank.output(outputCapacity, listener)
        keroseneTank = BasicFluidTank.output(outputCapacity, listener)
        naphthaTank = BasicFluidTank.output(outputCapacity, listener)
        gasolineTank = BasicFluidTank.output(outputCapacity, listener)

        builder.addTank(inputTank)
        builder.addTank(asphaltTank)
        builder.addTank(heavyOilTank)
        builder.addTank(lightOilTank)
        builder.addTank(keroseneTank)
        builder.addTank(naphthaTank)
        builder.addTank(gasolineTank)

        return builder.build()
    }

    override fun recalculateUpgrades(upgrade: Upgrade) {
        super.recalculateUpgrades(upgrade)
        if (upgrade == Upgrade.SPEED) {
            // 1: ベース倍率にスピードアップグレードの枚数を加算
            speedMultiplier = 1 + upgradeComponent.getUpgrades(Upgrade.SPEED)
        }
    }

    override fun onUpdateServer() {
        super.onUpdateServer()
        val currentLevel = level ?: return

        val loss = simulate()
        lastEnvironmentLoss = loss.environmentTransfer
        lastTransferLoss = loss.adjacentTransfer

        val checkInterval = 20
        if (structureCheckTimer++ >= checkInterval) {
            structureCheckTimer = 0
            isFormed = checkTowerStructure(currentLevel)
        }
        if (!isFormed) {
            active = false
            return
        }
        if (MekanismUtils.canFunction(this)) {
            processDistillation()
        } else {
            active = false
        }
        ejectFluidsPerLevel(currentLevel)
    }

    private fun processDistillation() {

        val crudeConsumption = 10 * speedMultiplier

        val asphaltOut = 1 * speedMultiplier
        val standardOut = 2 * speedMultiplier


        if (inputTank.fluidAmount < crudeConsumption) {
            active = false
            return
        }

        // 700.0: 石油コークスが生成される高温しきい値(K)
        val cokeTempThreshold = 700.0
        // 600.0: 通常の分留が行われる温度しきい値(K)
        val normalTempThreshold = 600.0

        if (heatCapacitor.temperature >= cokeTempThreshold) {
            // 石油コークス用の処理（700K以上）
            // 1: 1回の処理で生成される石油コークスの個数
            val byproductCount = 1
            val item = ModItems.PETROLEUM_COKE?.get() ?: return
            val resultStack = ItemStack(item, byproductCount)

            val remainder = outputSlot.insertItem(resultStack, Action.SIMULATE, AutomationType.INTERNAL)
            if (remainder.isEmpty) {
                inputTank.extract(crudeConsumption, Action.EXECUTE, AutomationType.INTERNAL)
                outputSlot.insertItem(resultStack, Action.EXECUTE, AutomationType.INTERNAL)
                active = true
            } else {
                active = false
            }
        } else if (heatCapacitor.temperature >= normalTempThreshold) {
            // 通常の6種分留処理（600K以上 700K未満）
            val hasSpace = asphaltTank.needed >= asphaltOut &&
                    heavyOilTank.needed >= standardOut &&
                    lightOilTank.needed >= standardOut &&
                    keroseneTank.needed >= standardOut &&
                    naphthaTank.needed >= standardOut &&
                    gasolineTank.needed >= standardOut
            if (!hasSpace) {
                active = false
                return
            }
            inputTank.extract(crudeConsumption, Action.EXECUTE, AutomationType.INTERNAL)

            asphaltTank.insert(FluidStack(ModFluids.ASPHALT.stillFluid, asphaltOut), Action.EXECUTE, AutomationType.INTERNAL)
            heavyOilTank.insert(FluidStack(ModFluids.HEAVY_OIL.stillFluid, standardOut), Action.EXECUTE, AutomationType.INTERNAL)
            lightOilTank.insert(FluidStack(ModFluids.LIGHT_OIL.stillFluid, standardOut), Action.EXECUTE, AutomationType.INTERNAL)
            keroseneTank.insert(FluidStack(ModFluids.KEROSENE.stillFluid, standardOut), Action.EXECUTE, AutomationType.INTERNAL)
            naphthaTank.insert(FluidStack(ModFluids.NAPHTHA.stillFluid, standardOut), Action.EXECUTE, AutomationType.INTERNAL)
            gasolineTank.insert(FluidStack(ModFluids.GASOLINE.stillFluid, standardOut), Action.EXECUTE, AutomationType.INTERNAL)

            active = true
        } else {
            active = false
        }
    }

    private fun checkTowerStructure(level: Level): Boolean {
        val requireHeight = 6
        for (offsetY in 1..requireHeight) {
            val checkPos = worldPosition.above(offsetY)
            if (!level.getBlockState(checkPos).`is`(ModBlocks.WELL_CASING.block)) {
                return false
            }
        }
        return true
    }

    private fun ejectFluidsPerLevel(level: Level) {
        // 256: 各段から1tickあたりに搬出する最大流量 (256 mB/t)
        val maxEjectRate = 256 * speedMultiplier

        for ((index, tank) in outputTanksByLevel.withIndex()) {
            if (tank.isEmpty) continue

            val levelPos = worldPosition.above(index + 1)

            for (side in Direction.Plane.HORIZONTAL) {
                if (tank.isEmpty) break
                val neighborTile = level.getBlockEntity(levelPos.relative(side)) ?: continue

                neighborTile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.opposite).ifPresent { target ->
                    val toDrain = tank.extract(maxEjectRate, Action.SIMULATE, AutomationType.INTERNAL)
                    if (!toDrain.isEmpty) {
                        val filled = target.fill(toDrain, IFluidHandler.FluidAction.EXECUTE)
                        if (filled > 0) {
                            tank.extract(filled, Action.EXECUTE, AutomationType.INTERNAL)
                        }
                    }
                }
            }
        }
    }

    override fun addContainerTrackers(container: MekanismContainer) {
        super.addContainerTrackers(container)
        container.track(SyncableBoolean.create({ isFormed }, { isFormed = it }))
        container.track(SyncableDouble.create({ lastEnvironmentLoss }, { lastEnvironmentLoss = it }))
        container.track(SyncableDouble.create({ lastTransferLoss }, { lastTransferLoss = it }))
    }
}