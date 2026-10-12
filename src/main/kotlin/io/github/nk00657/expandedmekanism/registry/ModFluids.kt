package io.github.nk00657.expandedmekanism.registry

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import mekanism.common.registration.impl.FluidDeferredRegister
import net.minecraft.sounds.SoundEvents
import net.minecraftforge.common.SoundActions
import net.minecraftforge.eventbus.api.IEventBus

object ModFluids {
    val FLUIDS: FluidDeferredRegister = FluidDeferredRegister(ExpandedMekanism.MOD_ID)

    val CRUDE_OIL = FLUIDS.register(
        "crude_oil",
        { properties ->
            properties
                .density(800)
                .viscosity(1000)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFF1E1E1E.toInt())
        }
    )

    val ASPHALT = FLUIDS.register(
        "asphalt",
        { properties ->
            properties
                .density(1200)
                .viscosity(1500)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFF2B2B2B.toInt())
        }
    )

    val HEAVY_OIL = FLUIDS.register(
        "heavy_oil",
        { properties ->
            properties
                .density(900)
                .viscosity(1200)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFF3A3226.toInt())
        }
    )

    val LIGHT_OIL = FLUIDS.register(
        "light_oil",
        { properties ->
            properties
                .density(700)
                .viscosity(800)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFFE0C759.toInt())
        }
    )

    val NAPHTHA = FLUIDS.register(
        "naphtha",
        { properties ->
            properties
                .density(600)
                .viscosity(700)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFFD4D8B8.toInt())
        }
    )

    val KEROSENE = FLUIDS.register(
        "kerosene",
        { properties ->
            properties
                .density(650)
                .viscosity(750)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFFC7E2E8.toInt())
        }
    )

    val GASOLINE = FLUIDS.register(
        "gasoline",
        { properties ->
            properties
                .density(550)
                .viscosity(600)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        },
        { renderProperties ->
            renderProperties.tint(0xFFF0E050.toInt())
        }
    )

    fun register(bus: IEventBus) {
        FLUIDS.register(bus)
    }
}