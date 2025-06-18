$densities = @("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
$iconContent = @"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
"@

foreach ($density in $densities) {
    $iconPath = "app/src/main/res/mipmap-$density/ic_launcher.xml"
    $roundIconPath = "app/src/main/res/mipmap-$density/ic_launcher_round.xml"
    
    Set-Content -Path $iconPath -Value $iconContent
    Set-Content -Path $roundIconPath -Value $iconContent
} 