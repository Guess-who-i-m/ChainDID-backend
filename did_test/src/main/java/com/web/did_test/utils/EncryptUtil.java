package com.web.did_test.utils;

import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptUtil {

    public static SecretKey generateTimeBasedKey(long timestamp) throws NoSuchAlgorithmException {
        // 使用SHA-256从时间戳生成密钥
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static EncryptionResult encryptString(String plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // 获取初始化向量
        IvParameterSpec iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class);

        // 执行加密
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        return new EncryptionResult(
                Base64.getEncoder().encodeToString(encryptedBytes),
                Base64.getEncoder().encodeToString(iv.getIV())
        );
    }

    public static String saveToFile(EncryptionResult result, long timestamp) throws Exception {
        // 构造文件内容：IV + 加密文本
        // result.getIv() + ":" +
        String fileContent = result.getEncryptedText();

        // 生成文件名
        String fileName = "credential_" + timestamp + ".txt";

        // 写入文件
        Path path = Paths.get(fileName);
        Files.writeString(path, fileContent);

        return fileName;
    }

    /**
     * 解密方法
     * @param ivBase64        Base64编码的初始化向量
     * @param encryptedText   Base64编码的加密文本
     * @param timestamp       加密时使用的时间戳（用于密钥生成）
     * @return 解密后的原始字符串
     */
    public static String decrypt(String ivBase64, String encryptedText, long timestamp) throws Exception {
        // 1. 将时间戳转换为密钥（需与加密时相同逻辑）
        SecretKeySpec secretKey = generateKeyFromTimestamp(timestamp);

        // 2. Base64解码输入参数
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        // 3. 初始化密码器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

        // 4. 执行解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static SecretKeySpec generateKeyFromTimestamp(long timestamp) throws Exception {
        // 与加密完全一致的密钥生成逻辑
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(String.valueOf(timestamp).getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }


    // 加密结果封装类
    @Getter
    public static class EncryptionResult {
        private final String encryptedText;
        private final String iv;

        public EncryptionResult(String encryptedText, String iv) {
            this.encryptedText = encryptedText;
            this.iv = iv;
        }

    }
}
