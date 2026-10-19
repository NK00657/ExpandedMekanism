package io.github.nk00657.expandedmekanism.client.gui

import io.github.nk00657.expandedmekanism.common.inventory.container.OilPumpJackContainer
import mekanism.client.gui.GuiMekanism
import mekanism.client.gui.element.gauge.GaugeType
import mekanism.client.gui.element.gauge.GuiEnergyGauge
import mekanism.client.gui.element.gauge.GuiFluidGauge
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class GuiOilPumpJack(
    container: OilPumpJackContainer,
    inv: Inventory,
    title: Component
) : GuiMekanism<OilPumpJackContainer>(container, inv, title){
    override fun addGuiElements() {
        super.addGuiElements()
        val title = menu.tileEntity

        addRenderableWidget(
            GuiEnergyGauge.getDummy(
                GaugeType.SMALL,
                this,
                7,
                13
            )
        )
        addRenderableWidget(
            GuiFluidGauge.getDummy(
                GaugeType.STANDARD,
                this,
                60,
                13
            )
        )
        addRenderableWidget(
            GuiFluidGauge.getDummy(
                GaugeType.STANDARD,
                this,
                100,
                13
            )
        )
    }
    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }
}