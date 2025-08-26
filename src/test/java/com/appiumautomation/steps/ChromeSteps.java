package com.appiumautomation.steps;

import com.appiumautomation.core.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ChromeSteps {
    private static final Logger logger = LoggerFactory.getLogger(ChromeSteps.class);
    private DriverManager driverManager = DriverManager.getInstance();

    @Given("kullanıcı {string} cihazında Chrome'u açar")
    public void kullaniciCihazdaChromuAcar(String deviceId) {
        logger.info("Chrome açılıyor - Cihaz: {}", deviceId);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            // Chrome'u başlat
            driver.activateApp("com.android.chrome");
            logger.info("Chrome başarıyla açıldı - Cihaz: {}", deviceId);
            
            // Chrome'un yüklenmesini bekle
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            logger.error("Chrome açılamadı - Cihaz: {}", deviceId, e);
            throw e;
        }
    }

    @When("kullanıcı {string} cihazında {string} adresine gider")
    public void kullaniciCihazdaAdreseGider(String deviceId, String url) {
        logger.info("URL'ye gidiliyor - Cihaz: {}, URL: {}", deviceId, url);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            // URL'yi aç
            driver.get("https://" + url);
            logger.info("URL'ye başarıyla gidildi - Cihaz: {}, URL: {}", deviceId, url);
            
            // Sayfanın yüklenmesini bekle
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            logger.error("URL'ye gidilemedi - Cihaz: {}, URL: {}", deviceId, url, e);
            throw e;
        }
    }

    @Then("kullanıcı {string} cihazında {string} başlığını görür")
    public void kullaniciCihazdaBasligiGorur(String deviceId, String expectedTitle) {
        logger.info("Başlık kontrol ediliyor - Cihaz: {}, Beklenen: {}", deviceId, expectedTitle);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.titleContains(expectedTitle));
            
            String actualTitle = driver.getTitle();
            logger.info("Başlık görüldü - Cihaz: {}, Başlık: {}", deviceId, actualTitle);
            
        } catch (Exception e) {
            logger.error("Başlık kontrolü başarısız - Cihaz: {}, Beklenen: {}", deviceId, expectedTitle, e);
            throw e;
        }
    }

    @When("kullanıcı {string} cihazında arama kutusuna {string} yazar")
    public void kullaniciCihazdaAramaKutusunaYazar(String deviceId, String searchText) {
        logger.info("Arama yapılıyor - Cihaz: {}, Arama: {}", deviceId, searchText);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            // Google arama kutusunu bul ve yaz
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.clear();
            searchBox.sendKeys(searchText);
            
            // Enter tuşuna bas
            searchBox.submit();
            
            logger.info("Arama başarıyla yapıldı - Cihaz: {}, Arama: {}", deviceId, searchText);
            
            // Arama sonuçlarının yüklenmesini bekle
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            logger.error("Arama yapılamadı - Cihaz: {}, Arama: {}", deviceId, searchText, e);
            throw e;
        }
    }

    @Then("kullanıcı {string} cihazında arama sonuçlarını görür")
    public void kullaniciCihazdaAramaSonuclariniGorur(String deviceId) {
        logger.info("Arama sonuçları kontrol ediliyor - Cihaz: {}", deviceId);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            // Arama sonuçlarının yüklenmesini bekle
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));
            
            logger.info("Arama sonuçları görüldü - Cihaz: {}", deviceId);
            
        } catch (Exception e) {
            logger.error("Arama sonuçları kontrolü başarısız - Cihaz: {}", deviceId, e);
            throw e;
        }
    }
}
