package com.web.did_test.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.dto.CredInfo;
import com.web.did_test.dto.CredentialParam;
import com.web.did_test.pojo.Commit;
import com.web.did_test.pojo.Organization;
import com.web.did_test.pojo.Query;
import com.web.did_test.pojo.Question;
import com.web.did_test.service.*;
import com.web.did_test.utils.EncryptUtil;
import com.web.did_test.utils.OssUtil;
import com.web.did_test.utils.TDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/did")
@RequiredArgsConstructor
public class DidController {
    private final ItemService itemService;
    private final OrganizationService organizationService;
    private final InsiderService insiderService;
    private final QueryService queryService;
    private final QuestionService questionService;
    private final OutsiderService outsiderService;
    private final CommitService commitService;

    // cptId
    // 到期时间
    @RequestMapping("/getCredential")
    public R<CredInfo> getCredential(@RequestBody CredentialParam credentialParam) {
        // 从请求体json中提取参数
        Integer queryId = credentialParam.getQueryId();
        List<String> keyList = credentialParam.getKeyList();
        List<String> valueList = credentialParam.getValueList();
        String did = credentialParam.getDid();

        // 设置时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 声明遍历参数
        int i = 0;

        if (keyList.size() != valueList.size()) {
            return R.error("键值列表长度不匹配");
        }

        // 1. 通过queryId获取orgDid作为发行方的DID
        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", queryId);
        Query query = queryService.getOne(queryWrapper);

        String orgDid = query.getOrgDid();
        Long cptId = 2002L;
        String expireTime = query.getExpireTime().format(formatter);

        // 2. 构建凭证的claimJson
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("query_id", queryId);
        List<Question> questionList = questionService.list(questionQueryWrapper);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        // 暂时性的，统一使用基础凭证模板，添加发正方DID进入claimJson
        String key = null;
        map.put("issuerDid", orgDid);
        map.put("did", did);
        for (i = 0; i < keyList.size(); i++) {
            for (Question question : questionList) {
                if (keyList.get(i).equals(question.getTitle())) {
                    key = question.getTitleMap();
                }
            }
            // 将A与键的映射存入凭证中
            if (key != null) {
                map.put(key.toUpperCase(), keyList.get(i));
            }
            // 将a与值的映射存入凭证中
            map.put(key, valueList.get(i));
        }
        String claimJson = null;
        try {
            claimJson = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String [] type = {"VerifiableCredential"};

        // 3. 调用
        JsonNode message = TDidUtil.issueCredential(222223L,cptId, orgDid, expireTime, claimJson, type);

        if (message.has("error")) {
            return R.error(message.get("error").asText() + "(原始凭证)");
        }

        // 4. 构建OperateCredential
        JsonNode operateCredential = null;
        String credentialDataStr = null;
        try {
            // 提取原始凭证的CredentialData
            JsonNode credentialData = null;
            credentialDataStr = message.get("credentialData").asText();

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

            String [] type1 = {"OperateCredential"};

            operateCredential = TDidUtil.issueCredential(222223L,1L, issuerDid, expireTime1str, claimJson1, type1);

            if (operateCredential.has("error")){
                return R.error(operateCredential.get("error").asText()  + "(操作凭证)");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第三步，使用操作凭证更新原始凭证状态
        String operateCredentialStr = operateCredential.get("credentialData").asText();

        JsonNode result = TDidUtil.updateCredentialState(222223L, operateCredentialStr);

        QueryWrapper<Organization> orgQueryWrapper = new QueryWrapper<>();
        orgQueryWrapper.eq("did", orgDid);
        Organization organization = organizationService.getOne(orgQueryWrapper);

        CredInfo credInfo = new CredInfo();
        credInfo.setVcName(query.getVcName());
        credInfo.setLogo(query.getLogo());
        credInfo.setExpireTime(query.getExpireTime());
        credInfo.setOrgName(organization.getOrgName());
        credInfo.setCredentialDataStr(credentialDataStr);

        // 记录填写信息
        JsonNode documentStr = TDidUtil.getDidDocument(222223L, did);

        String document = documentStr.get("document").asText();
        System.out.println(document);

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        Commit commit = new Commit();
        try {
            JsonNode documentJson = objectMapper.readTree(document);

            if (documentJson.has("name")) {
                commit.setName(documentJson.get("name").asText());
            } else {
                commit.setName(null);
            }

            if (documentJson.has("phoneNum")) {
                commit.setPhoneNum(documentJson.get("phoneNum").asText());
            } else {
                commit.setPhoneNum(null);
            }

            if (documentJson.has("type")) {
                commit.setType(documentJson.get("type").asText());
            } else {
                commit.setType(null);
            }

            commit.setDid(did);
            commit.setQueryId(queryId);
            commit.setAccount(query.getAccount());

            // 根据现在时间创建一个2分钟之后过期的expireTime
            ZonedDateTime nowInShanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

            commit.setCreateTime(nowInShanghai.toLocalDateTime());

            commitService.save(commit);


        } catch (JsonProcessingException e) {
            return R.error("添加提交信息失败");
        }

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

    @RequestMapping("/verifyCredential")
    public R<String> verifyCredential(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        String did = null;
        String credential = null;

        try {
            JsonNode request = objectMapper.readTree(json);
            did = request.get("did").asText();
            credential = request.get("credential").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 提取凭证内容中存储的did，和本人提交的did比对
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode credentialJson = mapper.readTree(credential);
//            System.out.println(credentialJson);
            JsonNode credentialSubject = credentialJson.get("credentialSubject");
//            System.out.println(credentialSubject);
            String didFromCredential = credentialSubject.get("did").asText();

            if (!did.equals(didFromCredential)) {
                return R.error("持有方DID不一致");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode result = TDidUtil.verifyCredential(222223L, 1L, credential);
        // 写一个提取判断结果并进一步判断的逻辑
        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                return R.successMsg("核验成功");
            } else {
                return R.error("核验失败1");
            }
        } else {
            if (result.has("error")) {
                return R.error("核验失败2");
            }
        }
        return R.error("核验失败2");
    }

    @PostMapping("/verifyQrcode")
    public R<String> verifyQrcode(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        String key = null;
        String url = null;
        Long   timestamp = null;

        System.out.println(json);

        try {
            JsonNode request = objectMapper.readTree(json);
            key = request.get("key").asText();
            url = request.get("url").asText();
            timestamp = request.get("timestamp").asLong();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String objectName = null;

        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // 获取路径部分: /credential_1741243763464.txt
            objectName = Paths.get(path).getFileName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String originContent = OssUtil.readFile(objectName);
        String content = null;

        try {
            content = EncryptUtil.decrypt(key, originContent, timestamp);
        } catch (Exception e) {
            return R.error("加密认证未通过");
        }

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        String did = null;
        String credential = null;
        try {
            JsonNode documentJson = objectMapper.readTree(content);
            did = documentJson.get("did").asText();
            credential = documentJson.get("credential").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        // 提取凭证内容中存储的did，和本人提交的did比对
//        try {
//            JsonNode credentialJson = objectMapper.readTree(credential);
////            System.out.println(credentialJson);
//            JsonNode credentialSubject = credentialJson.get("credentialSubject");
////            System.out.println(credentialSubject);
//            String didFromCredential = credentialSubject.get("did").asText();
//
//            if (!did.equals(didFromCredential)) {
//                return R.error("持有方DID不一致");
//            }
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        // 创建一个Map来存储键值对
        Map<String, Object> subjectMap = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields;
        JsonNode credentialJson = null;
        JsonNode credentialSubject = null;
        try {
            credentialJson = objectMapper.readTree(credential);
            credentialSubject = credentialJson.get("credentialSubject");



            // 遍历credentialSubject的所有字段
            fields = credentialSubject.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key1 = entry.getKey();
                JsonNode valueNode = entry.getValue();

                // 根据值的类型转换并存入Map
                if (valueNode.isTextual()) {
                    subjectMap.put(key1, valueNode.asText());
                } else if (valueNode.isNumber()) {
                    subjectMap.put(key1, valueNode.numberValue());
                } else if (valueNode.isBoolean()) {
                    subjectMap.put(key1, valueNode.asBoolean());
                }
                // 可以根据需要添加更多类型判断
                else {
                    subjectMap.put(key1, valueNode.toString());
                }
            }

            // 现在subjectMap包含了所有键值对，可以用作后续逻辑
            String didFromCredential = credentialSubject.get("did").asText();
            if (!did.equals(didFromCredential)) {
                return R.error("持有方DID不一致");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode result = TDidUtil.verifyCredential(222223L, 1L, credential);
        // 写一个提取判断结果并进一步判断的逻辑
        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                // 后处理一下问卷型的凭证
                if (credentialSubject.has("A") && credentialSubject.has("a")) {
                    Map<String, Object> resultMap = new HashMap<>();

                    // 用于记录已处理过的小写key，避免重复处理
                    Set<String> processedKeys = new HashSet<>();

                    for (Map.Entry<String, Object> entry : subjectMap.entrySet()) {
                        String key2 = entry.getKey();

                        // 只处理单个字母的key
                        if (key2.length() == 1 && Character.isUpperCase(key2.charAt(0)) ) {
                            char keyChar = key2.charAt(0);

                            // 处理大写字母作为新map的key
                            if (Character.isUpperCase(keyChar)) {
                                String lowerKey = String.valueOf(Character.toLowerCase(keyChar));

                                if (subjectMap.containsKey(lowerKey) && !processedKeys.contains(lowerKey)) {
                                    Object newKey = entry.getValue();
                                    Object newValue = subjectMap.get(lowerKey);

                                    // 类型安全转换
                                    if (newKey != null && newValue != null) {
                                        resultMap.put(newKey.toString(), newValue);
                                        processedKeys.add(lowerKey);
                                    }
                                }
                            }
                        }
                    }
                    subjectMap = resultMap;
                    resultMap.put("did", credentialSubject.get("did").asText());
                    resultMap.put("issuerDid", credentialSubject.get("issuerDid").asText());
                }

                R<String> r = new R<>();
                r.setCode(1);
                r.setMsg("核验成功");
                r.setMap(subjectMap);

                return r;
            } else {
                return R.error("核验失败1");
            }
        } else {
            if (result.has("error")) {
                return R.error("核验失败2");
            }
        }
        return R.error("核验失败2");

    }




    @PostMapping("/getDisclosedCredential")
    public R<String> getDisclosedCredential(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        String originCredential = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<String> keyList = new ArrayList<>();
        List<Integer> policyList = new ArrayList<>();

        JsonNode request = null;

        try {
            request = objectMapper.readTree(json);
            originCredential = request.get("originCredential").asText();

            if (request.has("keyList")) {
                JavaType typeKeyList = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, String.class);
                keyList = objectMapper.readValue(request.get("keyList").traverse(), typeKeyList);
            }

            if (request.has("policyList")) {
                JavaType typePolicyList = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, Integer.class);
                policyList = objectMapper.readValue(request.get("policyList").traverse(), typePolicyList);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (keyList.size() != policyList.size()) {
            return R.error("字段列表与披露策略列表不一致");
        }

        int i = 0;

        // 使用 Map<String, Object> 构建 JSON 结构
        Map<String, Object> map = new HashMap<>();

        for (i = 0; i < keyList.size(); i++) {
            map.put(keyList.get(i), policyList.get(i));
        }

        // 将 Map 转换为 JSON 字符串
        String policyJson = null;

        try {
            policyJson = objectMapper.writeValueAsString(map);
            System.out.println(policyJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode message = TDidUtil.createDisclosedCredentialJson(222223L, originCredential, policyJson);

        if (message.has("error")) {
            return R.error(message.get("error").asText() + "(原始凭证)");
        }

        // 4. 构建OperateCredential
        JsonNode operateCredential = null;
        String credentialDataStr = null;
        try {
            // 提取原始凭证的CredentialData
            JsonNode credentialData = null;
            credentialDataStr = message.get("credentialData").asText();

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

            String [] type1 = {"OperateCredential"};

            operateCredential = TDidUtil.issueCredential(222223L,1L, issuerDid, expireTime1str, claimJson1, type1);

            if (operateCredential.has("error")){
                return R.error(operateCredential.get("error").asText()  + "(操作凭证)");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第三步，使用操作凭证更新原始凭证状态
        String operateCredentialStr = operateCredential.get("credentialData").asText();

        JsonNode result = TDidUtil.updateCredentialState(222223L, operateCredentialStr);

        // 写一个提取判断结果并进一步判断的逻辑
        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                return R.success(credentialDataStr);
            } else {
                return R.error("上链失败");
            }
        } else {
            if (result.has("error")) {
                return R.error("上链失败");
            }
        }

        return R.success(credentialDataStr);

    }
}
