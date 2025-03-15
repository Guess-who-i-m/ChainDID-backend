package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("query")
public class Query {
    @TableId(value = "query_id", type = IdType.AUTO)
    private Integer queryId;
    private String title;
    private String vcName;
    private String orgDid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;
    private String logo;
    private String type;
    private Integer account;
}
