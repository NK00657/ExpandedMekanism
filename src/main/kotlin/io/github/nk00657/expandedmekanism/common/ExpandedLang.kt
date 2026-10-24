package io.github.nk00657.expandedmekanism.common

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import mekanism.api.text.ILangEntry
import net.minecraft.Util

enum class ExpandedLang(
    type: String,
    path: String
) : ILangEntry{
    DESCRIPTION_OIL_PUMPJACK("description", "oil_pumpjack"),
    DESCRIPTION_KEROSENE_HEATER("description", "kerosene_heater"),
    DESCRIPTION_WELL_CASING("description", "well_casing");


    private val key: String = Util.makeDescriptionId(type, ExpandedMekanism.rl(path))
    override fun getTranslationKey(): String = key
}