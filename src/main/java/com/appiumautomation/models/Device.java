package com.appiumautomation.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("deviceName")
    private String deviceName;
    
    @JsonProperty("platformVersion")
    private String platformVersion;
    
    @JsonProperty("appiumPort")
    private int appiumPort;
    
    @JsonProperty("systemPort")
    private int systemPort;
    
    @JsonProperty("udid")
    private String udid;
    
    @JsonProperty("status")
    private String status;

    public Device() {}

    public Device(String deviceId, String deviceName, String platformVersion, int appiumPort, int systemPort, String udid) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.platformVersion = platformVersion;
        this.appiumPort = appiumPort;
        this.systemPort = systemPort;
        this.udid = udid;
        this.status = "CONNECTED";
    }

    // Getters and Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public int getAppiumPort() {
        return appiumPort;
    }

    public void setAppiumPort(int appiumPort) {
        this.appiumPort = appiumPort;
    }

    public int getSystemPort() {
        return systemPort;
    }

    public void setSystemPort(int systemPort) {
        this.systemPort = systemPort;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", platformVersion='" + platformVersion + '\'' +
                ", appiumPort=" + appiumPort +
                ", systemPort=" + systemPort +
                ", udid='" + udid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
