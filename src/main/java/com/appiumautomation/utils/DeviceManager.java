package com.appiumautomation.utils;

import com.appiumautomation.models.Device;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceManager {
    private static final Logger logger = LoggerFactory.getLogger(DeviceManager.class);
    private static final String DEVICE_LIST_FILE = "device_list.json";
    private static final int BASE_APPIUM_PORT = 4723;
    private static final int BASE_SYSTEM_PORT = 8200;
    
    private List<Device> connectedDevices;
    private ObjectMapper objectMapper;
    private ExecutorService executorService;

    public DeviceManager() {
        this.connectedDevices = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * ADB ile cihazları bağlar ve device list oluşturur
     */
    public void connectDevices(List<String> deviceIPs) {
        logger.info("Cihaz bağlantıları başlatılıyor...");
        
        for (int i = 0; i < deviceIPs.size(); i++) {
            String deviceIP = deviceIPs.get(i);
            final int deviceIndex = i;
            
            executorService.submit(() -> {
                try {
                    connectDevice(deviceIP, deviceIndex);
                } catch (Exception e) {
                    logger.error("Cihaz bağlantısı başarısız: " + deviceIP, e);
                }
            });
        }
        
        // Tüm bağlantıların tamamlanmasını bekle
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        saveDeviceList();
        logger.info("Toplam {} cihaz bağlandı", connectedDevices.size());
    }

    /**
     * Tek bir cihazı ADB ile bağlar
     */
    private void connectDevice(String deviceIP, int deviceIndex) {
        try {
            logger.info("Cihaz bağlanıyor: {}", deviceIP);
            
            // ADB connect komutu çalıştır
            String adbConnectCommand = "adb connect " + deviceIP + ":5555";
            Process process = Runtime.getRuntime().exec(adbConnectCommand);
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Cihaz başarıyla bağlandı: {}", deviceIP);
                
                // Cihaz bilgilerini al
                Device device = getDeviceInfo(deviceIP, deviceIndex);
                if (device != null) {
                    synchronized (connectedDevices) {
                        connectedDevices.add(device);
                    }
                    logger.info("Cihaz bilgileri alındı: {}", device.getDeviceName());
                }
            } else {
                logger.error("Cihaz bağlantısı başarısız: {}", deviceIP);
            }
            
        } catch (Exception e) {
            logger.error("Cihaz bağlantısı sırasında hata: {}", deviceIP, e);
        }
    }

    /**
     * Cihaz bilgilerini alır
     */
    private Device getDeviceInfo(String deviceIP, int deviceIndex) {
        try {
            // Cihaz ID'sini al
            String deviceId = getDeviceId(deviceIP);
            if (deviceId == null) {
                return null;
            }
            
            // Cihaz adını al
            String deviceName = getDeviceName(deviceIP);
            
            // Platform versiyonunu al
            String platformVersion = getPlatformVersion(deviceIP);
            
            // Port numaralarını hesapla
            int appiumPort = BASE_APPIUM_PORT + deviceIndex;
            int systemPort = BASE_SYSTEM_PORT + deviceIndex;
            
            return new Device(deviceId, deviceName, platformVersion, appiumPort, systemPort, deviceIP);
            
        } catch (Exception e) {
            logger.error("Cihaz bilgileri alınırken hata: {}", deviceIP, e);
            return null;
        }
    }

    /**
     * Cihaz ID'sini alır
     */
    private String getDeviceId(String deviceIP) {
        try {
            String command = "adb -s " + deviceIP + ":5555 shell getprop ro.product.model";
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                String output = new String(process.getInputStream().readAllBytes()).trim();
                return output.isEmpty() ? deviceIP : output;
            }
        } catch (Exception e) {
            logger.error("Cihaz ID alınırken hata: {}", deviceIP, e);
        }
        return deviceIP;
    }

    /**
     * Cihaz adını alır
     */
    private String getDeviceName(String deviceIP) {
        try {
            String command = "adb -s " + deviceIP + ":5555 shell getprop ro.product.name";
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                String output = new String(process.getInputStream().readAllBytes()).trim();
                return output.isEmpty() ? "Unknown Device" : output;
            }
        } catch (Exception e) {
            logger.error("Cihaz adı alınırken hata: {}", deviceIP, e);
        }
        return "Unknown Device";
    }

    /**
     * Platform versiyonunu alır
     */
    private String getPlatformVersion(String deviceIP) {
        try {
            String command = "adb -s " + deviceIP + ":5555 shell getprop ro.build.version.release";
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                String output = new String(process.getInputStream().readAllBytes()).trim();
                return output.isEmpty() ? "Unknown" : output;
            }
        } catch (Exception e) {
            logger.error("Platform versiyonu alınırken hata: {}", deviceIP, e);
        }
        return "Unknown";
    }

    /**
     * Device list'i JSON dosyasına kaydeder
     */
    private void saveDeviceList() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(DEVICE_LIST_FILE), connectedDevices);
            logger.info("Device list kaydedildi: {}", DEVICE_LIST_FILE);
        } catch (IOException e) {
            logger.error("Device list kaydedilirken hata", e);
        }
    }

    /**
     * Device list'i JSON dosyasından okur
     */
    public List<Device> loadDeviceList() {
        try {
            File file = new File(DEVICE_LIST_FILE);
            if (file.exists()) {
                connectedDevices = objectMapper.readValue(file, new TypeReference<List<Device>>() {});
                logger.info("Device list yüklendi: {} cihaz", connectedDevices.size());
                return connectedDevices;
            }
        } catch (IOException e) {
            logger.error("Device list yüklenirken hata", e);
        }
        return new ArrayList<>();
    }

    /**
     * Bağlı cihazları döndürür
     */
    public List<Device> getConnectedDevices() {
        return new ArrayList<>(connectedDevices);
    }

    /**
     * Cihazları temizler
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
