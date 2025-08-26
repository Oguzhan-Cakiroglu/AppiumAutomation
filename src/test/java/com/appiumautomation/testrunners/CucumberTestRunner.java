package com.appiumautomation.testrunners;

import com.appiumautomation.hooks.Hooks;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.TestNG;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.appiumautomation.steps",
        "com.appiumautomation.hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber-pretty.html",
        "json:target/cucumber-reports/CucumberTestReport.json",
        "junit:target/cucumber-reports/CucumberTestReport.xml"
    },
    monochrome = true,
    dryRun = false,
    publish = false
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("=== Appium Multi Device Test Suite Başlatılıyor ===");
        System.out.println("Test ortamı hazırlanıyor...");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("=== Test Suite Tamamlandı ===");
        System.out.println("Test ortamı temizleniyor...");
        
        // Test ortamını temizle
        Hooks.cleanupTestEnvironment();
        
        System.out.println("Test ortamı temizlendi.");
    }

    // Main metodu ekledik - doğrudan çalıştırılabilir
    public static void main(String[] args) {
        System.out.println("=== CucumberTestRunner Başlatılıyor ===");
        
        // TestNG ile testi çalıştır
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[]{CucumberTestRunner.class});
        testNG.run();
        
        System.out.println("=== CucumberTestRunner Tamamlandı ===");
    }
}
