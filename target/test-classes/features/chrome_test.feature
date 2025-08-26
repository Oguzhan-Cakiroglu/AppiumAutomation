@chrome @android @browser @smoke
Feature: Chrome Browser Test
  As a user
  I want to test Chrome browser functionality
  So that I can verify browser automation works

  @chrome_open @google
  Scenario: Chrome'da Google'a Gitme
    Given kullanıcı "device1" cihazında Chrome'u açar
    And kullanıcı "device2" cihazında Chrome'u açar
    When kullanıcı "device1" cihazında "google.com" adresine gider
    And kullanıcı "device2" cihazında "google.com" adresine gider
    Then kullanıcı "device1" cihazında "Google" başlığını görür
    And kullanıcı "device2" cihazında "Google" başlığını görür
    When kullanıcı "device1" cihazında arama kutusuna "Appium" yazar
    And kullanıcı "device2" cihazında arama kutusuna "Selenium" yazar
    Then kullanıcı "device1" cihazında arama sonuçlarını görür
    And kullanıcı "device2" cihazında arama sonuçlarını görür
