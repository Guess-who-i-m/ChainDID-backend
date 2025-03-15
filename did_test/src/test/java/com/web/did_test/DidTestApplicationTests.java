package com.web.did_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.boot.test.context.SpringBootTest;

import com.tencentcloudapi.common.AbstractModel;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tdid.v20210519.TdidClient;
import com.tencentcloudapi.tdid.v20210519.models.*;

import com.web.did_test.utils.TDidUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
class DidTestApplicationTests {


    @Test
    void contextLoads() {
        System.out.println("hello world");
    }


    // 输入：
    @Test
    void generateDid() {
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map = new HashMap<>();
            map.put("type", "test");

            // 将 Map 转换为 JSON 字符串
            String customAttribute = objectMapper.writeValueAsString(map);

            System.out.println(customAttribute);

            JsonNode message = TDidUtil.createDid(222223L, customAttribute);

            // 示例1：提取字段
            if (message.has("error")) {
                System.out.println("错误信息: " + message.get("error").asText());
            } else {
                // System.out.println(message);    // 打印可以看到jackson修改了腾讯云返回的json格式，具体来说就是全部变驼峰
                String name = message.get("did").asText();
                System.out.println("did: " + name);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    @Test
    void testGetDidDocument() {
        JsonNode message = TDidUtil.getDidDocument(222223L, "did:tdid:w1:0x1e5d677dce35858207f8ca6af2ab7e541d66bbd9");
        System.out.println(message);
        // 从json中提取document字段处理
        String document = message.get("document").asText();
        System.out.println(document);

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode documentJson = mapper.readTree(document);
            String type = documentJson.get("type").asText();
            System.out.println("type: " + type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testQueryAuthInfo() {
        JsonNode message = TDidUtil.queryAuthInfo(222223L, "did:tdid:w1:0x8ad750852661514cae2e8dda1b4587bd46072bcc", "哈工大图书馆");
        System.out.println(message);
        if (message.has("error")) {
            // 说明不是权威机构
            System.out.println("不是权威机构");
        } else {
            System.out.println("是权威机构");
        }
    }

    @Test
    void testSetDidContent(){
        // 调用方法：要设定的属性数量为num
        // key列表和value列表的数量和位置要匹配对应
        int num = 2;
        String [] key = {"name", "age"};
        String [] value = {"test", "12"};
        JsonNode message = TDidUtil.setDidContent(222223L, "did:tdid:w1:0x8ad750852661514cae2e8dda1b4587bd46072bcc", num, key, value );
        System.out.println(message);
    }

    @Test
    void testIssueCredential(){

        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map = new HashMap<>();
            map.put("stuName", "bin");
            map.put("stuNumber", "2022113586");
            map.put("stuDid", "did:tdid:w1:0x9da07c7471fd98a13dcad430427b7da9c633ff42");
            map.put("uniName", "HIT");

            // 将 Map 转换为 JSON 字符串
            String claimJson = null;

            claimJson = objectMapper.writeValueAsString(map);

            String [] type = {"test"};

            JsonNode message = TDidUtil.issueCredential(222223L,1000L, "did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33", "2025-02-16 12:00:00", claimJson, type);
            System.out.println(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testVerifyCredential(){
        String credentialData = "{\"cptId\":1000,\"issuer\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33\",\"expirationDate\":\"2025-02-16T12:00:00+08:00\",\"issuanceDate\":\"2025-02-14T19:54:06+08:00\",\"context\":\"https://github.com/TencentCloud-Blockchain/TDID/blob/main/context/v1\",\"id\":\"6e8f4289445ea238e2f28f8d16b76fcd\",\"type\":[\"VerifiableCredential\",\"test\"],\"credentialSubject\":{\"stuDid\":\"did:tdid:w1:0x9da07c7471fd98a13dcad430427b7da9c633ff42\",\"stuName\":\"bin\",\"stuNumber\":\"2022113586\",\"uniName\":\"HIT\"},\"proof\":{\"created\":\"2025-02-14T19:54:06+08:00\",\"creator\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33#keys-0\",\"signatureValue\":\"MEUCIQD5Wsrq5foNyjauWTqhq5NnaZJzaVLFQAH3Cb/uEu/RqAIgSpE5DCVUmtoK/LYNvZd+2OT20cHqqUgVIoo86pz7Cbk=\",\"type\":\"Sm2p256v1\",\"metaDigest\":\"c5c188ae4510deedcd5c59f17da271d54b3190b8f46c18b3480db1d8c39e1095\",\"vcDigest\":\"05408a8a2bd24695af736473fda69ad89bb3e2fe48dcf5be4f5f92a5a77557c4\",\"privacy\":\"Public\",\"salt\":{\"stuDid\":\"5oFrr\",\"stuName\":\"oltuC\",\"stuNumber\":\"YfG0z\",\"uniName\":\"jj16q\"}}}";

        JsonNode message = TDidUtil.verifyCredential(222223L, 0L, credentialData);
        System.out.println(message);

        boolean result = message.get("result").asBoolean();

        if (result) {
            System.out.println("验证通过");
        } else {
            System.out.println("验证未通过");
        }
    }

    // 这个测试案例中不单独测试更新凭证链上状态接口，同时给一个完整的颁发凭证、上链到核验的过程。
    @Test
    void testUpdateCredentialState(){

        JsonNode message = null;

        // 第一步，创建一个VC
        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map = new HashMap<>();
            map.put("stuName", "bin");
            map.put("stuNumber", "2022113586");
            map.put("stuDid", "did:tdid:w1:0x9da07c7471fd98a13dcad430427b7da9c633ff42");
            map.put("uniName", "HIT");

            // 将 Map 转换为 JSON 字符串
            String claimJson = null;

            claimJson = objectMapper.writeValueAsString(map);

            String [] type = {"VerifiableCredential"};

            message = TDidUtil.issueCredential(222223L,1000L, "did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33", "2025-02-18 12:00:00", claimJson, type);
            System.out.println("原始凭证：" + message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第二步，构建OperateCredential
        JsonNode operateCredential = null;
        String credentialDataStr = null;
        try {
            // 提取原始凭证的CredentialData
            JsonNode credentialData = null;
            credentialDataStr = message.get("credentialData").asText();

            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                credentialData = objectMapper.readTree(credentialDataStr);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
            // 使用 Map<String, Object> 构建 JSON 结构
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> subMap = new HashMap<>();

            String id = credentialData.get("id").asText();
            String issuerDid = credentialData.get("issuer").asText();

            subMap.put("id", id);
            subMap.put("issuer", issuerDid);
            subMap.put("status", 1);

            map.put("action", "updateCredentialState");
            map.put("orignCredential", credentialDataStr);
            map.put("CredentialStatus", subMap);


            // 将 Map 转换为 JSON 字符串
            String claimJson = null;

            claimJson = objectMapper.writeValueAsString(map);

            String [] type = {"OperateCredential"};

            operateCredential = TDidUtil.issueCredential(222223L,1L, issuerDid, "2025-02-18 12:00:00", claimJson, type);
            System.out.println("操作凭证：" + operateCredential);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 第三步，使用操作凭证更新原始凭证状态
        String operateCredentialStr = operateCredential.get("credentialData").asText();

        JsonNode result = TDidUtil.updateCredentialState(222223L, operateCredentialStr);
        System.out.println(result);



        if (result.has("result")) {
            if (result.get("result").asBoolean()) {
                System.out.println("上链成功");
            }
        } else {
            if (result.has("error")) {
                System.out.println("上链失败");
            }
        }

        // 第四步，进行线上核验
        JsonNode judge = TDidUtil.verifyCredential(222223L, 1L, credentialDataStr);

    }

    @Test
    void testCreateDisclosedCredentialJson(){

        String credentialData = "{\"cptId\":1000,\"issuer\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33\",\"expirationDate\":\"2025-02-16T12:00:00+08:00\",\"issuanceDate\":\"2025-02-14T19:54:06+08:00\",\"context\":\"https://github.com/TencentCloud-Blockchain/TDID/blob/main/context/v1\",\"id\":\"6e8f4289445ea238e2f28f8d16b76fcd\",\"type\":[\"VerifiableCredential\",\"test\"],\"credentialSubject\":{\"stuDid\":\"did:tdid:w1:0x9da07c7471fd98a13dcad430427b7da9c633ff42\",\"stuName\":\"bin\",\"stuNumber\":\"2022113586\",\"uniName\":\"HIT\"},\"proof\":{\"created\":\"2025-02-14T19:54:06+08:00\",\"creator\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33#keys-0\",\"signatureValue\":\"MEUCIQD5Wsrq5foNyjauWTqhq5NnaZJzaVLFQAH3Cb/uEu/RqAIgSpE5DCVUmtoK/LYNvZd+2OT20cHqqUgVIoo86pz7Cbk=\",\"type\":\"Sm2p256v1\",\"metaDigest\":\"c5c188ae4510deedcd5c59f17da271d54b3190b8f46c18b3480db1d8c39e1095\",\"vcDigest\":\"05408a8a2bd24695af736473fda69ad89bb3e2fe48dcf5be4f5f92a5a77557c4\",\"privacy\":\"Public\",\"salt\":{\"stuDid\":\"5oFrr\",\"stuName\":\"oltuC\",\"stuNumber\":\"YfG0z\",\"uniName\":\"jj16q\"}}}";

        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 创建一个简单的 Map 或者 POJO 对象来表示 JSON 数据
        // 使用 Map<String, Object> 构建 JSON 结构
        Map<String, Object> map = new HashMap<>();
        map.put("stuName", 1);
        map.put("stuNumber", 1);
        map.put("stuDid", 0);
        map.put("uniName", 0);
        // 这个policyJson意味着只展示姓名和学号

        // 将 Map 转换为 JSON 字符串
        String policyJson = null;

        try {
            policyJson = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode message = TDidUtil.createDisclosedCredentialJson(222223L, credentialData, policyJson);
        System.out.println(message);
    }

    @Test
    void testCreateDisclosedCredentialId(){
        String credentialData = "{\"cptId\":1000,\"issuer\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33\",\"expirationDate\":\"2025-02-16T12:00:00+08:00\",\"issuanceDate\":\"2025-02-14T19:54:06+08:00\",\"context\":\"https://github.com/TencentCloud-Blockchain/TDID/blob/main/context/v1\",\"id\":\"6e8f4289445ea238e2f28f8d16b76fcd\",\"type\":[\"VerifiableCredential\",\"test\"],\"credentialSubject\":{\"stuDid\":\"did:tdid:w1:0x9da07c7471fd98a13dcad430427b7da9c633ff42\",\"stuName\":\"bin\",\"stuNumber\":\"2022113586\",\"uniName\":\"HIT\"},\"proof\":{\"created\":\"2025-02-14T19:54:06+08:00\",\"creator\":\"did:tdid:w1:0x76753955002ceb77dad212695ab67c683b5d7e33#keys-0\",\"signatureValue\":\"MEUCIQD5Wsrq5foNyjauWTqhq5NnaZJzaVLFQAH3Cb/uEu/RqAIgSpE5DCVUmtoK/LYNvZd+2OT20cHqqUgVIoo86pz7Cbk=\",\"type\":\"Sm2p256v1\",\"metaDigest\":\"c5c188ae4510deedcd5c59f17da271d54b3190b8f46c18b3480db1d8c39e1095\",\"vcDigest\":\"05408a8a2bd24695af736473fda69ad89bb3e2fe48dcf5be4f5f92a5a77557c4\",\"privacy\":\"Public\",\"salt\":{\"stuDid\":\"5oFrr\",\"stuName\":\"oltuC\",\"stuNumber\":\"YfG0z\",\"uniName\":\"jj16q\"}}}";
        // 要看控制台中的policyId和凭证cptId能匹配
        JsonNode message = TDidUtil.createDisclosedCredentialId(222223L, credentialData, 1004L);
        System.out.println(message);
    }

}
