package com.web.did_test.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.pojo.Student;
import com.web.did_test.service.*;
import com.web.did_test.utils.TDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final ItemsService itemsService;
    private final OrganizationService organizationService;
    private final StudentService studentService;
    private final QueryService queryService;
    private final QuestionService questionService;

    @PostMapping("/creStu")
    public R<Student> createStudent(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 定义从Json中解析出来的变量
        String name = null;
        String birthday = null;
        String phoneNum = null;
        String email = null;
        String firstYear = null;
        String hometown = null;
        String college = null;
        String major = null;
        String idNum = null;

        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            name = jsonNode.get("name").asText();
            birthday = jsonNode.get("birthday").asText();
            phoneNum = jsonNode.get("phoneNum").asText();
            email = jsonNode.get("email").asText();
            firstYear = jsonNode.get("firstYear").asText();
            hometown = jsonNode.get("hometown").asText();
            college = jsonNode.get("college").asText();
            major = jsonNode.get("major").asText();
            idNum = jsonNode.get("idNum").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
        // 使用 Map<String, Object> 构建 JSON 结构
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("birthday", birthday);
        map.put("phoneNum", phoneNum);
        map.put("email", email);
        map.put("firstYear", firstYear);
        map.put("hometown", hometown);
        map.put("college", college);
        map.put("major", major);

        // 将 Map 转换为 JSON 字符串
        String customAttribute = null;
        try {
            customAttribute = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode message = TDidUtil.createDid(222223L, customAttribute);

        String did = null;
        // 示例1：提取字段
        if (message.has("error")) {
            return R.error(message.get("error").asText());
        } else {
            // System.out.println(message);    // 打印可以看到jackson修改了腾讯云返回的json格式，具体来说就是全部变驼峰
            did = message.get("did").asText();
        }

        Student student = new Student();
        student.setName(name);
        student.setIdentityNum(idNum);
        student.setDid(did);

        try{
            if(studentService.save(student)){
                return R.successMsg(did);
            } else {
                return R.error("载入数据库失败");
            }
        } catch (Exception e) {
            return R.error(e.getMessage());
        }


    }
}
