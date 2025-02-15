package com.web.did_test.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("question")
public class Question {
    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;

    @TableField("type")
    private QuestionType type;

    private String title;

    private Integer queryId;

    /**
     * 问题类型枚举（与数据库ENUM值严格对应）
     */
    public enum QuestionType {
        SINGLE_LINE_TEXT("单行文本"),
        MULTI_LINE_TEXT("多行文本"),
        DROPDOWN_SELECT("下拉选择"),
        DATE_PICKER("日期选择"),
        FILE_UPLOAD("文件上传");

        @EnumValue  // 标注枚举存储值
        private final String value;

        QuestionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
