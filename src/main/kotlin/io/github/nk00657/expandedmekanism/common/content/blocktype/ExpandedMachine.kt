package io.github.nk00657.expandedmekanism.common.content.blocktype

import mekanism.api.Upgrade
import mekanism.api.text.ILangEntry
import mekanism.common.block.attribute.AttributeParticleFX
import mekanism.common.block.attribute.AttributeStateFacing
import mekanism.common.block.attribute.AttributeUpgradeSupport
import mekanism.common.block.attribute.Attributes
import mekanism.common.content.blocktype.BlockTypeTile
import mekanism.common.lib.math.Pos3D
import mekanism.common.registration.impl.TileEntityTypeRegistryObject
import mekanism.common.tile.base.TileEntityMekanism
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import java.util.EnumSet
import java.util.function.Supplier

class ExpandedMachine<TILE: TileEntityMekanism>(
    tileEntityRegister: Supplier<TileEntityTypeRegistryObject<TILE>>,
    description: ILangEntry
) : BlockTypeTile<TILE>(tileEntityRegister, description)/*{
    init {
        add(
            AttributeParticleFX()
                .add(ParticleTypes.SMOKE) { rand ->
                    Pos3D((rand.nextFloat() * 0.6f - 0.3f).toDouble(), (rand.nextFloat() * 6.0f / 16.0f).toDouble(), 0.52)
                }
                .add(DustParticleOptions.REDSTONE) { rand ->
                    Pos3D((rand.nextFloat() * 0.6f - 0.3f).toDouble(), (rand.nextFloat() * 6.0f / 16.0f).toDouble(), 0.52)
                }
        )
        add(
            Attributes.ACTIVE_LIGHT,
            AttributeStateFacing(),
            Attributes.INVENTORY,
            Attributes.SECURITY,
            Attributes.REDSTONE,
            Attributes.COMPARATOR
        )
        add(AttributeUpgradeSupport(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING)))
    }
    class ExpandedMachineBuilder<MACHINE : ExpandedMachine<TILE>, TILE : TileEntityMekanism, T : ExpandedMachineBuilder<MACHINE, TILE, T>>(
        holder: MACHINE
    ) : BlockTileBuilder<MACHINE, TILE, T>(holder) {

        companion object {
            fun <TILE : TileEntityMekanism> createMachine(
                tileEntityRegistrar: Supplier<TileEntityTypeRegistryObject<TILE>>,
                description: ILangEntry
            ): ExpandedMachineBuilder<ExpandedMachine<TILE>, TILE, *> {
                return ExpandedMachineBuilder(ExpandedMachine(tileEntityRegistrar, description))
            }
        }
    }
}*/