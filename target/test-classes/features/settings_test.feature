@settings @android
Feature: Android Ayarlar Uygulaması Testleri
  Çoklu cihaz desteği ile Android ayarlar uygulamasının test edilmesi

  Background:
    Given kullanıcı "device1" cihazında uygulamayı açar
    And kullanıcı "device2" cihazında uygulamayı açar

  @wifi @smoke
  Scenario: Wi-Fi Ayarlarını Kontrol Etme
    Kullanıcılar farklı cihazlarda Wi-Fi ayarlarını kontrol edebilir
    
    When kullanıcı "device1" cihazında "Wi-Fi" elementini tıklar
    And kullanıcı "device2" cihazında "Wi-Fi" elementini tıklar
    Then kullanıcı "device1" cihazında "Wi-Fi" elementinin görünür olduğunu doğrular
    And kullanıcı "device2" cihazında "Wi-Fi" elementinin görünür olduğunu doğrular
    When kullanıcı "device1" cihazında geri tuşuna basar
    And kullanıcı "device2" cihazında geri tuşuna basar

  @search @regression
  Scenario: Ayarlar Arama Fonksiyonu
    Kullanıcılar farklı cihazlarda ayarlar arama fonksiyonunu kullanabilir
    
    When kullanıcı "device1" cihazında "Arama" alanına "Wi-Fi" yazar
    And kullanıcı "device2" cihazında "Arama" alanına "Bluetooth" yazar
    Then kullanıcı "device1" cihazında "Wi-Fi" metninin görünür olduğunu doğrular
    And kullanıcı "device2" cihazında "Bluetooth" metninin görünür olduğunu doğrular
    When kullanıcı "device1" cihazında geri tuşuna basar
    And kullanıcı "device2" cihazında geri tuşuna basar

  @navigation @smoke
  Scenario: Ayarlar Menüsünde Gezinme
    Kullanıcılar farklı cihazlarda ayarlar menüsünde gezinme yapabilir
    
    When kullanıcı "device1" cihazında "Ayarlar" elementini tıklar
    And kullanıcı "device2" cihazında "Ayarlar" elementini tıklar
    Then kullanıcı "device1" cihazında "Ayarlar" elementinin görünür olduğunu doğrular
    And kullanıcı "device2" cihazında "Ayarlar" elementinin görünür olduğunu doğrular
    When kullanıcı "device1" cihazında 2 saniye bekler
    And kullanıcı "device2" cihazında 2 saniye bekler
