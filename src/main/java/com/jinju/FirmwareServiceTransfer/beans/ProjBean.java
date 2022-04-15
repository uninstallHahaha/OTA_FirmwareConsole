package com.jinju.FirmwareServiceTransfer.beans;

import java.util.List;

public class ProjBean {

    String name;
    List<DeviceBean> devices;

    public ProjBean() {
    }


    public ProjBean(String name, List<DeviceBean> devices) {
        this.name = name;
        this.devices = devices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeviceBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceBean> devices) {
        this.devices = devices;
    }
}
