package io.github.nk00657.expandedmekanism.common.tile.component

import mekanism.api.IContentsListener
import mekanism.api.heat.HeatAPI
import mekanism.common.capabilities.heat.BasicHeatCapacitor
import mekanism.common.capabilities.heat.CachedAmbientTemperature
import mekanism.common.capabilities.holder.heat.HeatCapacitorHelper
import mekanism.common.capabilities.holder.heat.IHeatCapacitorHolder
import mekanism.common.inventory.container.MekanismContainer
import mekanism.common.inventory.container.sync.SyncableDouble
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.ITileComponent
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import java.util.function.DoubleSupplier
import java.util.function.Supplier

interface IExpandedHeatMachine {
    val lastEnvironmentLoss: Double
    val lastTransferLoss: Double

    fun attachHeatComponent(tile: TileEntityMekanism)
    fun tickHeat(tile: TileEntityMekanism)
}
class HeatMachineDelegate : IExpandedHeatMachine, ITileComponent{

    override var lastEnvironmentLoss: Double = 0.0
        private set

    override var lastTransferLoss: Double = 0.0
        private set

    override fun attachHeatComponent(tile: TileEntityMekanism) {
        tile.addComponent(this)
    }
    override fun tickHeat(tile: TileEntityMekanism) {
        // 隣接ブロックとの熱交換および大気への放熱をシミュレートして結果を保持
        val loss = tile.simulate()
        lastEnvironmentLoss = loss.environmentTransfer
        lastTransferLoss = loss.adjacentTransfer
    }
    override fun trackForMainContainer(container: MekanismContainer) {
        container.track(SyncableDouble.create({ lastEnvironmentLoss }, { lastEnvironmentLoss = it }))
        container.track(SyncableDouble.create({ lastTransferLoss }, { lastTransferLoss = it }))
    }
    override fun read(nbtTags: CompoundTag) {}
    override fun write(nbtTags: CompoundTag) {}


    companion object {
        /**
        * @param directionSupplier ブロックの向きを返すSupplier（this::getDirection）
        * @param heatCapacity 熱容量（大きいほど温まりにくく冷めにくい）
        * @param inverseConduction 逆熱伝導係数（小さいほど隣接ブロックと素早く熱交換する）
        * @param inverseInsulation 逆断熱係数（大きいほど環境への放熱が減る）
        * @param ambientTemperature Mekanismから渡されるキャッシュ済み環境温度
        * @param listener 内容変更リスナー
        * @param onCreated 生成されたBasicHeatCapacitorを機械側のプロパティへ代入するコールバック
        */
        fun createSingleCapacitorHolder(
            directionSupplier: Supplier<Direction>,
            heatCapacity: Double = 10.0,
            inverseConduction: Double = 5.0,
            inverseInsulation: Double = 10.0,
            ambientTemperature: CachedAmbientTemperature?,
            listener: IContentsListener?,
            onCreated: (BasicHeatCapacitor) -> Unit
        ): IHeatCapacitorHolder{
            val builder = HeatCapacitorHelper.forSide(directionSupplier)
            val capacitor = BasicHeatCapacitor.create(
                heatCapacity,
                inverseConduction,
                inverseInsulation,
                (ambientTemperature ?: { HeatAPI.AMBIENT_TEMP}) as DoubleSupplier?,
                listener
            )
            onCreated(capacitor)
            builder.addCapacitor(capacitor)
            return builder.build()
        }
    }
}