package com.web.did_test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.web.did_test.dto.BlogInfo;
import com.web.did_test.pojo.Blog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BlogMapper extends BaseMapper<Blog> {

    @Select("SELECT blog_id, title, tag FROM blog WHERE type = #{type} OR type = 'both'")
    @Results({
            @Result(property = "blogId", column = "blog_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "tag", column = "tag")
    })
    List<BlogInfo> selectBlogInfoList(@Param("type") String type);
}
