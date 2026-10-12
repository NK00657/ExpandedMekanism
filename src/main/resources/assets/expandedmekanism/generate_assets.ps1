
# ==========================================
# 1. 登録対象の定義テーブル
# ==========================================
$fluids = @(
    @{ id = "crude_oil";  ja = "原油";         en = "Crude Oil" },
    @{ id = "asphalt";    ja = "アスファルト"; en = "Asphalt" },
    @{ id = "heavy_oil";  ja = "重油";         en = "Heavy Oil" },
    @{ id = "light_oil";  ja = "軽油";         en = "Light Oil" },
    @{ id = "naphtha";    ja = "ナフサ";       en = "Naphtha" },
    @{ id = "kerosene";   ja = "灯油";         en = "Kerosene" },
    @{ id = "gasoline";   ja = "ガソリン";     en = "Gasoline" }
)

$gases = @(
    @{ id = "lpg";        ja = "LPG";          en = "LPG" },
    @{ id = "methane";    ja = "メタン";       en = "Methane" },
    @{ id = "ethane";     ja = "エタン";       en = "Ethane" }
)

# ==========================================
# 2. 既存 JSON を読み込む（なければ新規作成）
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

# クリエイティブタブ共通名
$jaData | Add-Member -NotePropertyName "itemGroup.expandedmekanism.main" -NotePropertyValue "Expanded Mekanism" -Force
$enData | Add-Member -NotePropertyName "itemGroup.expandedmekanism.main" -NotePropertyValue "Expanded Mekanism" -Force

# ==========================================
# 3. 流体用: lang 追記 + バケツモデル JSON 生成
# ==========================================
foreach ($f in $fluids) {
    $id = $f.id

    # ja_jp.json
    $jaData | Add-Member -NotePropertyName "fluid.expandedmekanism.$id" -NotePropertyValue $f.ja -Force
    $jaData | Add-Member -NotePropertyName "item.expandedmekanism.${id}_bucket" -NotePropertyValue "$($f.ja)入りバケツ" -Force

    # en_us.json
    $enData | Add-Member -NotePropertyName "fluid.expandedmekanism.$id" -NotePropertyValue $f.en -Force
    $enData | Add-Member -NotePropertyName "item.expandedmekanism.${id}_bucket" -NotePropertyValue "$($f.en) Bucket" -Force

    # バケツモデル JSON 出力 (1.20.1 Forge 用)
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
# 4. ガス用: lang 追記
# ==========================================
foreach ($g in $gases) {
    $id = $g.id

    # ja_jp.json
    $jaData | Add-Member -NotePropertyName "gas.expandedmekanism.$id" -NotePropertyValue $g.ja -Force

    # en_us.json
    $enData | Add-Member -NotePropertyName "gas.expandedmekanism.$id" -NotePropertyValue $g.en -Force
}

# ==========================================
# 5. 言語ファイルを書き出し
# ==========================================
$jaData | ConvertTo-Json -Depth 10 | Set-Content -Path $jaJsonPath -Encoding UTF8
$enData | ConvertTo-Json -Depth 10 | Set-Content -Path $enJsonPath -Encoding UTF8

Write-Host "JSON assets successfully generated and synced!" -ForegroundColor Green
#by-GEMINI