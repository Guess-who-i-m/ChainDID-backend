package com.edu.hit.chaindidbackend.DID;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    public static void sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 获取响应码
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // 处理响应流...
    }

    public static void main(String[] args) throws Exception {
        // 使用前面生成的 finalUrl
        sendGetRequest("https://did.aliyuncs.com/?Action=CreateDID&DIDType=V1&RegionId=cn-hangzhou&Signature=vFX5kpafdP0W0TEN3UghuEeyUio%3D&SignatureMethod=HMAC-SHA1&SignatureVersion=1.0&Timestamp=2025-01-17T21%3A05%3A46.000Z");
    }
}
