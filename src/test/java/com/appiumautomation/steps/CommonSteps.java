package com.appiumautomation.steps;

import com.appiumautomation.core.DriverManager;
import com.appiumautomation.models.Device;
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

public class CommonSteps {
    private static final Logger logger = LoggerFactory.getLogger(CommonSteps.class);
    private DriverManager driverManager = DriverManager.getInstance();

    @Given("kullanıcı {string} cihazında uygulamayı açar")
    public void kullaniciCihazdaUygulamayiAcar(String deviceId) {
        logger.info("Uygulama açılıyor - Cihaz: {}", deviceId);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        Device device = driverManager.getDevice(deviceId);
        logger.info("Cihaz bilgileri: {}", device);
        
        // Uygulama zaten açık olmalı, sadece log
        logger.info("Uygulama başarıyla açıldı - Cihaz: {}", deviceId);
    }

    @When("kullanıcı {string} cihazında {string} elementini tıklar")
    public void kullaniciCihazdaElementiniTiklar(String deviceId, String elementName) {
        logger.info("Element tıklanıyor - Cihaz: {}, Element: {}", deviceId, elementName);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            WebElement element = findElement(driver, elementName);
            element.click();
            logger.info("Element başarıyla tıklandı - Cihaz: {}, Element: {}", deviceId, elementName);
        } catch (Exception e) {
            logger.error("Element tıklanamadı - Cihaz: {}, Element: {}", deviceId, elementName, e);
            throw e;
        }
    }

    @When("kullanıcı {string} cihazında {string} alanına {string} yazar")
    public void kullaniciCihazdaAlanaYazar(String deviceId, String fieldName, String text) {
        logger.info("Metin yazılıyor - Cihaz: {}, Alan: {}, Metin: {}", deviceId, fieldName, text);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            WebElement element = findElement(driver, fieldName);
            element.clear();
            element.sendKeys(text);
            logger.info("Metin başarıyla yazıldı - Cihaz: {}, Alan: {}, Metin: {}", deviceId, fieldName, text);
        } catch (Exception e) {
            logger.error("Metin yazılamadı - Cihaz: {}, Alan: {}, Metin: {}", deviceId, fieldName, text, e);
            throw e;
        }
    }

    @Then("kullanıcı {string} cihazında {string} elementinin görünür olduğunu doğrular")
    public void kullaniciCihazdaElementininGorunurOldugunuDogrular(String deviceId, String elementName) {
        logger.info("Element görünürlüğü kontrol ediliyor - Cihaz: {}, Element: {}", deviceId, elementName);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            WebElement element = findElement(driver, elementName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOf(element));
            
            if (element.isDisplayed()) {
                logger.info("Element görünür - Cihaz: {}, Element: {}", deviceId, elementName);
            } else {
                throw new RuntimeException("Element görünür değil: " + elementName);
            }
        } catch (Exception e) {
            logger.error("Element görünürlük kontrolü başarısız - Cihaz: {}, Element: {}", deviceId, elementName, e);
            throw e;
        }
    }

    @Then("kullanıcı {string} cihazında {string} metninin görünür olduğunu doğrular")
    public void kullaniciCihazdaMetnininGorunurOldugunuDogrular(String deviceId, String expectedText) {
        logger.info("Metin kontrol ediliyor - Cihaz: {}, Beklenen Metin: {}", deviceId, expectedText);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), '" + expectedText + "')]")
            ));
            
            if (element.isDisplayed()) {
                logger.info("Metin görünür - Cihaz: {}, Metin: {}", deviceId, expectedText);
            } else {
                throw new RuntimeException("Metin görünür değil: " + expectedText);
            }
        } catch (Exception e) {
            logger.error("Metin kontrolü başarısız - Cihaz: {}, Metin: {}", deviceId, expectedText, e);
            throw e;
        }
    }

    @When("kullanıcı {string} cihazında geri tuşuna basar")
    public void kullaniciCihazdaGeriTusunaBasar(String deviceId) {
        logger.info("Geri tuşuna basılıyor - Cihaz: {}", deviceId);
        
        AndroidDriver driver = driverManager.getDriver(deviceId);
        if (driver == null) {
            throw new RuntimeException("Driver bulunamadı: " + deviceId);
        }
        
        try {
            // Basit geri tuşu simülasyonu
            driver.navigate().back();
            logger.info("Geri tuşuna basıldı - Cihaz: {}", deviceId);
        } catch (Exception e) {
            logger.error("Geri tuşuna basılamadı - Cihaz: {}", deviceId, e);
            throw e;
        }
    }

    @When("kullanıcı {string} cihazında {int} saniye bekler")
    public void kullaniciCihazdaSaniyeBekler(String deviceId, int seconds) {
        logger.info("Bekleniyor - Cihaz: {}, Süre: {} saniye", deviceId, seconds);
        
        try {
            Thread.sleep(seconds * 1000L);
            logger.info("Bekleme tamamlandı - Cihaz: {}, Süre: {} saniye", deviceId, seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Bekleme kesildi - Cihaz: {}", deviceId, e);
        }
    }

    /**
     * Element bulma yardımcı metodu
     */
    private WebElement findElement(AndroidDriver driver, String elementName) {
        // Bu metod element adına göre uygun locator'ı döndürür
        // Gerçek uygulamada element mapping'i yapılmalı
        switch (elementName.toLowerCase()) {
            case "ayarlar":
                return driver.findElement(By.xpath("//android.widget.TextView[@text='Ayarlar']"));
            case "wifi":
                return driver.findElement(By.xpath("//android.widget.TextView[@text='Wi-Fi']"));
            case "wifi_switch":
                return driver.findElement(By.id("android:id/switch_widget"));
            case "arama":
                return driver.findElement(By.id("android:id/search_src_text"));
            default:
                // Varsayılan olarak text ile arama yap
                return driver.findElement(By.xpath("//*[@text='" + elementName + "']"));
        }
    }
}
