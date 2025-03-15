package com.web.did_test.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CredentialParam {
    private Integer queryId;
    private List<String> keyList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();
    private String did;
}
