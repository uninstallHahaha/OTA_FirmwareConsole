package com.jinju.FirmwareServiceTransfer.utils;

import com.jinju.FirmwareServiceTransfer.beans.DeviceBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InfoParser {

    public DeviceBean parseInfo(Map<String, Object> infoMap,
                                String companyName,
                                String projectName,
                                String deviceVersion) {
        Map<String, String> info;
        if (infoMap.containsKey(companyName) &&
                ((Map<String, Map>) infoMap.get(companyName)).containsKey(projectName)
        ) {
            List<Map<String, String>> devices =
                    (List<Map<String, String>>)(((Map<String, Map>) infoMap.get(companyName)).get(projectName));
            DeviceBean deviceBean = new DeviceBean();
            for(int i=0;i<devices.size();i++){
                Map<String, String> deviceInfo = devices.get(i);
                String ver = deviceInfo.get("version");
                if(ver.equals(deviceVersion)){
                    deviceBean.setVersion(ver);
                    deviceBean.setRemoteVersion(deviceInfo.get("remoteVersion"));
                    deviceBean.setLocation(deviceInfo.get("location"));
                    return deviceBean;
                }
            }
        }
        return null;
    }
}
