package com.web.did_test.dto;

import lombok.Data;

import java.util.List;

@Data
public class QueryDetail {
    Integer blogId;
    String blogTitle;
    String content;
    String image1;
    String image2;
    String image3;
    String image4;
    String image5;
    String image6;
    String image7;
    String image8;
    String image9;
    Integer queryId;
    private String queryTitle;
    private List<QuestionWithOption> result;
}
