package com.appiumautomation.utils;

import com.appiumautomation.models.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppiumServerManager {
    private static final Logger logger = LoggerFactory.getLogger(AppiumServerManager.class);
    private List<Process> appiumProcesses;
    private ExecutorService executorService;

    public AppiumServerManager() {
        this.appiumProcesses = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Tüm cihazlar için Appium sunucularını başlatır
     */
    public void startAppiumServers(List<Device> devices) {
        logger.info("Appium sunucuları başlatılıyor...");
        
        for (Device device : devices) {
            executorService.submit(() -> {
                try {
                    startAppiumServer(device);
                } catch (Exception e) {
                    logger.error("Appium sunucusu başlatılamadı: {}", device.getDeviceId(), e);
                }
            });
        }
        
        // Sunucuların başlaması için bekle
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("{} Appium sunucusu başlatıldı", devices.size());
    }

    /**
     * Tek bir cihaz için Appium sunucusu başlatır
     */
    private void startAppiumServer(Device device) {
        try {
            logger.info("Appium sunucusu başlatılıyor - Port: {}, Cihaz: {}", 
                       device.getAppiumPort(), device.getDeviceName());
            
            // Appium sunucusu başlatma komutu
            String appiumCommand = String.format(
                "appium --port %d --base-path /wd/hub --log appium_%s.log",
                device.getAppiumPort(), device.getDeviceId()
            );
            
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", appiumCommand);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            appiumProcesses.add(process);
            
            // Sunucunun başlamasını bekle
            Thread.sleep(5000);
            
            // Sunucu durumunu kontrol et
            if (process.isAlive()) {
                logger.info("Appium sunucusu başarıyla başlatıldı - Port: {}, Cihaz: {}", 
                           device.getAppiumPort(), device.getDeviceName());
            } else {
                logger.error("Appium sunucusu başlatılamadı - Port: {}, Cihaz: {}", 
                            device.getAppiumPort(), device.getDeviceName());
            }
            
        } catch (Exception e) {
            logger.error("Appium sunucusu başlatılırken hata - Cihaz: {}", device.getDeviceId(), e);
        }
    }

    /**
     * Tüm Appium sunucularını durdurur
     */
    public void stopAllServers() {
        logger.info("Tüm Appium sunucuları durduruluyor...");
        
        for (Process process : appiumProcesses) {
            try {
                if (process.isAlive()) {
                    process.destroy();
                    logger.info("Appium sunucusu durduruldu");
                }
            } catch (Exception e) {
                logger.error("Appium sunucusu durdurulurken hata", e);
            }
        }
        
        // Force kill için ek kontrol
        try {
            Runtime.getRuntime().exec("pkill -f appium");
            logger.info("Tüm Appium süreçleri sonlandırıldı");
        } catch (IOException e) {
            logger.error("Appium süreçleri sonlandırılırken hata", e);
        }
        
        appiumProcesses.clear();
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        logger.info("Tüm Appium sunucuları durduruldu");
    }

    /**
     * Belirli bir porttaki Appium sunucusunu durdurur
     */
    public void stopServer(int port) {
        try {
            String command = String.format("lsof -ti:%d | xargs kill -9", port);
            Runtime.getRuntime().exec(command);
            logger.info("Port {}'deki Appium sunucusu durduruldu", port);
        } catch (IOException e) {
            logger.error("Port {}'deki Appium sunucusu durdurulurken hata", port, e);
        }
    }

    /**
     * Sunucu durumunu kontrol eder
     */
    public boolean isServerRunning(int port) {
        try {
            String command = String.format("lsof -i:%d", port);
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            logger.error("Sunucu durumu kontrol edilirken hata - Port: {}", port, e);
            return false;
        }
    }

    /**
     * Temizlik işlemleri
     */
    public void cleanup() {
        stopAllServers();
    }
}
