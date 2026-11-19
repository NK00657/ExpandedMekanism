package io.github.nk00657.expandedmekanism.dategen

import io.github.nk00657.expandedmekanism.ExpandedMekanism
import io.github.nk00657.expandedmekanism.registry.ModBlocks
import io.github.nk00657.expandedmekanism.registry.ModFluids
import io.github.nk00657.expandedmekanism.registry.ModGases
import io.github.nk00657.expandedmekanism.registry.ModItems
import mekanism.common.registration.impl.GasRegistryObject
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.registries.ForgeRegistries

/**
 * 各Registryオブジェクトのフィールドに付与された @Translation アノテーションを読み取り、
 * en_us.json と ja_jp.json をそれぞれの言語で自動生成するプロバイダクラス。
 */
class ModLanguageProvider(
    output: PackOutput,
    locale: String
) : LanguageProvider(output, ExpandedMekanism.MOD_ID, locale) {

    // 出力対象が日本語（ja_jp）かどうかを判定するフラグ
    private val isJapanese: Boolean = locale == "ja_jp"

    override fun addTranslations() {
        // 【追加点】各レジストリクラスから、登録オブジェクトのインスタンスと @Translation の対応マップを収集
        val blockAnnotations = collectAnnotations(ModBlocks::class.java)
        val itemAnnotations = collectAnnotations(ModItems::class.java)
        val fluidAnnotations = collectAnnotations(ModFluids::class.java)
        val gasAnnotations = collectAnnotations(ModGases::class.java)

        // 1. クリエイティブタブの翻訳キー登録
        add("itemGroup.${ExpandedMekanism.MOD_ID}", "Expanded Mekanism")
        add("itemGroup.${ExpandedMekanism.MOD_ID}.main", "Expanded Mekanism")

        // 2. ブロックと説明文（Shift + N）の翻訳生成
        ModBlocks.BLOCKS.allBlocks.forEach { blockProvider ->
            val block = blockProvider.block
            val path = ForgeRegistries.BLOCKS.getKey(block)?.path ?: return@forEach
            val defaultEn = toTitleCase(path)
            val annotation = blockAnnotations[blockProvider]

            // アノテーションがあればその言語の文字列を使い、無ければ自動英語名にフォールバック
            val blockName = if (isJapanese) {
                annotation?.ja?.ifEmpty { defaultEn } ?: defaultEn
            } else {
                annotation?.en?.ifEmpty { defaultEn } ?: defaultEn
            }
            add(block, blockName)

            // 説明文（description.expandedmekanism.xxx）の生成
            val defaultDescEn = "Description for $defaultEn"
            val descText = if (isJapanese) {
                annotation?.descJa?.ifEmpty { defaultDescEn } ?: defaultDescEn
            } else {
                annotation?.descEn?.ifEmpty { defaultDescEn } ?: defaultDescEn
            }
            add("description.${ExpandedMekanism.MOD_ID}.$path", descText)
        }

        // 3. 通常アイテム（石油コークス等）の翻訳生成
        ModItems.ITEMS.entries.forEach { itemRO ->
            val item = itemRO.get()
            val defaultEn = toTitleCase(itemRO.id.path)
            val annotation = itemAnnotations[itemRO]

            val itemName = if (isJapanese) {
                annotation?.ja?.ifEmpty { defaultEn } ?: defaultEn
            } else {
                annotation?.en?.ifEmpty { defaultEn } ?: defaultEn
            }
            add(item, itemName)
        }

        // 4. 流体・液体ブロック・バケツの翻訳生成
        ModFluids.FLUIDS.allFluids.forEach { fluidRO ->
            val fluidBlock = fluidRO.block
            val path = ForgeRegistries.BLOCKS.getKey(fluidBlock)?.path ?: return@forEach
            val defaultEn = toTitleCase(path)
            val annotation = fluidAnnotations[fluidRO]

            val fluidName = if (isJapanese) {
                annotation?.ja?.ifEmpty { defaultEn } ?: defaultEn
            } else {
                annotation?.en?.ifEmpty { defaultEn } ?: defaultEn
            }

            // バケツ名（日本語でbucketJaが未指定の場合は「{流体名}入りバケツ」を自動生成）
            val bucketName = if (isJapanese) {
                when {
                    annotation == null -> "$defaultEn Bucket"
                    annotation.bucketJa.isNotEmpty() -> annotation.bucketJa
                    else -> "${annotation.ja}入りバケツ"
                }
            } else {
                "${annotation?.en?.ifEmpty { defaultEn } ?: defaultEn} Bucket"
            }

            add(fluidBlock, fluidName)
            add("fluid.${ExpandedMekanism.MOD_ID}.$path", fluidName)
            add(fluidRO.bucket, bucketName)
        }

        // 5. ガスの翻訳生成
        ModGases::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            // staticフィールドからインスタンスを取得し、GasRegistryObject型であれば処理を進める
            val gasRO = field.get(null) as? GasRegistryObject<*> ?: return@forEach
            val gas = gasRO.get()

            val path = gas.registryName.path
            val defaultEn = toTitleCase(path)
            // アノテーション収集マップから該当ガスの @Translation 情報を取得
            val annotation = gasAnnotations[gasRO]

            val gasName = if (isJapanese) {
                annotation?.ja?.ifEmpty { defaultEn } ?: defaultEn
            } else {
                annotation?.en?.ifEmpty { defaultEn } ?: defaultEn
            }
            // Mekanismのガス用翻訳キー（gas.expandedmekanism.xxx）で登録
            add(gas.translationKey, gasName)
        }
    }


    /**
     * 指定されたオブジェクトクラス（ModBlocks等）の全フィールドをリフレクションで走査し、
     * 「フィールドが保持しているRegistryObjectインスタンス」をキー、「@Translation」を値とするマップを返す関数。
     */
    private fun collectAnnotations(registryClass: Class<*>): Map<Any, Translation> {
        val map = mutableMapOf<Any, Translation>()
        for (field in registryClass.declaredFields) {
            val annotation = field.getAnnotation(Translation::class.java) ?: continue
            field.isAccessible = true
            // Kotlinの object のフィールドは static なので get(null) でインスタンスを取得できる
            val instance = field.get(null) ?: continue
            map[instance] = annotation
        }
        return map
    }

    /**
     * スネークケースの文字列（例: "crude_oil"）をスペース区切りのTitle Case（例: "Crude Oil"）に変換する関数。
     */
    private fun toTitleCase(registryPath: String): String {
        return registryPath.split("_")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { char -> char.uppercaseChar() }
            }
    }
}
//by-GEMINI