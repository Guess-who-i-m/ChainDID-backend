package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Item {
    @TableId(value = "item_id", type = IdType.AUTO)
    private Integer itemId;
    private String content;
    private Integer questionId;
}
