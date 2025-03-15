package com.web.did_test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.web.did_test.pojo.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionWithOption {
    private List<String> options;
    @JsonFormat(shape = JsonFormat.Shape.STRING) // 按字符串解析
    private Question.QuestionType type;
    private String title;
//    private String titleMap;
    private Boolean required;
}
