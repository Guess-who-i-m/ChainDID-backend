package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Outsider {
    @TableId(value = "person_id", type = IdType.AUTO)
    private Integer personId;
    private String identityNum;
    private String did;
    private String name;
}