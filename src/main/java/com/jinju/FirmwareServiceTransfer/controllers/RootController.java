package com.jinju.FirmwareServiceTransfer.controllers;


import com.jinju.FirmwareServiceTransfer.beans.DeviceBean;
import com.jinju.FirmwareServiceTransfer.utils.InfoParser;
import com.jinju.FirmwareServiceTransfer.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RootController {

    @Value("${countConcurrent}")
    int countConcurrent;

    @Value("${firmwarePath}")
    String firmwarePath;

    @Autowired
    InfoParser infoParser;

    @Autowired
    @Qualifier(value = "serverUtil")
    Util util;

    @Resource(name = "versionInfoMap")
    Map<String, Object> versionInfoMap;

    @RequestMapping(value = "/getall")
    @ResponseBody
    public Map<String, Object> hello() {
        return this.versionInfoMap;
    }

    @PostMapping(value = "/")
    @ResponseBody
    public Map<String, Object> hello(@RequestBody Map map) {
        String company = (String) map.get("company");
        String project = (String) map.get("project");
        String deviceVersion = (String) map.get("deviceVersion");
        String remoteVersion = (String) map.get("remoteVersion");
        // todo
        String mac = (String) map.get("mac");

        if(deviceVersion.equals("")) deviceVersion = "null";
        // get information
        DeviceBean deviceBean = infoParser.parseInfo(this.versionInfoMap, company, project, deviceVersion);
        if (deviceBean == null) return null;
        // don't need update
        if (util.compareVersion(remoteVersion, deviceBean.getRemoteVersion(), 1) >= 0)
            return new HashMap<String, Object>() { };
            // ret update location
        else return new HashMap<String, Object>() {
            {
                put("version", deviceBean.getVersion());
                put("remoteVersion", deviceBean.getRemoteVersion());
                put("location", deviceBean.getLocation());
            }
        };

        // invalid version
//        if ("999".equals(version)) return null;
    }


//    @RequestMapping(value = "/getFirmware/{vid}", method = RequestMethod.GET)
//    public String getFirmware(@PathVariable(name = "vid") String vid) {
////        if (vid.equals(firmware_version))
//        return "redirect:/obtainFirmware/" + vid;
////        else return "redirect:/data.empty";
//    }

//    @RequestMapping(path = "/obtainFirmware/{vid}")
//    public String obtainFirmware(@PathVariable(name = "vid") String vid) {
//        return "redirect:/ax.dat";
//    }


//    @RequestMapping(path = "/update/{companyName}/{projectName}/{curVid}")
//    @ResponseBody
//    public String update(@PathVariable(name = "companyName") String companyName,
//                         @PathVariable(name = "projectName") String projectName,
//                         @PathVariable(name = "curVid") String curVid) {
//        DeviceBean deviceBean = infoParser.parseInfo(versionInfoMap, companyName, projectName);
//        if (deviceBean == null) return "no such project";
//        return deviceBean.getVersion().equals(curVid) ?
//                "no update since before" : deviceBean.getLocation();
//    }
}
