package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Organization {
    @TableId(value = "org_id", type = IdType.AUTO)
    private Integer orgId;
    private String orgName;
    private String did;
    private String orgLicence;
    private String logo;

}
