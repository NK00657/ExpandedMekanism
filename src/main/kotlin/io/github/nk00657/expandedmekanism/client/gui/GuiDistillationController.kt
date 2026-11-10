package io.github.nk00657.expandedmekanism.client.gui

import io.github.nk00657.expandedmekanism.common.tile.machine.DistillationControllerBlockEntity
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.GuiInnerScreen
import mekanism.client.gui.element.gauge.GaugeType
import mekanism.client.gui.element.gauge.GuiFluidGauge
import mekanism.client.gui.element.tab.GuiHeatTab
import mekanism.common.MekanismLang
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.util.MekanismUtils
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class GuiDistillationController(
    container: MekanismTileContainer<DistillationControllerBlockEntity>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<DistillationControllerBlockEntity, MekanismTileContainer<DistillationControllerBlockEntity>>(
    container,
    inv,
    title
){
    init{
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()


        addRenderableWidget(
            GuiFluidGauge(
                { tile.inputTank },
                { tile.getFluidTanks(null) },
                GaugeType.STANDARD,
                this,
                7,
                13
            )
        )

        val startX = 29
        val spacingX = 19
        val gaugeY = 13

        val outputTanks = listOf(
            tile.asphaltTank,
            tile.heavyOilTank,
            tile.lightOilTank,
            tile.keroseneTank,
            tile.naphthaTank,
            tile.gasolineTank
        )

        outputTanks.forEachIndexed { index, tank ->
            addRenderableWidget(
                GuiFluidGauge(
                    { tank },
                    { tile.getFluidTanks(null) },
                    GaugeType.STANDARD,
                    this,
                    startX + (index * spacingX),
                    gaugeY
                )
            )
        }

        addRenderableWidget(
            GuiInnerScreen(this, 145, 13, 24, 36) {
                listOf(
                    if (tile.isFormed) Component.literal("OK") else Component.literal("NG")
                )
            }
        )

        addRenderableWidget(
            GuiHeatTab(this) {
                val temp = MekanismUtils.getTemperatureDisplay(tile.totalTemperature, TemperatureUnit.KELVIN, true)
                val transfer = MekanismUtils.getTemperatureDisplay(tile.lastTransferLoss, TemperatureUnit.KELVIN, false)
                val environment = MekanismUtils.getTemperatureDisplay(tile.lastEnvironmentLoss, TemperatureUnit.KELVIN, false)
                listOf(
                    MekanismLang.TEMPERATURE.translate(temp),
                    MekanismLang.TRANSFERRED_RATE.translate(transfer),
                    MekanismLang.DISSIPATED_RATE.translate(environment)
                )
            }
        )
    }
}