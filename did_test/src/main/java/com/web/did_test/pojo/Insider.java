package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;


@Data
public class Insider {
    @TableId(value = "person_id", type = IdType.AUTO)
    private Integer personId;
    private String identityNum;
    private String did;
    private String name;

}
