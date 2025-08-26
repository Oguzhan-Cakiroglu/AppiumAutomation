package com.appiumautomation.core;

import com.appiumautomation.models.Device;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static DriverManager instance;
    private Map<String, AndroidDriver> drivers;
    private Map<String, Device> deviceMap;

    private DriverManager() {
        this.drivers = new HashMap<>();
        this.deviceMap = new HashMap<>();
    }

    public static DriverManager getInstance() {
        if (instance == null) {
            instance = new DriverManager();
        }
        return instance;
    }

    public void initializeDrivers(List<Device> devices) {
        logger.info("Driver'lar başlatılıyor...");
        
        for (Device device : devices) {
            try {
                AndroidDriver driver = createDriver(device);
                if (driver != null) {
                    drivers.put(device.getDeviceId(), driver);
                    deviceMap.put(device.getDeviceId(), device);
                    logger.info("Driver oluşturuldu: {} - {}", device.getDeviceName(), device.getDeviceId());
                }
            } catch (Exception e) {
                logger.error("Driver oluşturulamadı: {}", device.getDeviceId(), e);
            }
        }
        
        logger.info("Toplam {} driver oluşturuldu", drivers.size());
    }

    private AndroidDriver createDriver(Device device) {
        try {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setUdid(device.getUdid())
                    .setPlatformName("Android")
                    .setPlatformVersion(device.getPlatformVersion())
                    .setDeviceName(device.getDeviceName())
                    .setAutomationName("UiAutomator2")
                    .setSystemPort(device.getSystemPort())
                    .setNoReset(true)
                    .setFullReset(false)
                    .setAutoGrantPermissions(true)
                    .setNewCommandTimeout(Duration.ofSeconds(60))
                    .setAppPackage("com.android.settings")
                    .setAppActivity("com.android.settings.Settings");

            String appiumUrl = String.format("http://localhost:%d/wd/hub", device.getAppiumPort());
            URL url = new URL(appiumUrl);
            
            AndroidDriver driver = new AndroidDriver(url, options);
            logger.info("Driver başarıyla oluşturuldu: {} - Port: {}", device.getDeviceName(), device.getAppiumPort());
            
            return driver;
            
        } catch (Exception e) {
            logger.error("Driver oluşturulurken hata: {}", device.getDeviceId(), e);
            return null;
        }
    }

    public AndroidDriver getDriver(String deviceId) {
        return drivers.get(deviceId);
    }

    public Map<String, AndroidDriver> getAllDrivers() {
        return new HashMap<>(drivers);
    }

    public Device getDevice(String deviceId) {
        return deviceMap.get(deviceId);
    }

    public Map<String, Device> getAllDevices() {
        return new HashMap<>(deviceMap);
    }

    public void quitDriver(String deviceId) {
        AndroidDriver driver = drivers.get(deviceId);
        if (driver != null) {
            try {
                driver.quit();
                drivers.remove(deviceId);
                deviceMap.remove(deviceId);
                logger.info("Driver kapatıldı: {}", deviceId);
            } catch (Exception e) {
                logger.error("Driver kapatılırken hata: {}", deviceId, e);
            }
        }
    }

    public void quitAllDrivers() {
        logger.info("Tüm driver'lar kapatılıyor...");
        
        for (Map.Entry<String, AndroidDriver> entry : drivers.entrySet()) {
            try {
                AndroidDriver driver = entry.getValue();
                if (driver != null) {
                    driver.quit();
                    logger.info("Driver kapatıldı: {}", entry.getKey());
                }
            } catch (Exception e) {
                logger.error("Driver kapatılırken hata: {}", entry.getKey(), e);
            }
        }
        
        drivers.clear();
        deviceMap.clear();
        logger.info("Tüm driver'lar kapatıldı");
    }

    public boolean isDriverActive(String deviceId) {
        AndroidDriver driver = drivers.get(deviceId);
        if (driver != null) {
            try {
                driver.getSessionId();
                return true;
            } catch (Exception e) {
                logger.warn("Driver aktif değil: {}", deviceId);
                drivers.remove(deviceId);
                deviceMap.remove(deviceId);
                return false;
            }
        }
        return false;
    }

    public void cleanup() {
        quitAllDrivers();
    }
}

