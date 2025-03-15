package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Commit {
    @TableId(value = "commit_id", type = IdType.AUTO)
    private Integer commitId;
    private String name;
    private String did;
    private String type;
    private String phoneNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    private Integer queryId;
    private Integer account;
}
