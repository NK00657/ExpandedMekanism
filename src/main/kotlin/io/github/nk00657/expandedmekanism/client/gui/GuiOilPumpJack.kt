package io.github.nk00657.expandedmekanism.client.gui

import io.github.nk00657.expandedmekanism.common.tile.machine.OilPumpJackBlockEntity
import mekanism.client.gui.GuiConfigurableTile
import mekanism.client.gui.element.GuiInnerScreen
import mekanism.client.gui.element.GuiTexturedElement
import mekanism.client.gui.element.gauge.GaugeType
import mekanism.client.gui.element.gauge.GuiEnergyGauge
import mekanism.client.gui.element.gauge.GuiFluidGauge
import mekanism.client.gui.element.tab.GuiEnergyTab
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.util.text.EnergyDisplay
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class GuiOilPumpJack(
    container: MekanismTileContainer<OilPumpJackBlockEntity>,
    inv: Inventory,
    title: Component
) : GuiConfigurableTile<OilPumpJackBlockEntity, MekanismTileContainer<OilPumpJackBlockEntity>>(container, inv, title){
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
            GuiInnerScreen(this, 30, 23, 80, 41) {
                listOf(
                    EnergyDisplay.of(tile.energyContainer).textComponent,
                    if (tile.isValidWell) {
                        Component.literal("Well: Valid")
                    } else {
                        Component.literal("Well: Invalid")
                    }
                )
            }
        )
        addRenderableWidget(
            GuiFluidGauge(
                { tile.crudeOilTank },
                { tile.getFluidTanks(null) },
                GaugeType.STANDARD,
                this,
                116,
                13
            )
        )
        addRenderableWidget(
            GuiFluidGauge(
                { tile.waterTank },
                { tile.getFluidTanks(null) },
                GaugeType.STANDARD,
                this,
                138,
                13
            )
        )
        addRenderableWidget(
            GuiEnergyTab(this, tile.energyContainer as GuiTexturedElement.IInfoHandler?)
        )
    }

    override fun drawForegroundText(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        renderTitleText(guiGraphics)
        drawString(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, titleTextColor())
        super.drawForegroundText(guiGraphics, mouseX, mouseY)
    }

}