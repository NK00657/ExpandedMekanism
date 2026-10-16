[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# ==========================================
# 1. 登録対象の定義テーブル
# ==========================================

# 通常アイテム定義（触媒、中間素材など）
$items = @(

)

# ブロック定義
$blocks = @(
    @{ id = "well_casing";   ja = "油井ケーシング管"; en = "Well Casing" },
    @{ id = "oil_pumpjack";  ja = "油井ポンプ";       en = "Oil Pumpjack" }
)

# 流体定義
$fluids = @(
    @{ id = "crude_oil";  ja = "原油";         en = "Crude Oil" },
    @{ id = "asphalt";    ja = "アスファルト"; en = "Asphalt" },
    @{ id = "heavy_oil";  ja = "重油";         en = "Heavy Oil" },
    @{ id = "light_oil";  ja = "軽油";         en = "Light Oil" },
    @{ id = "naphtha";    ja = "ナフサ";       en = "Naphtha" },
    @{ id = "kerosene";   ja = "灯油";         en = "Kerosene" },
    @{ id = "gasoline";   ja = "ガソリン";     en = "Gasoline" }
)

# ガス定義
$gases = @(
    @{ id = "lpg";        ja = "LPG";          en = "LPG" },
    @{ id = "methane";    ja = "メタン";       en = "Methane" },
    @{ id = "ethane";     ja = "エタン";       en = "Ethane" }
)

# ==========================================
# 2. 必要なフォルダの自動作成
# ==========================================
$dirs = @("blockstates", "models\block", "models\item", "lang")
foreach ($d in $dirs) {
    if (-not (Test-Path $d)) {
        New-Item -ItemType Directory -Path $d | Out-Null
    }
}

# ==========================================
# 3. 既存 JSON を読み込む
# ==========================================
function Get-JsonObject($path) {
    if (Test-Path $path) {
        return Get-Content -Raw -Encoding UTF8 $path | ConvertFrom-Json
    }
    return [PSCustomObject]@{}
}

$jaJsonPath = "lang\ja_jp.json"
$enJsonPath = "lang\en_us.json"

$jaData = Get-JsonObject $jaJsonPath
$enData = Get-JsonObject $enJsonPath

$jaData | Add-Member -NotePropertyName "itemGroup.expandedmekanism.main" -NotePropertyValue "Expanded Mekanism" -Force
$enData | Add-Member -NotePropertyName "itemGroup.expandedmekanism.main" -NotePropertyValue "Expanded Mekanism" -Force

# ==========================================
# 4. 通常アイテム用アセット生成
# ==========================================
foreach ($item in $items) {
    $id = $item.id

    # lang 追記
    $jaData | Add-Member -NotePropertyName "item.expandedmekanism.$id" -NotePropertyValue $item.ja -Force
    $enData | Add-Member -NotePropertyName "item.expandedmekanism.$id" -NotePropertyValue $item.en -Force

    # models/item/<id>.json（標準の平面アイテムモデル）
    $itemModel = @"
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "expandedmekanism:item/$id"
  }
}
"@
    Set-Content -Path "models\item\$id.json" -Value $itemModel -Encoding UTF8
}

# ==========================================
# 5. ブロック用アセット生成
# ==========================================
foreach ($b in $blocks) {
    $id = $b.id

    $jaData | Add-Member -NotePropertyName "block.expandedmekanism.$id" -NotePropertyValue $b.ja -Force
    $enData | Add-Member -NotePropertyName "block.expandedmekanism.$id" -NotePropertyValue $b.en -Force

    # blockstate
    $blockstateJson = @"
{
  "variants": {
    "": {
      "model": "expandedmekanism:block/$id"
    }
  }
}
"@
    Set-Content -Path "blockstates\$id.json" -Value $blockstateJson -Encoding UTF8

    # models/block
    $blockModelJson = @"
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "expandedmekanism:block/$id"
  }
}
"@
    Set-Content -Path "models\block\$id.json" -Value $blockModelJson -Encoding UTF8

    # models/item（ブロック用）
    $itemBlockModelJson = @"
{
  "parent": "expandedmekanism:block/$id"
}
"@
    Set-Content -Path "models\item\$id.json" -Value $itemBlockModelJson -Encoding UTF8
}

# ==========================================
# 6. 流体用アセット生成
# ==========================================
foreach ($f in $fluids) {
    $id = $f.id

    $jaData | Add-Member -NotePropertyName "fluid.expandedmekanism.$id" -NotePropertyValue $f.ja -Force
    $jaData | Add-Member -NotePropertyName "item.expandedmekanism.${id}_bucket" -NotePropertyValue "$($f.ja)入りバケツ" -Force

    $enData | Add-Member -NotePropertyName "fluid.expandedmekanism.$id" -NotePropertyValue $f.en -Force
    $enData | Add-Member -NotePropertyName "item.expandedmekanism.${id}_bucket" -NotePropertyValue "$($f.en) Bucket" -Force

    $bucketModel = @"
{
  "parent": "forge:item/bucket",
  "loader": "forge:fluid_container",
  "fluid": "expandedmekanism:$id"
}
"@
    Set-Content -Path "models\item\${id}_bucket.json" -Value $bucketModel -Encoding UTF8
}

# ==========================================
# 7. ガス用アセット生成
# ==========================================
foreach ($g in $gases) {
    $id = $g.id
    $jaData | Add-Member -NotePropertyName "gas.expandedmekanism.$id" -NotePropertyValue $g.ja -Force
    $enData | Add-Member -NotePropertyName "gas.expandedmekanism.$id" -NotePropertyValue $g.en -Force
}

# ==========================================
# 8. 言語ファイルの保存
# ==========================================
$jaData | ConvertTo-Json -Depth 10 | Set-Content -Path $jaJsonPath -Encoding UTF8
$enData | ConvertTo-Json -Depth 10 | Set-Content -Path $enJsonPath -Encoding UTF8

Write-Host "All assets (Items, Blocks, Fluids, Gases, Langs) generated successfully!" -ForegroundColor Green
#by-GEMINI