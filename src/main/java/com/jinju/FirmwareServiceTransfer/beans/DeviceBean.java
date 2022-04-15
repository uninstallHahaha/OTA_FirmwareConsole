package com.jinju.FirmwareServiceTransfer.beans;


public class DeviceBean {

    String version;
    String remoteVersion;
    String location;

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DeviceBean() {
    }

    public DeviceBean(String version, String remoteVersion, String location) {
        this.version = version;
        this.remoteVersion = remoteVersion;
        this.location = location;
    }
}
