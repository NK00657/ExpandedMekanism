package io.github.nk00657.expandedmekanism.common.tile.machine

import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import mekanism.api.Action
import mekanism.api.AutomationType
import mekanism.api.IContentsListener
import mekanism.api.RelativeSide
import mekanism.api.Upgrade
import mekanism.common.capabilities.energy.MachineEnergyContainer
import mekanism.common.capabilities.fluid.BasicFluidTank
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder
import mekanism.common.capabilities.holder.fluid.FluidTankHelper
import mekanism.common.capabilities.holder.fluid.IFluidTankHolder
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableBoolean
import mekanism.common.lib.transmitter.TransmissionType
import mekanism.common.tile.component.TileComponentConfig
import mekanism.common.tile.component.TileComponentEjector
import mekanism.common.tile.component.config.DataType
import mekanism.common.tile.component.config.slot.FluidSlotInfo
import mekanism.common.tile.prefab.TileEntityConfigurableMachine
import mekanism.common.util.MekanismUtils
import net.minecraft.core.BlockPos
import net.minecraft.tags.BiomeTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.Tags
import net.minecraftforge.fluids.FluidStack
import kotlin.math.pow

/**
 * 油井ポンプのTileEntityクラス。
 * TileEntityConfigurableMachineを継承し、面ごとの搬出入設定・自動搬出・アップグレード・セキュリティに対応する。
 */
class OilPumpJackBlockEntity(
    pos: BlockPos,
    state: BlockState
) : TileEntityConfigurableMachine(ModBlocks.OIL_PUMPJACK, pos, state) {

    // 油井ケーシングが岩盤または深層岩まで正しく到達しているかのフラグ
    var isValidWell: Boolean = false
        private set

    // 油井構造チェックの間隔を計るタイマー(tick)
    private var checkTimer: Int = 0

    // スピードアップグレード倍率（1枚につき2^(枚数/8)倍、最大8枚で10倍速などMekanism標準計算にも変更可）
    private var speedMultiplier: Int = 1

    lateinit var energyContainer: MachineEnergyContainer<OilPumpJackBlockEntity>
        private set
    lateinit var crudeOilTank: BasicFluidTank
        private set
    lateinit var waterTank: BasicFluidTank
        private set

    init {
        // 面ごとの搬出入設定コンポーネントを初期化（流体とエネルギーの面設定を有効化）
        configComponent = TileComponentConfig(this, TransmissionType.FLUID, TransmissionType.ENERGY)

        // 流体タンクの面設定：OUTPUT_1 を原油タンク（初期値：上面）、OUTPUT_2 を水タンク（初期値：前後左右）に割り当て
        val fluidConfig = configComponent.getConfig(TransmissionType.FLUID)
        if (fluidConfig != null) {
            fluidConfig.addSlotInfo(DataType.OUTPUT_1, FluidSlotInfo(false, true, crudeOilTank))
            fluidConfig.addSlotInfo(DataType.OUTPUT_2, FluidSlotInfo(false, true, waterTank))
            fluidConfig.setDataType(DataType.OUTPUT_1, RelativeSide.TOP)
            fluidConfig.setDataType(
                DataType.OUTPUT_2,
                RelativeSide.FRONT,
                RelativeSide.BACK,
                RelativeSide.LEFT,
                RelativeSide.RIGHT
            )
            // デフォルトで自動搬出（Auto-Eject）をONにする
            fluidConfig.isEjecting = true
        }

        // エネルギーの面設定：すべての面を入力（INPUT）として初期設定
        configComponent.setupInputConfig(TransmissionType.ENERGY, energyContainer)

        // 自動搬出コンポーネント（Ejector）の初期化：流体のOUTPUT_1 / OUTPUT_2に設定された面へ毎tick自動で押し出す
        ejectorComponent = TileComponentEjector(this)
        ejectorComponent.setOutputData(configComponent, TransmissionType.FLUID)
    }

    /**
     * エネルギーコンテナの初期化。
     * forSideWithConfig と MachineEnergyContainer を使用することで、サイドコンフィグとアップグレードに連動する。
     */
    override fun getInitialEnergyContainers(listener: IContentsListener): IEnergyContainerHolder {
        val builder = EnergyContainerHelper.forSideWithConfig(this::getDirection, this::getConfig)
        energyContainer = MachineEnergyContainer.input(this, listener)
        builder.addContainer(energyContainer)
        return builder.build()
    }

    /**
     * 流体タンクの初期化。
     * forSideWithConfig を使用することで、GUIのサイドコンフィグ画面での面変更が即座に反映される。
     */
    override fun getInitialFluidTanks(listener: IContentsListener): IFluidTankHolder {
        val builder = FluidTankHelper.forSideWithConfig(this::getDirection, this::getConfig)
        // 10_000: 各タンクの最大容量 (10,000 mB = バケツ10杯分)
        val tankCapacity = 10_000
        crudeOilTank = BasicFluidTank.output(tankCapacity, listener)
        waterTank = BasicFluidTank.output(tankCapacity, listener)

        builder.addTank(crudeOilTank)
        builder.addTank(waterTank)
        return builder.build()
    }

    /**
     * アップグレード枚数が変更された際にMekanismから自動で呼ばれるメソッド。
     * super.recalculateUpgrades(upgrade) により MachineEnergyContainer の容量・消費電力は自動更新される。
     */
    override fun recalculateUpgrades(upgrade: Upgrade) {
        super.recalculateUpgrades(upgrade)
        if (upgrade == Upgrade.SPEED) {
            // 2.0: 底数, 8.0: 最大アップグレード枚数(8枚)で割ることで緩やかに倍率を上げる指数計算
            // （または単純に 1 + upgradeComponent.getUpgrades(Upgrade.SPEED) でも可）
            val speedUpgrades = upgradeComponent.getUpgrades(Upgrade.SPEED)
            speedMultiplier = 2.0.pow(speedUpgrades / 2.0).toInt().coerceAtLeast(1)
        }
    }

    override fun onUpdateServer() {
        // super.onUpdateServer() 内で ejectorComponent.tickServer() が呼ばれ、自動搬出が実行される
        super.onUpdateServer()
        val currentLevel = level ?: return

        // 60: 3秒（60 tick）ごとに油井の構造が有効かチェックする間隔
        val checkIntervalTicks = 60
        if (checkTimer++ >= checkIntervalTicks) {
            checkTimer = 0
            isValidWell = checkWellIntegrity(currentLevel, worldPosition)
        }

        // レッドストーン制御（パルスやRS信号による停止）と油井の有効性をチェック
        if (!isValidWell || !MekanismUtils.canFunction(this)) {
            setActive(false)
            return
        }

        val energyPerTick = energyContainer.energyPerTick
        if (energyContainer.energy.smallerThan(energyPerTick)) {
            setActive(false)
            return
        }

        // 5: 1tickあたりの基本原油生成量(mB), 1: 1tickあたりの基本水生成量(mB)
        val baseCrudeAmount = 5 * speedMultiplier
        val baseWaterAmount = 1 * speedMultiplier

        if (crudeOilTank.needed >= baseCrudeAmount && waterTank.needed >= baseWaterAmount) {
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL)
            crudeOilTank.insert(FluidStack(ModFluids.CRUDE_OIL.stillFluid, baseCrudeAmount), Action.EXECUTE, AutomationType.INTERNAL)
            waterTank.insert(FluidStack(Fluids.WATER, baseWaterAmount), Action.EXECUTE, AutomationType.INTERNAL)
            setActive(true)
        } else {
            setActive(false)
        }
    }

    /**
     * 油井ポンプ直下から深層岩または岩盤まで油井ケーシング管が繋がっているか検証する。
     */
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

    /**
     * クライアント側のGUIへ独自フィールド（isValidWell）を同期する。
     */
    override fun addContainerTrackers(container: MekanismContainer) {
        super.addContainerTrackers(container)
        container.track(SyncableBoolean.create({ isValidWell }, { isValidWell = it }))
    }
}
//by-Gemini