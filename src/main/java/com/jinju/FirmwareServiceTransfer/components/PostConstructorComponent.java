package com.jinju.FirmwareServiceTransfer.components;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class PostConstructorComponent {

    @Value ("${firmwarePath}")
    String firmwarePath;

    @PostConstruct
    public void initIp() {
        String ip = "3.133.162.167";
        System.out.println("this host ip is : " + ip);
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        this.firmwarePath = ip + this.firmwarePath;
    }
}
