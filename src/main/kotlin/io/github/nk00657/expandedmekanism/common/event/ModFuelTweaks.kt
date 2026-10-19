package io.github.nk00657.expandedmekanism.common.event

import com.mojang.logging.LogUtils
import mekanism.api.chemical.Chemical
import mekanism.api.chemical.gas.attribute.GasAttributes
import mekanism.api.math.FloatingLong
import mekanism.common.registries.MekanismGases
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import java.lang.reflect.Field

object ModFuelTweaks {
    private val LOGGER = LogUtils.getLogger()

    fun onCompleteSetup(event: FMLLoadCompleteEvent) {
        event.enqueueWork {
            nerfEthylene()
        }
    }

    internal fun nerfEthylene() {
        try {
            val ethylene = MekanismGases.ETHENE.get()

            // ナーフ値: 燃焼時間 5t, エネルギー密度 4,000 J (約 1.6 kFE/mB)
            // 5: 燃焼ブースト倍率を抑制する目標値 (40 -> 5 tick)
            val nerfedFuel = GasAttributes.Fuel(
                5,
                FloatingLong.create(4000.0)
            )

            val field: Field = Chemical::class.java.getDeclaredField("attributeMap").apply {
                isAccessible = true
            }

            @Suppress("UNCHECKED_CAST")
            val attributeMap = field.get(ethylene) as? MutableMap<Class<*>, Any>

            if (attributeMap != null) {
                attributeMap[GasAttributes.Fuel::class.java] = nerfedFuel

                // 検証用ログ: 実際に書き換わった値を取り出して確認
                val currentFuel = ethylene.get(GasAttributes.Fuel::class.java)
                LOGGER.info(
                    "Ethylene nerfed successfully! BurnTicks: {}, EnergyDensity: {}",
                    currentFuel?.burnTicks,
                    currentFuel?.energyPerTick
                )
            } else {
                LOGGER.warn("attributeMap was null for Ethylene!")
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to nerf Ethylene fuel attribute!", e)
        }
    }
}