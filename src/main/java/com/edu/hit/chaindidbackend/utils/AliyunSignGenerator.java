package com.edu.hit.chaindidbackend.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AliyunSignGenerator {

    // 阿里云 Access Key Secret（你自己的密钥）
    private static final String ACCESS_KEY_SECRET = "your_access_key_secret";

    // API 请求的 HTTP 方法、请求路径和参数
    private static final String HTTP_METHOD = "GET";  // 或 "POST"
    private static final String REQUEST_PATH = "/api/v1/data";  // 请求路径（不包含域名）

    public static void main(String[] args) throws Exception {
        // 请求参数，通常来自于URL的查询字符串部分
        Map<String, String> params = new TreeMap<>();  // 使用 TreeMap 确保参数按字典顺序排序
        params.put("Action", "DescribeInstances");
        params.put("RegionId", "cn-hangzhou");
        params.put("Timestamp", "2025-01-17T10:00:00Z");

        // 拼接查询字符串
        String queryString = buildQueryString(params);

        // 拼接待签名的字符串
        String stringToSign = buildStringToSign(HTTP_METHOD, REQUEST_PATH, queryString);

        // 生成签名
        String signature = generateSignature(stringToSign);

        // 输出最终的签名
        System.out.println("Signature: " + signature);

        // 将签名添加到请求参数中
        params.put("Signature", signature);

        // 打印带签名的完整请求URL
        String finalUrl = buildFinalUrl("https://api.aliyun.com", REQUEST_PATH, params);
        System.out.println("Final URL with Signature: " + finalUrl);
    }

    // 构建查询字符串，确保参数按字典顺序排序并进行 URL 编码
    private static String buildQueryString(Map<String, String> params) throws Exception {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(encode(entry.getKey()))
                    .append("=")
                    .append(encode(entry.getValue()));
        }
        return queryString.toString();
    }

    // 构建待签名的字符串
    private static String buildStringToSign(String method, String path, String queryString) {
        return method + "\n" + path + "\n" + queryString;
    }

    // 生成签名
    private static String generateSignature(String stringToSign) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec((ACCESS_KEY_SECRET + "&").getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKey);
        byte[] signedBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        // 返回 Base64 编码的签名
        return java.util.Base64.getEncoder().encodeToString(signedBytes);
    }

    // URL 编码
    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replaceAll("\\+", "%20") // 将 + 转换为 %20
                .replaceAll("%7E", "~");  // 将 ~ 转换为 ~
    }

    // 构建最终的请求 URL
    private static String buildFinalUrl(String baseUrl, String path, Map<String, String> params) throws Exception {
        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(path).append("?");
        finalUrl.append(buildQueryString(params));
        return finalUrl.toString();
    }
}
