package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("query")
public class Query {
    @TableId(value = "query_id", type = IdType.AUTO)
    private Integer queryId;
    private String title;
    @TableField(value = "type")
    private QueryType type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    private String orgDid;

    /**
     * 类型枚举定义
     */
    public enum QueryType {
        INFO_COLLECTION("信息收集"),
        ACTIVITY_REGISTRATION("活动报名");

        @EnumValue  // MyBatis-Plus枚举值注解
        private final String value;

        QueryType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
