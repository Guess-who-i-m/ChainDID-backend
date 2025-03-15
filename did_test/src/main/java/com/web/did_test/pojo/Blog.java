package com.web.did_test.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Blog {
    @TableId(value = "blog_id", type = IdType.AUTO)
    private Integer blogId;
    private String title;
    private String content;
    private String tag;
    private String type;
    private Integer queryId;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String image5;
    private String image6;
    private String image7;
    private String image8;
    private String image9;
}
