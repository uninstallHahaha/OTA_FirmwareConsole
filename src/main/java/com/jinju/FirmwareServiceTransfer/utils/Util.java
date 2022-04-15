package com.jinju.FirmwareServiceTransfer.utils;

import com.jinju.FirmwareServiceTransfer.beans.DiskInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component(value = "serverUtil")
public class Util {

    @Value("${firmwareFilePath}")
    String firmwareFilePath;

    public DiskInfo refreshDiskInfo(){
        File file = new File(this.firmwareFilePath);
        if(!file.exists()) return null;
        Long used = (file.getTotalSpace()-file.getUsableSpace())/1024/1024;
        Long total = file.getTotalSpace()/1024/1024;
        double usedRate = (double) (used*100/total);
        return new DiskInfo(used, total, usedRate) ;
    }

//    public static void main(String[] args) {
//        Util util = new Util();
//        System.out.println(util.compareVersion("2.1.1.0","2.1.1",0));
//    }

    public int compareVersion(String com, String remain, int begin){
        String[] comcs = com.trim().substring(begin).split("\\.");
        String[] remaincs = remain.trim().substring(begin).split("\\.");
        int cp = 0, rp = 0;
        int clen = comcs.length, rlen = remaincs.length;
        while (cp<clen && rp<rlen){
            int cn = Integer.parseInt(comcs[cp]);
            int rn = Integer.parseInt(remaincs[rp]);
            if(cn<rn) return -1;
            else if(cn>rn) return 1;
            cp++;
            rp++;
        }
        if(cp<clen) return 1;
        if(rp<rlen) return -1;
        return 0;
    }
}
