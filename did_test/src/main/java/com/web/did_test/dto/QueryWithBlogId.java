package com.web.did_test.dto;

import com.web.did_test.pojo.Query;
import lombok.Data;

@Data
public class QueryWithBlogId extends Query {
    private Integer blogId;
}
