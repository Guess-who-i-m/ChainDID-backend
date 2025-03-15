package com.web.did_test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CredInfo {

    private String vcName;                          // 凭证名称
    private String orgName;                         // 组织名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;               // 到期时间
    private String logo;                            // logo的URL
    private String credentialDataStr;               // 凭证数据

}
