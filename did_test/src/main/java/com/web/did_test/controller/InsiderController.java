package com.web.did_test.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.dto.CredInfo;
import com.web.did_test.dto.QuestionWithOption;
import com.web.did_test.pojo.Insider;
import com.web.did_test.pojo.Organization;
import com.web.did_test.pojo.Query;
import com.web.did_test.service.*;
import com.web.did_test.utils.TDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/insider")
@RequiredArgsConstructor
public class InsiderController {
    private final ItemService itemService;
    private final OrganizationService organizationService;
    private final InsiderService insiderService;
    private final QueryService queryService;
    private final QuestionService questionService;
    private final OutsiderService outsiderService;

    @PostMapping("/creInsider")
    public R<String> createInsider(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 定义从Json中解析出来的变量
        String name = null;
        String phoneNum = null;
        String idNum = null;

        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            name = jsonNode.get("name").asText();
            phoneNum = jsonNode.get("phoneNum").asText();
            idNum = jsonNode.get("idNum").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
        // 使用 Map<String, Object> 构建 JSON 结构
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phoneNum", phoneNum);
        map.put("type", "insider");

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

        Insider insider = new Insider();
        insider.setName(name);
        insider.setIdentityNum(idNum);
        insider.setDid(did);

        try{
            if(insiderService.save(insider)){
                return R.success(did);
            } else {
                return R.error("载入数据库失败");
            }
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/creSubDid")
    public R<String> createSubDid(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        String rootDid = null;
        String usage = null;
        Boolean phoneNum = null;

        // 获取请求中的信息
        try {
            JsonNode request = objectMapper.readTree(json);
            rootDid = request.get("rootDid").asText();
            usage = request.get("usage").asText();
            phoneNum = request.get("phoneNum").asBoolean();
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        // 查询根DID的DID文档，判断是否满足insider的要求
        JsonNode message = TDidUtil.getDidDocument(222223L, rootDid);

        // 从json中提取document字段处理
        String document = message.get("document").asText();

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        ObjectMapper mapper = new ObjectMapper();
        String type = null;
        String phoneNumStr = null;
        String name = null;
        try {
            JsonNode documentJson = mapper.readTree(document);
            type = documentJson.get("type").asText();
            if (type.equals("outsider")) {
                return R.error("检测根DID类型为校外，无创建子DID的权限");
            }
            if (phoneNum) {
                phoneNumStr = documentJson.get("phoneNum").asText();
            }
            name = documentJson.get("name").asText();
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
        // 使用 Map<String, Object> 构建 JSON 结构
        Map<String, Object> map = new HashMap<>();

        map.put("rootDid", rootDid);
        map.put("usage", usage);
        map.put("type", type);
        map.put("name", name);
        if (phoneNum){
            map.put("phoneNum", phoneNumStr);
        }
        String customAttribute = null;
        try {
            customAttribute = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        JsonNode didMessage = TDidUtil.createDid(222223L, customAttribute);

        String did = null;
        // 示例1：提取字段
        if (didMessage.has("error")) {
            return R.error(didMessage.get("error").asText());
        } else {
            // System.out.println(message);    // 打印可以看到jackson修改了腾讯云返回的json格式，具体来说就是全部变驼峰
            did = didMessage.get("did").asText();
        }

        R<String> r = new R<>();
        r.setCode(1);
        r.setData(did);
        r.add("usage", usage);
        return r;

    }

    @PostMapping("/getStudentCard")
    public R<CredInfo> getStudentCard(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        String rootDid = null;
        String name = null;
        String idNum = null;
        String enrollment = null;
        String duration = null;
        String faculty = null;
        String majority = null;
        String classNum = null;
        String gender = null;
        String nation = null;

        Long cptId = 2001L;
        String orgDid = "did:tdid:w1:0xff51a9ebfc5a79b57f729d3146f66709d993c0fc";                 // 学校的DID
        String vcName = "链证校园学生证";
        String vcLogo = "https://chain-did.oss-cn-beijing.aliyuncs.com/5b1c74b9-0046-467e-ab02-e0bbae8258c6.png";
        String school = "链证校园";

        // 设置时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String expireTime = null;

        try {
            JsonNode request = objectMapper.readTree(json);
            rootDid = request.get("rootDid").asText();
            name = request.get("name").asText();
            idNum = request.get("idNum").asText();
            enrollment = request.get("enrollment").asText();
            duration = request.get("duration").asText();
            faculty = request.get("faculty").asText();
            majority = request.get("majority").asText();
            classNum = request.get("class").asText();
            gender = request.get("gender").asText();
            nation = request.get("nation").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 查询根DID的DID文档，判断是否满足insider的要求
        JsonNode message = TDidUtil.getDidDocument(222223L, rootDid);

        // 从json中提取document字段处理
        String document = message.get("document").asText();

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode documentJson = mapper.readTree(document);
            String type = documentJson.get("type").asText();
            if (type.equals("outsider")) {
                return R.error("检测根DID类型为校外，无创建学生证明的权限");
            }
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        Map<String, Object> map = new HashMap<>();

        map.put("did", rootDid);
        map.put("name", name);
        map.put("idNum", idNum);
        map.put("enrollment", enrollment);
        map.put("duration", duration);
        map.put("faculty", faculty);
        map.put("majority", majority);
        map.put("classNum", classNum);
        map.put("gender", gender);
        map.put("nation", nation);
        map.put("issuerDid", orgDid);

        String claimJson = null;
        try {
            claimJson = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String[] type = {"VerifiableCredential"};

        // 新增：计算凭证过期时间 --------------------------------------
        LocalDateTime expireDateTime;
        try {
            // 1. 将 enrollment 和 duration 转换为整数
            int enrollYear = Integer.parseInt(enrollment);
            int durationYears = Integer.parseInt(duration);

            // 2. 计算毕业年份 (enrollment + duration)
            int expireYear = enrollYear + durationYears;

            // 3. 构建日期时间对象（7月1日 00:00:00）
            expireDateTime = LocalDateTime.of(
                    expireYear, 7, 1, 0, 0, 0
            );

            // 4. 格式化为 "YYYY-MM-DD HH:MM:SS"
            expireTime = expireDateTime.format(formatter);

        } catch (NumberFormatException e) {
            return R.error("入学年份或学制格式错误");
        }

        // 3. 调用
        JsonNode messageVC = TDidUtil.issueCredential(222223L, cptId, orgDid, expireTime, claimJson, type);

        if (messageVC.has("error")) {
            return R.error(messageVC.get("error").asText() + "(原始凭证)");
        }

        // 4. 构建OperateCredential
        JsonNode operateCredential = null;
        String credentialDataStr = null;
        try {
            // 提取原始凭证的CredentialData
            JsonNode credentialData = null;
            credentialDataStr = messageVC.get("credentialData").asText();

            try {
                credentialData = objectMapper.readTree(credentialDataStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map1 = new HashMap<>();
            Map<String, Object> subMap = new HashMap<>();

            String id = credentialData.get("id").asText();
            String issuerDid = credentialData.get("issuer").asText();

            subMap.put("id", id);
            subMap.put("issuer", issuerDid);
            subMap.put("status", 1);

            map1.put("action", "updateCredentialState");
            map1.put("orignCredential", credentialDataStr);
            map1.put("CredentialStatus", subMap);

            // 根据现在时间创建一个2分钟之后过期的expireTime
            ZonedDateTime nowInShanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime expireTime1 = nowInShanghai.plusMinutes(2);
            String expireTime1str = expireTime1.format(formatter);


            // 将 Map 转换为 JSON 字符串
            String claimJson1 = null;

            claimJson1 = objectMapper.writeValueAsString(map1);

            String[] type1 = {"OperateCredential"};

            operateCredential = TDidUtil.issueCredential(222223L, 1L, issuerDid, expireTime1str, claimJson1, type1);

            if (operateCredential.has("error")) {
                return R.error(operateCredential.get("error").asText() + "(操作凭证)");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第三步，使用操作凭证更新原始凭证状态
        String operateCredentialStr = operateCredential.get("credentialData").asText();

        JsonNode result = TDidUtil.updateCredentialState(222223L, operateCredentialStr);


        CredInfo credInfo = new CredInfo();
        credInfo.setVcName(vcName);
        credInfo.setLogo(vcLogo);
        credInfo.setExpireTime(expireDateTime);
        credInfo.setOrgName(school);
        credInfo.setCredentialDataStr(credentialDataStr);

        // 写一个提取判断结果并进一步判断的逻辑
        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                return R.success(credInfo);
            } else {
                return R.error("上链失败");
            }
        } else {
            if (result.has("error")) {
                return R.error("上链失败");
            } else {
                return R.error("未知错误");
            }
        }
    }

    @PostMapping("/getSubStudentCard")
    public R<CredInfo> getSubStudentCard(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        String did = null;
        String rootCredential = null;
        List<String> options = null;

        Long cptId = 2001L;
        String orgDid = "did:tdid:w1:0xff51a9ebfc5a79b57f729d3146f66709d993c0fc";                 // 学校的DID
        String vcName = "链证校园学生证";
        String vcLogo = "https://chain-did.oss-cn-beijing.aliyuncs.com/5b1c74b9-0046-467e-ab02-e0bbae8258c6.png";
        String school = "链证校园";

        // 设置时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 提取请求中的字段
        try {
            JsonNode request = objectMapper.readTree(json);

            did = request.get("did").asText();
            rootCredential = request.get("rootCredential").asText();

            // 处理QuestionWithOption列表
            if (request.has("options") && request.get("options").isArray()) {
                System.out.println("检测到选项列表" + request.get("options").asText());
                JavaType typeQuestion = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, String.class);
                options = objectMapper.readValue(request.get("options").traverse(), typeQuestion);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 提取原始凭证的CredentialData
        JsonNode credentialData = null;
        String expireTimeStr = null;

        try {
            credentialData = objectMapper.readTree(rootCredential);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        expireTimeStr = credentialData.get("expirationDate").asText();
        JsonNode credentialSubject = credentialData.get("credentialSubject");
        String claimJson = null;

        if (options != null) {
            Map<String, Object> map = null;
            map = new HashMap<>();
            map.put("did", did);
            for (String option : options) {
                map.put(option, credentialSubject.get(option).asText());
            }
            map.put("issuerDid", orgDid);
            try {
                claimJson = objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        String[] type = {"VerifiableCredential"};

        // 使用 ISO 格式器解析为 OffsetDateTime
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(expireTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // 如果需要 LocalDateTime（丢弃时区信息）
        LocalDateTime expireDateTime = offsetDateTime.toLocalDateTime();
        expireTimeStr = expireDateTime.format(formatter);

        // 3. 调用
        JsonNode messageVC = TDidUtil.issueCredential(222223L, cptId, orgDid, expireTimeStr, claimJson, type);

        if (messageVC.has("error")) {
            return R.error(messageVC.get("error").asText() + "(原始凭证)");
        }

        // 4. 构建OperateCredential
        JsonNode operateCredential = null;
        String credentialDataStr = null;
        try {
            // 提取原始凭证的CredentialData
            JsonNode credentialData1 = null;
            credentialDataStr = messageVC.get("credentialData").asText();

            try {
                credentialData1 = objectMapper.readTree(credentialDataStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map1 = new HashMap<>();
            Map<String, Object> subMap = new HashMap<>();

            String id = credentialData1.get("id").asText();
            String issuerDid = credentialData1.get("issuer").asText();

            subMap.put("id", id);
            subMap.put("issuer", issuerDid);
            subMap.put("status", 1);

            map1.put("action", "updateCredentialState");
            map1.put("orignCredential", credentialDataStr);
            map1.put("CredentialStatus", subMap);

            // 根据现在时间创建一个2分钟之后过期的expireTime
            ZonedDateTime nowInShanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime expireTime1 = nowInShanghai.plusMinutes(2);
            String expireTime1str = expireTime1.format(formatter);


            // 将 Map 转换为 JSON 字符串
            String claimJson1 = null;

            claimJson1 = objectMapper.writeValueAsString(map1);

            String[] type1 = {"OperateCredential"};

            operateCredential = TDidUtil.issueCredential(222223L, 1L, issuerDid, expireTime1str, claimJson1, type1);

            if (operateCredential.has("error")) {
                return R.error(operateCredential.get("error").asText() + "(操作凭证)");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第三步，使用操作凭证更新原始凭证状态
        String operateCredentialStr = operateCredential.get("credentialData").asText();

        JsonNode result = TDidUtil.updateCredentialState(222223L, operateCredentialStr);



        CredInfo credInfo = new CredInfo();
        credInfo.setVcName(vcName);
        credInfo.setLogo(vcLogo);
        credInfo.setExpireTime(expireDateTime);
        credInfo.setOrgName(school);
        credInfo.setCredentialDataStr(credentialDataStr);

        // 写一个提取判断结果并进一步判断的逻辑
        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                return R.success(credInfo);
            } else {
                return R.error("上链失败");
            }
        } else {
            if (result.has("error")) {
                return R.error("上链失败");
            } else {
                return R.error("未知错误");
            }
        }


    }

}
