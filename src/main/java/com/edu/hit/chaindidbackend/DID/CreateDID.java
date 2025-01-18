package com.edu.hit.chaindidbackend.DID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CreateDID {

    private static final String ACCESS_KEY_ID = "LTAI5tAyS6fshaa95uLKgfhn";  // 你的 Access Key ID
    private static final String ACCESS_KEY_SECRET = "Lioz3qhImiUnZixRggFVXjq6mVJIdC";  // 你的 Access Key Secret
    private static final String CLIENT_TOKEN = "1F9FD46B-8B7D-41C4-BC89-F09E68388D6A";  // 客户端令牌
    private static final String OWNER_UNIQUE_ID = "your_owner_unique_id";  // 用户唯一 ID
    private static final String API_URL = "https://baasdis.cn-hangzhou.aliyuncs.com";  // 阿里云 DID API 的 URL
    private static final String ACTION = "CreateTenantDID";  // 调用的操作
    private static final String VERSION = "2020-05-09";  // API 版本

    public static void main(String[] args) throws Exception {
        // 设置请求参数
        Map<String, String> params = new TreeMap<>();
        params.put("AccessKeyId", ACCESS_KEY_ID);
        params.put("Action", ACTION);
        params.put("ClientToken", CLIENT_TOKEN);
        params.put("Format", "JSON");
        params.put("OwnerUniqueID", OWNER_UNIQUE_ID);
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", UUID.randomUUID().toString());  // 随机生成 SignatureNonce
        params.put("SignatureVersion", "1.0");
        params.put("Timestamp", getCurrentTimestamp());  // 当前时间戳
        params.put("Version", VERSION);

        // 拼接查询字符串
        String queryString = buildQueryString(params);

        // 构建待签名字符串
        String stringToSign = buildStringToSign("GET", "/", queryString);

        // 生成签名
        String signature = generateSignature(stringToSign);

        // 将签名添加到参数中
        //params.put("Signature", signature);

        // 构建最终请求 URL
        String finalUrl = buildFinalUrl(API_URL, "/", params);

        finalUrl = finalUrl + "&Signature=" + signature;

        // 打印最终请求 URL
        System.out.println("Final URL: " + finalUrl);

        // 发送 GET 请求并获取响应
        sendGetRequest(finalUrl);
    }

    // 获取当前时间戳（UTC 格式）
    private static String getCurrentTimestamp() {
        Date date = new Date();
        return String.format("%tFT%<tT.000Z", date);  // 格式：yyyy-MM-dd'T'HH:mm:ss.000Z
    }

    // 构建查询字符串
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

    // 构建待签名字符串
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
                .replaceAll("\\+", "%20")  // 将 + 转换为 %20
                .replaceAll("%7E", "~");   // 将 ~ 转换为 ~
    }

    // 构建最终的请求 URL
    private static String buildFinalUrl(String baseUrl, String path, Map<String, String> params) throws Exception {
        StringBuilder finalUrl = new StringBuilder(baseUrl);
        finalUrl.append(path).append("?");
        finalUrl.append(buildQueryString(params));
        return finalUrl.toString();
    }

    // 发送 GET 请求并获取响应
    private static void sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 获取响应码
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // 输出响应头信息
        System.out.println("Response Headers:");
        for (String header : connection.getHeaderFields().keySet()) {
            System.out.println(header + ": " + connection.getHeaderField(header));
        }

        // 获取响应内容
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("Response Body: " + response.toString());
        } catch (Exception e) {
            System.out.println("Error reading the response body: " + e.getMessage());
        }
    }
}
