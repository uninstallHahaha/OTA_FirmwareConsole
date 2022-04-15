package com.jinju.FirmwareServiceTransfer.controllers;

import com.jinju.FirmwareServiceTransfer.annotations.Permission;
import com.jinju.FirmwareServiceTransfer.beans.*;
import com.jinju.FirmwareServiceTransfer.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    Util util;

    @Resource(name = "versionInfoMap")
    Map<String, Object> versionInfoMap;

    @Resource(name = "diskInfo")
    DiskInfo diskInfo;


    @Value("${loginUsername}")
    String loginUsername;

    @Value("${loginPassword}")
    String loginPassword;


    @RequestMapping("/test")
    public String test(Model model){
        buildIndexPageData(model);
        return "test";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("token")!=null) {
            buildIndexPageData(model);
            return "test";
        }
        return "login";
    }

    @PostMapping("/verifyLogin")
    @ResponseBody
    public String verifyLogin(@RequestBody User user, HttpServletRequest request){
        if(user.getUserName().equals(this.loginUsername) && user.getPassWord().equals(this.loginPassword)){
            request.getSession().setAttribute("token", user);
            return "ok";
        }
        return "";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "login";
    }

    @Permission
    @GetMapping("/index")
    public String index(Model model) {
        buildIndexPageData(model);
        return "test";
    }

    void buildIndexPageData(Model model){
        List<CompanyBean> companyBeans = new ArrayList<>();
        versionInfoMap.forEach((companyName, projects) -> {
            CompanyBean companyBean = new CompanyBean();
            companyBean.setName(companyName);
            List<ProjBean> projBeans = new ArrayList<>();
            ((Map<String, List>) projects).forEach((pn, devices) -> {
                List<DeviceBean> deviceBeans = new ArrayList<>();
                devices.forEach(d->{
                    String dVersion = ((Map<String, String>)d).get("version");
                    String rVersion = ((Map<String, String>)d).get("remoteVersion");
                    String location = ((Map<String, String>)d).get("location");
                    deviceBeans.add(new DeviceBean(dVersion, rVersion, location));
//                    deviceBeans.add((DeviceBean) d);
                });
                ProjBean projBean = new ProjBean();
                projBean.setName(pn);
                projBean.setDevices(deviceBeans);
//                ProjBean projBean = new ProjBean(pn,
//                        new DeviceBean(((Map<String, String>) devices).get("version"),
//                                ((Map<String, String>) devices).get("location")));
                projBeans.add(projBean);
            });
            companyBean.setProjBeans(projBeans);
            companyBeans.add(companyBean);
        });
        model.addAttribute("companyBeans", companyBeans);
        this.diskInfo = util.refreshDiskInfo();
        model.addAttribute("diskInfo", diskInfo);
    }
}
