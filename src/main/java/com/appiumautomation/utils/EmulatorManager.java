package com.appiumautomation.utils;

import com.appiumautomation.models.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmulatorManager {
    private static final Logger logger = LoggerFactory.getLogger(EmulatorManager.class);
    private static final String EMULATOR_PATH = "emulator";
    private static final String AVD_PATH = "avdmanager";
    private List<Process> emulatorProcesses;
    private ExecutorService executorService;

    public EmulatorManager() {
        this.emulatorProcesses = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Mevcut AVD'leri listeler
     */
    public List<String> listAvailableAVDs() {
        List<String> avdList = new ArrayList<>();
        try {
            String command = AVD_PATH + " list avd";
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);

            // Ortam değişkenlerini geçir
            String sdkRoot = System.getenv().getOrDefault("ANDROID_SDK_ROOT", System.getProperty("user.home") + "/Library/Android/sdk");
            String androidHome = System.getenv().getOrDefault("ANDROID_HOME", sdkRoot);
            String javaHome = System.getenv().getOrDefault("JAVA_HOME", "/opt/homebrew/opt/openjdk");
            String toolsPath = sdkRoot + "/tools/bin";
            String platformToolsPath = sdkRoot + "/platform-tools";
            String emulatorPath = sdkRoot + "/emulator";
            String cmdlineToolsPath = "/opt/homebrew/share/android-commandlinetools/cmdline-tools/latest/bin";
            pb.environment().put("ANDROID_SDK_ROOT", sdkRoot);
            pb.environment().put("ANDROID_HOME", androidHome);
            pb.environment().put("JAVA_HOME", javaHome);
            pb.environment().put("PATH", javaHome + "/bin:" + platformToolsPath + ":" + emulatorPath + ":" + cmdlineToolsPath + ":" + toolsPath + ":" + pb.environment().getOrDefault("PATH", ""));

            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Name:")) {
                    String avdName = line.split("Name:")[1].trim();
                    avdList.add(avdName);
                    logger.info("Bulunan AVD: {}", avdName);
                }
            }
            
            process.waitFor();
        } catch (Exception e) {
            logger.error("AVD listesi alınırken hata", e);
        }
        return avdList;
    }

    /**
     * Sanal cihazları başlatır
     */
    public List<Device> startEmulators(List<String> avdNames) {
        logger.info("Sanal cihazlar başlatılıyor...");
        List<Device> emulatorDevices = new ArrayList<>();
        
        for (int i = 0; i < avdNames.size(); i++) {
            String avdName = avdNames.get(i);
            final int deviceIndex = i;
            
            executorService.submit(() -> {
                try {
                    Device device = startEmulator(avdName, deviceIndex);
                    if (device != null) {
                        synchronized (emulatorDevices) {
                            emulatorDevices.add(device);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Emulator başlatılamadı: {}", avdName, e);
                }
            });
        }
        
        // Emulator'ların başlaması için bekle
        try {
            Thread.sleep(30000); // 30 saniye bekle
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("{} sanal cihaz başlatıldı", emulatorDevices.size());
        return emulatorDevices;
    }

    /**
     * Tek bir emulator'ı başlatır
     */
    private Device startEmulator(String avdName, int deviceIndex) {
        try {
            logger.info("Emulator başlatılıyor: {}", avdName);
            
            // Emulator başlatma komutu
            String emulatorCommand = String.format(
                "%s -avd %s -port %d -no-snapshot-save -no-audio -no-boot-anim",
                EMULATOR_PATH, avdName, 5554 + deviceIndex * 2
            );
            
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("sh", "-c", emulatorCommand);
            processBuilder.redirectErrorStream(true);

            // Ortam değişkenlerini geçir
            String sdkRoot2 = System.getenv().getOrDefault("ANDROID_SDK_ROOT", System.getProperty("user.home") + "/Library/Android/sdk");
            String androidHome2 = System.getenv().getOrDefault("ANDROID_HOME", sdkRoot2);
            String javaHome2 = System.getenv().getOrDefault("JAVA_HOME", "/opt/homebrew/opt/openjdk");
            String toolsPath2 = sdkRoot2 + "/tools/bin";
            String platformToolsPath2 = sdkRoot2 + "/platform-tools";
            String emulatorPath2 = sdkRoot2 + "/emulator";
            String cmdlineToolsPath2 = "/opt/homebrew/share/android-commandlinetools/cmdline-tools/latest/bin";
            processBuilder.environment().put("ANDROID_SDK_ROOT", sdkRoot2);
            processBuilder.environment().put("ANDROID_HOME", androidHome2);
            processBuilder.environment().put("JAVA_HOME", javaHome2);
            processBuilder.environment().put("PATH", javaHome2 + "/bin:" + platformToolsPath2 + ":" + emulatorPath2 + ":" + cmdlineToolsPath2 + ":" + toolsPath2 + ":" + processBuilder.environment().getOrDefault("PATH", ""));
            
            Process process = processBuilder.start();
            emulatorProcesses.add(process);
            
            // Emulator'un başlamasını bekle
            Thread.sleep(15000);
            
            // Emulator'un hazır olup olmadığını kontrol et
            if (isEmulatorReady(5554 + deviceIndex * 2)) {
                Device device = getEmulatorDeviceInfo(avdName, deviceIndex);
                logger.info("Emulator başarıyla başlatıldı: {} - Port: {}", avdName, 5554 + deviceIndex * 2);
                return device;
            } else {
                logger.error("Emulator başlatılamadı: {}", avdName);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Emulator başlatılırken hata: {}", avdName, e);
            return null;
        }
    }

    /**
     * Emulator'un hazır olup olmadığını kontrol eder
     */
    private boolean isEmulatorReady(int port) {
        try {
            String command = String.format("adb -s emulator-%d shell getprop sys.boot_completed", port);
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            // Ortam değişkenlerini geçir
            String sdkRoot = System.getenv().getOrDefault("ANDROID_SDK_ROOT", System.getProperty("user.home") + "/Library/Android/sdk");
            String androidHome = System.getenv().getOrDefault("ANDROID_HOME", sdkRoot);
            String javaHome = System.getenv().getOrDefault("JAVA_HOME", "/opt/homebrew/opt/openjdk");
            String toolsPath = sdkRoot + "/tools/bin";
            String platformToolsPath = sdkRoot + "/platform-tools";
            String emulatorPath = sdkRoot + "/emulator";
            String cmdlineToolsPath = "/opt/homebrew/share/android-commandlinetools/cmdline-tools/latest/bin";
            pb.environment().put("ANDROID_SDK_ROOT", sdkRoot);
            pb.environment().put("ANDROID_HOME", androidHome);
            pb.environment().put("JAVA_HOME", javaHome);
            pb.environment().put("PATH", javaHome + "/bin:" + platformToolsPath + ":" + emulatorPath + ":" + cmdlineToolsPath + ":" + toolsPath + ":" + pb.environment().getOrDefault("PATH", ""));
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = reader.readLine();
                return "1".equals(output);
            }
        } catch (Exception e) {
            logger.error("Emulator durumu kontrol edilirken hata - Port: {}", port, e);
        }
        return false;
    }

    /**
     * Emulator cihaz bilgilerini alır
     */
    private Device getEmulatorDeviceInfo(String avdName, int deviceIndex) {
        try {
            int port = 5554 + deviceIndex * 2;
            String deviceId = String.format("emulator-%d", port);
            
            // Cihaz adını al
            String deviceName = getEmulatorProperty(port, "ro.product.name");
            if (deviceName.isEmpty()) {
                deviceName = avdName;
            }
            
            // Platform versiyonunu al
            String platformVersion = getEmulatorProperty(port, "ro.build.version.release");
            if (platformVersion.isEmpty()) {
                platformVersion = "Unknown";
            }
            
            // Port numaralarını hesapla
            int appiumPort = 4723 + deviceIndex;
            int systemPort = 8200 + deviceIndex;
            
            return new Device(deviceId, deviceName, platformVersion, appiumPort, systemPort, deviceId);
            
        } catch (Exception e) {
            logger.error("Emulator bilgileri alınırken hata: {}", avdName, e);
            return null;
        }
    }

    /**
     * Emulator'dan property değeri alır
     */
    private String getEmulatorProperty(int port, String property) {
        try {
            String command = String.format("adb -s emulator-%d shell getprop %s", port, property);
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            // Ortam değişkenlerini geçir
            String sdkRoot = System.getenv().getOrDefault("ANDROID_SDK_ROOT", System.getProperty("user.home") + "/Library/Android/sdk");
            String androidHome = System.getenv().getOrDefault("ANDROID_HOME", sdkRoot);
            String javaHome = System.getenv().getOrDefault("JAVA_HOME", "/opt/homebrew/opt/openjdk");
            String toolsPath = sdkRoot + "/tools/bin";
            String platformToolsPath = sdkRoot + "/platform-tools";
            String emulatorPath = sdkRoot + "/emulator";
            String cmdlineToolsPath = "/opt/homebrew/share/android-commandlinetools/cmdline-tools/latest/bin";
            pb.environment().put("ANDROID_SDK_ROOT", sdkRoot);
            pb.environment().put("ANDROID_HOME", androidHome);
            pb.environment().put("JAVA_HOME", javaHome);
            pb.environment().put("PATH", javaHome + "/bin:" + platformToolsPath + ":" + emulatorPath + ":" + cmdlineToolsPath + ":" + toolsPath + ":" + pb.environment().getOrDefault("PATH", ""));
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return reader.readLine().trim();
            }
        } catch (Exception e) {
            logger.error("Emulator property alınırken hata - Port: {}, Property: {}", port, property, e);
        }
        return "";
    }

    /**
     * Tüm emulator'ları durdurur
     */
    public void stopAllEmulators() {
        logger.info("Tüm emulator'lar durduruluyor...");
        
        for (Process process : emulatorProcesses) {
            try {
                if (process.isAlive()) {
                    process.destroy();
                    logger.info("Emulator durduruldu");
                }
            } catch (Exception e) {
                logger.error("Emulator durdurulurken hata", e);
            }
        }
        
        // Force kill için ek kontrol
        try {
            Runtime.getRuntime().exec("pkill -f emulator");
            logger.info("Tüm emulator süreçleri sonlandırıldı");
        } catch (IOException e) {
            logger.error("Emulator süreçleri sonlandırılırken hata", e);
        }
        
        emulatorProcesses.clear();
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        logger.info("Tüm emulator'lar durduruldu");
    }

    /**
     * Temizlik işlemleri
     */
    public void cleanup() {
        stopAllEmulators();
    }
}
