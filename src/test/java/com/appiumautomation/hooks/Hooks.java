package com.appiumautomation.hooks;

import com.appiumautomation.core.DriverManager;
import com.appiumautomation.utils.AppiumServerManager;
import com.appiumautomation.utils.DeviceManager;
import com.appiumautomation.utils.EmulatorManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.appiumautomation.models.Device;

public class Hooks {
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    private static DeviceManager deviceManager;
    private static EmulatorManager emulatorManager;
    private static AppiumServerManager appiumServerManager;
    private static DriverManager driverManager;
    private static boolean isInitialized = false;
    
    // Test modu: "REAL_DEVICES" veya "EMULATORS"
    private static final String TEST_MODE = System.getProperty("test.mode", "EMULATORS");

    @Before
    public void setUp(Scenario scenario) {
        logger.info("Test başlatılıyor: {}", scenario.getName());
        
        if (!isInitialized) {
            initializeTestEnvironment();
            isInitialized = true;
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        logger.info("Test tamamlandı: {} - Durum: {}", scenario.getName(), scenario.getStatus());
        
        // Test sonrası temizlik işlemleri
        if (scenario.isFailed()) {
            logger.error("Test başarısız: {}", scenario.getName());
            // Başarısız test durumunda ekran görüntüsü alınabilir
        }
    }

    /**
     * Test ortamını başlatır
     */
    private void initializeTestEnvironment() {
        try {
            logger.info("Test ortamı başlatılıyor... Test Modu: {}", TEST_MODE);
            
            List<com.appiumautomation.models.Device> connectedDevices;
            
            if ("EMULATORS".equals(TEST_MODE)) {
                // Emulator modu
                connectedDevices = initializeEmulators();
            } else {
                // Gerçek cihaz modu
                connectedDevices = initializeRealDevices();
            }
            
            if (connectedDevices.isEmpty()) {
                logger.error("Hiçbir cihaz başlatılamadı!");
                throw new RuntimeException("Cihaz başlatma başarısız");
            }
            
            // Appium sunucularını başlat
            appiumServerManager = new AppiumServerManager();
            appiumServerManager.startAppiumServers(connectedDevices);
            
            // Driver'ları başlat
            driverManager = DriverManager.getInstance();
            driverManager.initializeDrivers(connectedDevices);
            
            logger.info("Test ortamı başarıyla başlatıldı - {} cihaz hazır", connectedDevices.size());
            
        } catch (Exception e) {
            logger.error("Test ortamı başlatılırken hata", e);
            throw new RuntimeException("Test ortamı başlatılamadı", e);
        }
    }
    
    /**
     * Emulator'ları başlatır
     */
    private List<com.appiumautomation.models.Device> initializeEmulators() {
        logger.info("Emulator modu başlatılıyor...");
        
        emulatorManager = new EmulatorManager();
        
        // Mevcut emulator'ları kontrol et
        try {
            List<String> availableAVDs = emulatorManager.listAvailableAVDs();
            if (availableAVDs.isEmpty()) {
                logger.warn("AVD listesi alınamadı, mevcut emulator'ları kullanıyoruz...");
                // Mevcut emulator'ları kullan
                return createDevicesFromExistingEmulators();
            }
            
            // Test için kullanılacak AVD'leri seç (maksimum 2 tane)
            List<String> testAVDs = availableAVDs.subList(0, Math.min(2, availableAVDs.size()));
            logger.info("Test için seçilen AVD'ler: {}", testAVDs);
            
            // Emulator'ları başlat
            return emulatorManager.startEmulators(testAVDs);
            
        } catch (Exception e) {
            logger.warn("AVD listesi alınamadı, mevcut emulator'ları kullanıyoruz...");
            return createDevicesFromExistingEmulators();
        }
    }
    
    /**
     * Mevcut emulator'lardan Device listesi oluşturur
     */
    private List<com.appiumautomation.models.Device> createDevicesFromExistingEmulators() {
        logger.info("Mevcut emulator'lardan Device listesi oluşturuluyor...");
        
        List<com.appiumautomation.models.Device> devices = new ArrayList<>();
        
        // Mevcut emulator'lar için Device objeleri oluştur
        Device device1 = new Device();
        device1.setDeviceId("device1");
        device1.setDeviceName("Emulator-5554");
        device1.setUdid("emulator-5554");
        device1.setPlatformVersion("11.0");
        device1.setAppiumPort(4723);
        device1.setSystemPort(8200);
        
        Device device2 = new Device();
        device2.setDeviceId("device2");
        device2.setDeviceName("Emulator-5556");
        device2.setUdid("emulator-5556");
        device2.setPlatformVersion("11.0");
        device2.setAppiumPort(4724);
        device2.setSystemPort(8201);
        
        devices.add(device1);
        devices.add(device2);
        
        logger.info("{} emulator için Device objeleri oluşturuldu", devices.size());
        return devices;
    }
    
    /**
     * Gerçek cihazları başlatır
     */
    private List<com.appiumautomation.models.Device> initializeRealDevices() {
        logger.info("Gerçek cihaz modu başlatılıyor...");
        
        deviceManager = new DeviceManager();
        
        // Test cihazlarının IP adreslerini tanımla
        // Bu IP adreslerini kendi cihazlarınızın IP adresleri ile değiştirin
        List<String> deviceIPs = Arrays.asList(
            "192.168.1.100",  // Cihaz 1 IP adresi
            "192.168.1.101"   // Cihaz 2 IP adresi
        );
        
        // Cihazları bağla
        deviceManager.connectDevices(deviceIPs);
        
        return deviceManager.getConnectedDevices();
    }

    /**
     * Test ortamını temizler
     */
    public static void cleanupTestEnvironment() {
        logger.info("Test ortamı temizleniyor...");
        
        try {
            if (driverManager != null) {
                driverManager.cleanup();
            }
            
            if (appiumServerManager != null) {
                appiumServerManager.cleanup();
            }
            
            if (deviceManager != null) {
                deviceManager.cleanup();
            }
            
            // Emulator'ları kapatma - mevcut emulator'lar açık kalsın
            // if (emulatorManager != null) {
            //     emulatorManager.cleanup();
            // }
            
            logger.info("Test ortamı temizlendi (Emulator'lar açık bırakıldı)");
            
        } catch (Exception e) {
            logger.error("Test ortamı temizlenirken hata", e);
        }
    }

    /**
     * Device Manager'ı döndürür
     */
    public static DeviceManager getDeviceManager() {
        return deviceManager;
    }

    /**
     * Appium Server Manager'ı döndürür
     */
    public static AppiumServerManager getAppiumServerManager() {
        return appiumServerManager;
    }

    /**
     * Driver Manager'ı döndürür
     */
    public static DriverManager getDriverManager() {
        return driverManager;
    }
}
