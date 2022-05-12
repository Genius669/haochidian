package com.zhao.haochidian.controller;

import com.zhao.haochidian.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${haochidian.pic.path}")
    private String picPath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();

        File dir = new File(picPath);
        if (!dir.exists()) dir.mkdirs();

        try {
            file.transferTo(new File(picPath + uuid + suffix));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success(uuid + suffix);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try (
                FileInputStream fileInputStream = new FileInputStream(picPath + name);
                BufferedInputStream input = new BufferedInputStream(fileInputStream);
                ServletOutputStream outputStream = response.getOutputStream();
                BufferedOutputStream output = new BufferedOutputStream(outputStream)
        ) {
            response.setContentType("image/jpeg");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
