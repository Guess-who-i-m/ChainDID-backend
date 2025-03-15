package com.web.did_test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.did_test.common.R;
import com.web.did_test.utils.OssUtil;
import com.web.did_test.utils.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        //把文件内容存在本地磁盘上
        String originalFilename = file.getOriginalFilename();
        //保证文件名字唯一防止覆盖
        String filename = null;
        if (originalFilename != null) {
            filename = UUID.randomUUID().toString()+originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String url = null;
        try {
            url = OssUtil.uploadFile(filename, file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return R.success(url);
    }


    @PostMapping("/uploadJson")
    public R<String> uploadJson(@RequestBody String json) {
        ObjectMapper mapper = new ObjectMapper();
        String originalCredential = null;
        String did = null;

        try {
            // 解析JSON获取凭证
            JsonNode request = mapper.readTree(json);
            originalCredential = request.get("credential").asText();
            did = request.get("did").asText();

            Map<String,Object> originMap = new HashMap<>();
            originMap.put("credential",originalCredential);
            originMap.put("did",did);

            String jsonString = mapper.writeValueAsString(originMap);

            // 1. 生成基于时间戳的密钥
            long timestamp = System.currentTimeMillis();
            SecretKey secretKey = EncryptUtil.generateTimeBasedKey(timestamp);

            System.out.println(timestamp);

            // 2. 加密凭证
            EncryptUtil.EncryptionResult encryptionResult = EncryptUtil.encryptString(jsonString, secretKey);

            // 3. 这里可以添加文件上传逻辑（例如上传到云存储）
            String fileContent = encryptionResult.getEncryptedText();
            byte[] fileBytes = fileContent.getBytes(StandardCharsets.UTF_8);

            InputStream in = new ByteArrayInputStream(fileBytes);

            // 4. 直接上传到云存储
            String cloudPath = "credential_" + timestamp + ".txt";

            String url = OssUtil.uploadFile(cloudPath, in);



            Map<String, Object> map = new HashMap<>();
            map.put("key", encryptionResult.getIv());
            map.put("url", url);
            map.put("timestamp", timestamp);

            R<String> r = new R<>();
            r.setMsg("成功上传");
            r.setMap(map);
            r.setCode(1);


            return r;

        } catch (JsonProcessingException e) {
            return R.error("JSON解析错误: " + e.getMessage());
        } catch (Exception e) {
            return R.error("处理失败: " + e.getMessage());
        }
    }


}
