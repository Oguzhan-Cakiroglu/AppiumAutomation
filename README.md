# Appium Multi Device Automation

Bu proje, Android cihazlarda çoklu cihaz desteği ile Appium otomasyon testleri çalıştırmak için geliştirilmiştir.

## Özellikler

- ✅ Android çoklu gerçek cihaz desteği
- ✅ Android emulator (sanal cihaz) desteği
- ✅ ADB connect ile otomatik cihaz bağlantısı
- ✅ Otomatik emulator başlatma
- ✅ Device list JSON dosyasına kaydetme
- ✅ Her cihaz için ayrı Appium portu
- ✅ Cihazları otomatik ayaklandırma
- ✅ Java + Appium + Cucumber + TestNG
- ✅ TestNG Runner ile test başlatma
- ✅ Test modu seçimi (gerçek cihaz / emulator)

## Gereksinimler

### Sistem Gereksinimleri
- Java 11 veya üzeri
- Maven 3.6+
- Node.js 14+ (Appium için)
- Android SDK
- ADB (Android Debug Bridge)
- Android Studio (emulator için)
- Android SDK Command Line Tools

### Kurulum

1. **Appium Kurulumu:**
```bash
npm install -g appium
```

2. **Android SDK Kurulumu:**
   - Android Studio'yu indirin ve kurun
   - ANDROID_HOME environment variable'ını ayarlayın
   - PATH'e platform-tools ekleyin

3. **Proje Bağımlılıklarını İndirin:**
```bash
mvn clean install
```

4. **AVD Oluşturun (Emulator için):**
```bash
./scripts/create_avd.sh
```

## Konfigürasyon

### Test Modu Seçimi

Proje iki farklı modda çalışabilir:

1. **Emulator Modu (Varsayılan):** Sanal Android cihazları kullanır
2. **Gerçek Cihaz Modu:** Fiziksel Android cihazları kullanır

Test modunu değiştirmek için:

```bash
# Emulator modu (varsayılan)
mvn clean test -Dtest.mode=EMULATORS

# Gerçek cihaz modu
mvn clean test -Dtest.mode=REAL_DEVICES
```

### Cihaz IP Adreslerini Ayarlama (Gerçek Cihaz Modu)

`src/test/java/com/appiumautomation/hooks/Hooks.java` dosyasında cihaz IP adreslerini güncelleyin:

```java
List<String> deviceIPs = Arrays.asList(
    "192.168.1.100",  // Cihaz 1 IP adresi
    "192.168.1.101"   // Cihaz 2 IP adresi
);
```

### Emulator Hazırlama (Emulator Modu)

1. **Android Studio'yu Kurun:**
   - Android Studio'yu indirin ve kurun
   - Android SDK'yı kurun

2. **AVD Oluşturun:**
   ```bash
   ./scripts/create_avd.sh
   ```

3. **AVD'leri Kontrol Edin:**
   ```bash
   avdmanager list avd
   ```

### Gerçek Cihazları Hazırlama (Gerçek Cihaz Modu)

1. **Cihazlarda USB Debugging'i Aktifleştirin:**
   - Ayarlar > Geliştirici Seçenekleri > USB Debugging
   - Ayarlar > Geliştirici Seçenekleri > USB Debugging (Security Settings)

2. **Cihazları Ağa Bağlayın:**
   - Cihazları aynı Wi-Fi ağına bağlayın
   - IP adreslerini not edin

3. **ADB Bağlantısını Test Edin:**
   ```bash
   adb connect 192.168.1.100:5555
   adb devices
   ```

## Kullanım

### Testleri Çalıştırma

1. **Emulator Modunda Testleri Çalıştırma (Varsayılan):**
```bash
mvn clean test -Dtest.mode=EMULATORS
```

2. **Gerçek Cihaz Modunda Testleri Çalıştırma:**
```bash
mvn clean test -Dtest.mode=REAL_DEVICES
```

3. **Smoke Testleri Çalıştırma:**
```bash
mvn clean test -Dtest.mode=EMULATORS -Dgroups=smoke
```

4. **Belirli Test Suite'ini Çalıştırma:**
```bash
mvn clean test -Dtest.mode=EMULATORS -DsuiteXmlFile=testng.xml
```

5. **TestNG ile Doğrudan Çalıştırma:**
```bash
mvn clean test -Dtest.mode=EMULATORS -Dtest=CucumberTestRunner
```

### Test Raporları

Test tamamlandıktan sonra raporlar şu konumlarda bulunabilir:

- **Cucumber HTML Raporu:** `target/cucumber-reports/cucumber-pretty.html`
- **JSON Raporu:** `target/cucumber-reports/CucumberTestReport.json`
- **JUnit Raporu:** `target/cucumber-reports/CucumberTestReport.xml`
- **TestNG Raporu:** `target/surefire-reports/`

## Proje Yapısı

```
src/
├── main/java/com/appiumautomation/
│   ├── core/
│   │   └── DriverManager.java          # Appium driver yönetimi
│   ├── models/
│   │   └── Device.java                 # Cihaz model sınıfı
│   ├── utils/
│   │   ├── DeviceManager.java          # Cihaz bağlantı yönetimi
│   │   └── AppiumServerManager.java    # Appium sunucu yönetimi
│   └── resources/
│       └── logback.xml                 # Logging konfigürasyonu
└── test/java/com/appiumautomation/
    ├── hooks/
    │   └── Hooks.java                  # Cucumber hooks
    ├── steps/
    │   └── CommonSteps.java            # Test adımları
    ├── testrunners/
    │   └── CucumberTestRunner.java     # Test runner
    └── resources/features/
        └── settings_test.feature       # Cucumber feature dosyası
```

## Özellik Detayları

### 1. Cihaz Bağlantısı
- ADB connect komutu ile cihazlara bağlanır
- Cihaz bilgilerini otomatik olarak alır
- Bağlantı durumunu kontrol eder

### 2. Appium Sunucu Yönetimi
- Her cihaz için ayrı port açar (4723, 4724, ...)
- Sunucuları paralel olarak başlatır
- Sunucu durumunu kontrol eder

### 3. Driver Yönetimi
- Her cihaz için ayrı AndroidDriver oluşturur
- Driver yaşam döngüsünü yönetir
- Hata durumlarını handle eder

### 4. Test Yapısı
- Cucumber BDD yaklaşımı
- Türkçe test senaryoları
- TestNG ile paralel çalıştırma
- Detaylı raporlama

## Sorun Giderme

### Yaygın Sorunlar

1. **Cihaz Bağlantı Sorunu:**
   - Cihazların aynı ağda olduğundan emin olun
   - USB Debugging'in aktif olduğunu kontrol edin
   - IP adreslerinin doğru olduğunu kontrol edin

2. **Appium Sunucu Sorunu:**
   - Portların kullanılabilir olduğunu kontrol edin
   - Appium'un global olarak kurulu olduğunu kontrol edin
   - Node.js versiyonunu kontrol edin

3. **Driver Sorunu:**
   - Cihazların açık ve kilitli olmadığından emin olun
   - Uygulama paket adının doğru olduğunu kontrol edin
   - Android SDK versiyonunu kontrol edin

### Log Dosyaları

- **Uygulama Logları:** `logs/appium-automation.log`
- **Appium Logları:** `appium_[deviceId].log`

## Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add some amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## İletişim

Sorularınız için issue açabilir veya pull request gönderebilirsiniz.
# AppiumAutomation
