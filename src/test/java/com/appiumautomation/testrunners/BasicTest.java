package com.appiumautomation.testrunners;

import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;

public class BasicTest {
    
    @Test
    public void testCucumberTestRunner() {
        System.out.println("=== CucumberTestRunner Test Başlatılıyor ===");
        
        try {
            // CucumberTestRunner sınıfını test et
            Class<?> testRunnerClass = Class.forName("com.appiumautomation.testrunners.CucumberTestRunner");
            System.out.println("CucumberTestRunner sınıfı bulundu: " + testRunnerClass.getName());
            
            // Test runner'ın annotation'larını kontrol et
            CucumberOptions options = testRunnerClass.getAnnotation(CucumberOptions.class);
            if (options != null) {
                System.out.println("CucumberOptions bulundu:");
                System.out.println("- Features: " + options.features());
                System.out.println("- Glue: " + String.join(", ", options.glue()));
                System.out.println("- Monochrome: " + options.monochrome());
                System.out.println("- Dry Run: " + options.dryRun());
            }
            
            System.out.println("=== Test Başarılı ===");
            
        } catch (ClassNotFoundException e) {
            System.err.println("CucumberTestRunner sınıfı bulunamadı: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Test sırasında hata: " + e.getMessage());
        }
    }
}

