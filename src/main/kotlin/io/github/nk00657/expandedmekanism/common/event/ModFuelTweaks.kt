package io.github.nk00657.expandedmekanism.common.event

//import io.github.nk00657.expandedmekanism.ExpandedMekanism
import mekanism.api.chemical.Chemical
import com.mojang.logging.LogUtils
import mekanism.api.chemical.gas.attribute.GasAttributes
import mekanism.api.math.FloatingLong
import mekanism.common.registries.MekanismGases
//import net.minecraftforge.eventbus.api.SubscribeEvent
//import net.minecraftforge.fml.common.Mod
//import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.lang.reflect.Field

//@Mod.EventBusSubscriber(modid = ExpandedMekanism.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModFuelTweaks {

    //@SubscribeEvent
    //fun onCommonSetup(event: FMLCommonSetupEvent) {
    //    event.enqueueWork {
    //        nerfEthylene()
    //    }
    //}
    private val LOGGER = LogUtils.getLogger()

     fun nerfEthylene() {
        val ethylene = MekanismGases.ETHENE.get()

        val nerfedFuel = GasAttributes.Fuel(
            2,
            FloatingLong.create(4000.0)
        )

        try {
            // Chemical クラスの attributeMap フィールドにアクセス
            val field: Field = Chemical::class.java.getDeclaredField("attributeMap").apply {
                isAccessible = true
            }

            @Suppress("UNCHECKED_CAST")
            val attributeMap = field.get(ethylene) as? MutableMap<Class<*>, Any>

            if (attributeMap != null) {
                attributeMap[GasAttributes.Fuel::class.java] = nerfedFuel
                LOGGER.info("Successfully nerfed Ethylene fuel attribute")
            } else {
                LOGGER.error("Failed to nerf Ethylene fuel attribute: attributeMap is null")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to nerf Ethylene fuel attribute!", e)
        }
    }
}