package com.appiumautomation.testrunners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.appiumautomation.steps",
        "com.appiumautomation.hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/simple-test.html"
    },
    monochrome = true,
    dryRun = true, // Sadece test adımlarını kontrol et, gerçek çalıştırma yapma
    publish = false
)
public class SimpleCucumberTest extends AbstractTestNGCucumberTests {

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("=== Basit Cucumber Test Başlatılıyor ===");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("=== Basit Cucumber Test Tamamlandı ===");
    }
}
