package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ExpandedMekanism.MOD_ID)

     val PETROLEUM_COKE: RegistryObject<Item?>? = ITEMS.register("petroleum_coke"){
        Item(Item.Properties())
     }

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }
}