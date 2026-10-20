package io.github.nk00657.expandedmekanism.client.gui

import io.github.nk00657.expandedmekanism.common.inventory.container.OilPumpJackContainer
import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.client.gui.GuiMekanism
import mekanism.client.gui.GuiMekanismTile
import mekanism.client.gui.element.gauge.GaugeType
import mekanism.client.gui.element.gauge.GuiEnergyGauge
import mekanism.client.gui.element.gauge.GuiFluidGauge
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.MekanismLang
import mekanism.common.util.text.EnergyDisplay
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class GuiOilPumpJack(
    container: OilPumpJackContainer,
    inv: Inventory,
    title: Component
) : GuiMekanismTile<OilPumpJackBlockEntity, OilPumpJackContainer>(container, inv, title){
    init {
        dynamicSlots = true
    }

    override fun addGuiElements() {
        super.addGuiElements()

        addRenderableWidget(
            GuiEnergyGauge(
                tile.energyContainer,
                GaugeType.SMALL,
                this,
                7,
                13
            )
        )
        addRenderableWidget(
            GuiFluidGauge(
                { tile.crudeOilTank },
                { tile.getFluidTanks(null) },
                GaugeType.STANDARD,
                this,
                108,
                13
            )
        )
        addRenderableWidget(
            GuiFluidGauge(
                {tile.waterTank},
                {tile.getFluidTanks(null)},
                GaugeType.STANDARD,
                this,
                138,
                13
            )
        )
        addRenderableWidget(
            GuiEnergyTab(this, {
                listOf(
                    MekanismLang.USING.translate(EnergyDisplay.of(tile.requireEnergy)),
                    MekanismLang.NEEDED.translate(EnergyDisplay.of(tile.energyContainer))
                )
            })
        )
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)

        drawString(guiGraphics,playerInventoryTitle,inventoryLabelX,inventoryLabelY,titleTextColor())
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }

}