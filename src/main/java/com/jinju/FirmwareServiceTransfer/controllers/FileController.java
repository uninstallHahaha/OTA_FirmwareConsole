package com.jinju.FirmwareServiceTransfer.controllers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

@Controller
public class FileController {

    @Value("${firmwareFilePath}")
    String firmwareFilePath;

    @GetMapping("/download/{fileName}")
    @ResponseBody
    public void downloadLocal(HttpServletResponse response,
                              @PathVariable(name = "fileName")String fileName) throws Exception {
        /** 获取静态文件的路径 .*/
//        String path = ResourceUtils.getURL("classpath:").getPath() + "static/1.dat";
        String path = firmwareFilePath + fileName;

        /** 获取文件的名称 . */
//        String fileName = path.substring(path.lastIndexOf("/") +1);
        File file = new File(path);
        if (!file.exists()){
            System.out.println("路径有误，文件不存在！");
        }

        /** 将文件名称进行编码 */
        response.setHeader("content-disposition","attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
        response.setContentType("content-type:octet-stream");
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1){ /** 将流中内容写出去 .*/
            outputStream.write(buffer ,0 , len);
        }
        inputStream.close();
        outputStream.close();
        System.out.println("下载完了");
    }
}
