package com.jinju.FirmwareServiceTransfer.controllers;


import com.jinju.FirmwareServiceTransfer.annotations.Permission;
import com.jinju.FirmwareServiceTransfer.beans.DeviceBean;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Predicate;

@Controller
@RequestMapping("/opt")
public class OptController {

    @Autowired
    ZooKeeper zk;

    @Resource(name = "versionInfoMap")
    Map<String, Object> versionInfoMap;

    @Resource(name = "yaml")
    Yaml yaml;

    @Value("${basePath}")
    String basePath;

    @Value("${versionInfoFileName}")
    String versionInfoFileName;

    @Value("${firmwareFilePath}")
    String firmwareFilePath;

    @Value("${firmwarePath}")
    String firmwarePath;

    @Value("${configVersion}")
    String configVersion;

    // test
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
//        this.versionInfoMap = new HashMap<>();
        return "OK";
    }


    // upload firmware file and add firmware info
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest req) {

        String fileName = "";
        String filePath = this.firmwareFilePath;
        if (file.isEmpty()) fileName = "-";
        else fileName = file.getOriginalFilename();

        String companyName = req.getParameter("cni");
        String projectName = req.getParameter("pni");
        String deviceVersion = req.getParameter("dvi");
        String version = req.getParameter("vi");

        // without device
        if(deviceVersion.equals("") && !file.isEmpty()) deviceVersion = "null";

        // record to versionInfo.yml
        addRecordIntoVersionInfoMap(companyName, projectName, deviceVersion, version, fileName);
        restore();
        // upload file
        if(!file.isEmpty()){
            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                return "Fail to upload, please retry later!";
            }
        }
        return "redirect:/index";
    }

    // add if not exists, or modify
//    @RequestMapping(value = "/add/{companyName}/{projectName}/{version}/{remoteVersion}/{fileName}")
//    @ResponseBody()
//    @Permission
//    public String addVersionInfo(@PathVariable(name = "companyName") String companyName,
//                                 @PathVariable(name = "projectName") String projectName,
//                                 @PathVariable(name = "version") String version,
//                                 @PathVariable(name = "remoteVersion") String remoteVersion,
//                                 @PathVariable(name = "fileName", required = false) String fileName) {
//        addRecordIntoVersionInfoMap(companyName, projectName, version, remoteVersion, fileName);
//        restore();
//        return "OK";
//    }


    // delete proj
    @RequestMapping("/del/{companyName}/{projectName}/{deviceVersion}")
    @ResponseBody
    @Permission
    public String del(@PathVariable(name = "companyName") String companyName,
                      @PathVariable(name = "projectName") String projectName,
                      @PathVariable(name = "deviceVersion") String deviceVersion) {
        String fileName = "";
        if (!versionInfoMap.containsKey(companyName)) return "OK, this is not exist";
        Map<String, Object> projsMap = (Map<String, Object>) versionInfoMap.get(companyName);

        if (!projsMap.containsKey(projectName)) return "OK, this is not exist";
        List<Map<String, String>> devicesInfo = ((List<Map<String, String>>) projsMap.get(projectName));

        Iterator<Map<String, String>> iterator = devicesInfo.iterator();
        while(iterator.hasNext()){
            Map<String, String> next = iterator.next();
            if(next.get("version").equals(deviceVersion)) {
                iterator.remove();
                break;
            }
        }
        restore();
        // del file
        if (!containFileName(fileName) && !delFirmwareFile(fileName)) return "OK,but need del file by hand";
        else return "OK";
    }

    // delete company
    @RequestMapping("/delCompany/{companyName}")
    @ResponseBody
    @Permission
    public String delCompany(@PathVariable(name = "companyName") String companyName) {
        String fileName = "";
        Map<String, Object> projs = new HashMap<>();
        if (!versionInfoMap.containsKey(companyName)) return "OK, this is not exist";
        else {
            projs = (Map<String, Object>) versionInfoMap.get(companyName);
            versionInfoMap.remove(companyName);
        }
        restore();
        // delete files
        projs.forEach((pName, pObj) -> {
            List<Map<String, String>> devices = (List<Map<String, String>>) pObj;
            devices.forEach(device->{
                String candidate = device.get("location").split(firmwarePath)[1];
                if (!containFileName(candidate)) delFirmwareFile(candidate);
            });
        });
        return "OK";
    }

    // check whether contains this file
    boolean containFileName(String fileName) {
        return !this.versionInfoMap.values().stream().allMatch(new Predicate() {
            @Override
            public boolean test(Object proj) {
                return ((Map<String, List<Object>>) proj).values().stream().allMatch(new Predicate() {
                    @Override
                    public boolean test(Object devices) {
                        return ((ArrayList<Map<String, String>>)devices).stream().allMatch(new Predicate<Map<String, String>>() {
                            @Override
                            public boolean test(Map<String, String> device) {
                                String curname = device.get("location").split(firmwarePath)[1];
                                return !curname.equals(fileName);
                            }
                        });
                    }
                });
            }
        });
    }

    // del firmware file
    boolean delFirmwareFile(String fileName) {
        System.err.println("del file " + fileName);
        String filePath = this.firmwareFilePath;
        File file = new File(filePath + fileName);
        if (!file.exists()) return false;
        try {
            file.delete();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    // map from company
    Map<String, Object> buildCompanyMap(String company, String project,
                                        String version, String location) {
        Map<String, Object> infoMap = new LinkedHashMap<>();
        infoMap.put("version", version);
        infoMap.put("location", location);

        Map<String, Object> projsMap = new LinkedHashMap<>();
        projsMap.put(project, infoMap);

        Map<String, Object> companyMap = new LinkedHashMap<>();
        companyMap.put(company, projsMap);

        return companyMap;
    }

    // map from project
    Map<String, List<Map<String, String>>> buildProjectMap(String projectName,
                                                  String version, String remoteVersion,
                                                  String location) {
        List<Map<String, String>> deviceBeans = buildDeviceList(version, remoteVersion, location);

        Map<String, List<Map<String, String>>> projsMap = new LinkedHashMap<>();

        projsMap.put(projectName, deviceBeans);

        return projsMap;
    }

    // map from info
    List<Map<String, String>> buildDeviceList(String version, String remoteVersion, String location) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> deviceBean = new LinkedHashMap<>();
        deviceBean.put("version", version);
        deviceBean.put("remoteVersion", remoteVersion);
        deviceBean.put("location", location);
        list.add(deviceBean);
        return list;
    }

    // add record into versionInfoMap
    boolean addRecordIntoVersionInfoMap(String companyName,
                                        String projectName,
                                        String version,
                                        String remoteVersion,
                                        String fileName) {
        // only add project
        if(fileName.equals("-")){
            if (this.versionInfoMap.containsKey(companyName)){
                Map<String, List> projects = (Map<String, List>)this.versionInfoMap.get(companyName);
                // create empty project
                if(!projects.containsKey(projectName)){
                    projects.put(projectName, new ArrayList());
                }
            }
            else{
                // create company and empty project
                Map<String, List<Map<String, String>>> projs = new LinkedHashMap<>();
                projs.put(projectName, new ArrayList<>());
                this.versionInfoMap.put(companyName, projs);
            }
            return true;
        }

        if (this.versionInfoMap.containsKey(companyName)) {
            Map<String, Object> projs = (Map<String, Object>) versionInfoMap.get(companyName);

            if (projs.containsKey(projectName)) {
                List<Map<String, String>> deviceBeans = (List<Map<String, String>>) projs.get(projectName);
                boolean containsDevice = false;
                int deviceLoc = 0;
                for (; deviceLoc < deviceBeans.size(); deviceLoc++) {
                    Map<String, String> deviceInfo = deviceBeans.get(deviceLoc);
                    String verison =  (deviceInfo.get("version"));
                    String  remoteVer= (deviceInfo.get("remoteVersion"));
                    if (verison.equals(version) && remoteVer.equals(remoteVersion)) {
                        containsDevice = true;
                        deviceBeans.get(deviceLoc).put("location", firmwarePath + fileName);
                        break;
                    }
                }
                if (!containsDevice) {
                    Map<String, String> deviceInfo = new LinkedHashMap<>();
                    deviceInfo.put("version", version);
                    deviceInfo.put("remoteVersion", remoteVersion);
                    deviceInfo.put("location", firmwarePath + fileName);
                    deviceBeans.add(deviceInfo);
                }
            } else {
                projs.put(projectName, buildDeviceList(version, remoteVersion, firmwarePath + fileName));
            }
        } else
            this.versionInfoMap.put(companyName, buildProjectMap(projectName, version, remoteVersion, firmwarePath + fileName));
        this.versionInfoMap.remove("none");
        return true;
    }


    // save yml file
    public Boolean restore() {
        try {
            StringWriter writer = new StringWriter();
            yaml.dump(this.versionInfoMap, writer);

            File file = new File(basePath);
            if (!file.exists())
                if (!file.mkdirs()) return false;
            String filePath = basePath + "/" + versionInfoFileName;
            file = new File(filePath);
            if (!file.exists()) file.createNewFile();

            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(writer.toString());
            fileWriter.close();
            writer.close();

            // update zk node version
//            zk.setData("/root/version",
//                    (System.currentTimeMillis() + "").getBytes(), -1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
