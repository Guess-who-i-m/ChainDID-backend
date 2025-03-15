package com.web.did_test.utils;

import com.tencentcloudapi.common.AbstractModel;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tdid.v20210519.TdidClient;
import com.tencentcloudapi.tdid.v20210519.models.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TDidUtil {

    // 设置secretID和secretKey
    private static final String TDID_ENDPOINT = "";
    private static final String TDID_ACCESS_ID = "";
    private static final String TDID_SECRET_KEY = "";
    private static final String TDID_REGION = "ap-beijing";

    // 用法输入dapId和希望存入DID文档中的属性键值对json字符串，得到DID
    // json字符串格式：{"key1":"value1", "key2":"value2", ...... }
    public static JsonNode createDid(Long dapId, String customAttribute){
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateTDidByHostRequest req = new CreateTDidByHostRequest();

            req.setDAPId(dapId);
            req.setCustomAttribute(customAttribute);

            // 返回的resp是一个CreateTDidByHostResponse的实例，与请求对象对应
            CreateTDidByHostResponse resp = client.CreateTDidByHost(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    // 用法：输入dapId和DID，获得DID文档
    public static JsonNode getDidDocument(Long dapId, String did){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            GetTDidDocumentRequest req = new GetTDidDocumentRequest();

            req.setDAPId(dapId);
            req.setDid(did);

            // 返回的resp是一个GetTDidDocumentResponse的实例，与请求对象对应
            GetTDidDocumentResponse resp = client.GetTDidDocument(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    // 用法：输入DID和权威机构名称进行验证
    public static JsonNode queryAuthInfo(Long dapId, String did, String name){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            QueryAuthorityInfoRequest req = new QueryAuthorityInfoRequest();

            req.setDAPId(dapId);
            req.setDid(did);
            req.setName(name);

            // 返回的resp是一个QueryAuthorityInfoResponse的实例，与请求对象对应
            QueryAuthorityInfoResponse resp = client.QueryAuthorityInfo(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {

            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    // 用法：通过num设定需要设置在DID文档中属性的个数
    // key是键值列表，键数量要与num一致
    // value是键值列表，键数量要与num一致
    public static JsonNode setDidContent(Long dapId, String did, int num, String [] key, String [] value){
        ObjectMapper mapper = new ObjectMapper();

        // 参数校验
        if (num <= 0) {
            return mapper.createObjectNode().put("error", "num必须大于0");
        }
        if (key == null || value == null || key.length != num || value.length != num) {
            return mapper.createObjectNode().put("error", "key或value数组长度不匹配");
        }

        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            SetTDidAttributeRequest req = new SetTDidAttributeRequest();

            // 开始设置请求参数
            req.setDAPId(dapId);
            req.setDid(did);

            // 第一组属性键值对
            DidAttribute[] didAttributes = new DidAttribute[num];

            int i = 0;
            for (i = 0 ; i < num ; i ++ ){
                DidAttribute didAttribute = new DidAttribute();
                didAttribute.setKey(key[i]);
                didAttribute.setVal(value[i]);
                didAttributes[i] = didAttribute;
            }
            req.setAttributes(didAttributes);

            // 返回的resp是一个SetTDidAttributeResponse的实例，与请求对象对应
            SetTDidAttributeResponse resp = client.SetTDidAttribute(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    // 用法：
    // 这里主要需要构建一个凭证命令行，包括：
    // cptId：Long，凭证模板编号
    // issuer：String，发证方的DID
    // expirationDate：String，格式必须是YYYY-MM-DD HH:MM:SS
    // claimJson：json格式的String，内容必须与凭证模板对应
    // type：String []，内容有["VerifiableCredential"]、["OperateCredential"]和["TempCredential"]
    public static JsonNode issueCredential(Long dapId, Long cptId, String issuerDid, String expirationDate, String claimJson, String [] type){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            IssueCredentialRequest req = new IssueCredentialRequest();

            // 构建请求参数
            req.setDAPId(dapId);

            // 构建凭证命令行
            CRDLArg cRDLArg1 = new CRDLArg();
            cRDLArg1.setCPTId(cptId);
            cRDLArg1.setIssuer(issuerDid);
            cRDLArg1.setExpirationDate(expirationDate);
            cRDLArg1.setClaimJson(claimJson);
            cRDLArg1.setType(type);
            req.setCRDLArg(cRDLArg1);


            // 返回的resp是一个IssueCredentialResponse的实例，与请求对象对应
            IssueCredentialResponse resp = client.IssueCredential(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    public static JsonNode verifyCredential(long dapId, long verifyType, String credentialData){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            VerifyCredentialsRequest req = new VerifyCredentialsRequest();
            req.setDAPId(dapId);
            req.setVerifyType(verifyType);
            req.setCredentialData(credentialData);

            // 返回的resp是一个VerifyCredentialsResponse的实例，与请求对象对应
            VerifyCredentialsResponse resp = client.VerifyCredentials(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    // 用法
    // 这里需要一个OperateCredential，OperateCredential需要使用issueCredential完成发布
    // 类型type为["OperateCredential"]，cptId为1，claimJson包括
    // action: "updateCredentialState"，固定键值对
    // orignCredential: 原始凭证的CredentialData，注意拼写orign
    // CredentialStatus: 一个表明待更新凭证状态的json格式包括三个字段
    //      id: 待更新状态凭证中的id
    //      issuer: 待更新状态的凭证的发证方
    //      status: 1或0, 1是有效，0是报废
    // 具体调用见测试代码。
    public static JsonNode updateCredentialState(long dapId, String operateCredential){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            UpdateCredentialStateRequest req = new UpdateCredentialStateRequest();

            // 构建请求参数
            req.setDAPId(dapId);
            req.setOperateCredential(operateCredential);

            // 返回的resp是一个UpdateCredentialStateResponse的实例，与请求对象对应
            UpdateCredentialStateResponse resp = client.UpdateCredentialState(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    public static JsonNode createDisclosedCredentialJson(long dapId, String CredentialData, String policyJson){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateDisclosedCredentialRequest req = new CreateDisclosedCredentialRequest();

            // 构建请求参数
            req.setDAPId(dapId);
            req.setCredentialData(CredentialData);
            req.setPolicyJson(policyJson);

            // 返回的resp是一个CreateDisclosedCredentialResponse的实例，与请求对象对应
            CreateDisclosedCredentialResponse resp = client.CreateDisclosedCredential(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

    public static JsonNode createDisclosedCredentialId(long dapId, String CredentialData, Long policyId){
        ObjectMapper mapper = new ObjectMapper();
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(TDID_ACCESS_ID, TDID_SECRET_KEY);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(TDID_ENDPOINT);

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TdidClient client = new TdidClient(cred, TDID_REGION, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateDisclosedCredentialRequest req = new CreateDisclosedCredentialRequest();

            // 构建请求参数
            req.setDAPId(dapId);
            req.setCredentialData(CredentialData);
            req.setPolicyId(policyId);

            // 返回的resp是一个CreateDisclosedCredentialResponse的实例，与请求对象对应
            CreateDisclosedCredentialResponse resp = client.CreateDisclosedCredential(req);

            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));

            // return AbstractModel.toJsonString(resp);
            return mapper.valueToTree(resp);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            // 返回异常信息（或自定义错误JSON）
            return mapper.createObjectNode()
                    .put("error", "Internal error: " + e.getMessage());
        }
    }

}
