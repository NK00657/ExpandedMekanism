package io.github.nk00657.expandedmekanism.client.gui

import io.github.nk00657.expandedmekanism.common.tile.component.IExpandedHeatMachine
import mekanism.client.gui.GuiMekanismTile
import mekanism.client.gui.element.tab.GuiHeatTab
import mekanism.common.MekanismLang
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.util.MekanismUtils
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit

fun <TILE> GuiMekanismTile<TILE, *>.createHeatTab(): GuiHeatTab
    where TILE : TileEntityMekanism, TILE : IExpandedHeatMachine{
        return GuiHeatTab(this) {
            val temp = MekanismUtils.getTemperatureDisplay(tileEntity.totalTemperature, TemperatureUnit.KELVIN, true)
            val transfer = MekanismUtils.getTemperatureDisplay(tileEntity.lastTransferLoss, TemperatureUnit.KELVIN, false)
            val environment = MekanismUtils.getTemperatureDisplay(tileEntity.lastEnvironmentLoss, TemperatureUnit.KELVIN, false)
            listOf(
                MekanismLang.TEMPERATURE.translate(temp),
                MekanismLang.TRANSFERRED_RATE.translate(transfer),
                MekanismLang.DISSIPATED_RATE.translate(environment)
            )
        }
    }