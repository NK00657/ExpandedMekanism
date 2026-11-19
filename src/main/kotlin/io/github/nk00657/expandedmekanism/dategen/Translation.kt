package io.github.nk00657.expandedmekanism.dategen



/**
* @property ja 日本語の表示名
* @property en 英語の表示名
* @property descJa Shift + N で表示される日本語の説明文（ブロック用・省略可）
* @property descEn Shift + N で表示される英語の説明文（ブロック用・省略可）
* @property bucketJa 流体バケツの日本語名(省略可)
*/

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Translation(
    val ja: String,
    val en: String,
    val descJa: String = "",
    val descEn: String = "",
    val bucketJa: String = ""
)
