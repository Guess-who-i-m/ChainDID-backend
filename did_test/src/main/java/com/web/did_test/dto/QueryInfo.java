package com.web.did_test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryInfo {
    private Integer queryId;
    private String title;
    private String orgName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    private String photo;
    private String type;
    private String tag;
    private Integer blogId;
}
