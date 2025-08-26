#!/bin/bash

# AVD oluşturma script'i
# Bu script Android Virtual Device'ları oluşturur

echo "=== Android Virtual Device Oluşturma Script'i ==="

# Android SDK path'ini kontrol et
if [ -z "$ANDROID_HOME" ]; then
    echo "ANDROID_HOME environment variable'ı ayarlanmamış!"
    echo "Android Studio'yu kurun ve ANDROID_HOME'u ayarlayın."
    exit 1
fi

# AVD Manager path'ini ayarla
AVD_MANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager"
if [ ! -f "$AVD_MANAGER" ]; then
    AVD_MANAGER="$ANDROID_HOME/tools/bin/avdmanager"
fi

if [ ! -f "$AVD_MANAGER" ]; then
    echo "AVD Manager bulunamadı! Android SDK Command Line Tools kurulu olduğundan emin olun."
    exit 1
fi

# Mevcut AVD'leri listele
echo "Mevcut AVD'ler:"
$AVD_MANAGER list avd

echo ""
echo "Test için AVD oluşturuluyor..."

# Test AVD'lerini oluştur
create_avd() {
    local avd_name=$1
    local device=$2
    local api_level=$3
    
    echo "AVD oluşturuluyor: $avd_name"
    
    # AVD'yi oluştur
    $AVD_MANAGER create avd \
        --name "$avd_name" \
        --device "$device" \
        --package "system-images;android-$api_level;google_apis;x86_64" \
        --force
    
    if [ $? -eq 0 ]; then
        echo "✅ $avd_name başarıyla oluşturuldu"
    else
        echo "❌ $avd_name oluşturulamadı"
    fi
}

# Test için 2 AVD oluştur
create_avd "TestDevice_API30" "pixel_4" "30"
create_avd "TestDevice_API29" "pixel_3" "29"

echo ""
echo "Oluşturulan AVD'ler:"
$AVD_MANAGER list avd

echo ""
echo "=== AVD Oluşturma Tamamlandı ==="
echo "Testleri çalıştırmak için: mvn clean test -Dtest.mode=EMULATORS"

