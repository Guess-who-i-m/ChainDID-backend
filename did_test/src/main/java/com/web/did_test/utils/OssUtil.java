package com.web.did_test.utils;

import java.io.*;
import java.util.Random;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

public class OssUtil {


    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY_SECRET = "";

    private static final String REGION = "cn-beijing";

    // 填写Bucket名称，例如examplebucket。
    private static final String BUCKET_NAME = "chain-did";

    public static String uploadFile(String objectName, InputStream in) throws Exception {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        String url = "";

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, in);

            // 上传字符串。
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            //url组成：https://bucket名称.区域节点/objectName
            url = "https://" + BUCKET_NAME  + "." + ENDPOINT.substring(ENDPOINT.lastIndexOf("/")+1) + "/" + objectName;

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return url;
    }

    public static String readFile(String objectName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        OSSObject ossObject = ossClient.getObject(BUCKET_NAME, objectName);
        InputStream contentStream = ossObject.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream));
        String line;
        StringBuilder result = new StringBuilder();
//        System.out.println("3. 下载的文件内容：");
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                } else {
//                    System.out.println(line);
                    result.append(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            contentStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }
}
