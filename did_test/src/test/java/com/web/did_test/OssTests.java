package com.web.did_test;

import com.aliyun.oss.internal.OSSUtils;
import com.web.did_test.utils.OssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class OssTests {

    @Test
    void readFile(){
        String result = OssUtil.readFile("credential_1741243763464.txt");

        System.out.println(result);

    }

    @Test
    void time(){
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
    }
}
