package com.web.did_test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.dto.BlogInfo;
import com.web.did_test.mapper.BlogMapper;
import com.web.did_test.pojo.Blog;
import com.web.did_test.pojo.Query;
import com.web.did_test.service.BlogService;
import com.web.did_test.service.QueryService;
import com.web.did_test.utils.TDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final QueryService queryService;
    private final BlogMapper blogMapper;

    @PostMapping("/issueBlog")
    public R<Blog> issueBlog(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Blog blog = new Blog();

        try {
            JsonNode request = objectMapper.readTree(json);
            blog.setContent(request.get("content").asText());
            blog.setTitle(request.get("title").asText());
            blog.setTag(request.get("tag").asText());
            blog.setQueryId(request.get("queryId").asInt());
            if (request.has("image1")){
                blog.setImage1(request.get("image1").asText());
            } else {
                blog.setImage1(null);
            }
            if (request.has("image2")){
                blog.setImage2(request.get("image2").asText());
            } else {
                blog.setImage2(null);
            }
            if (request.has("image3")){
                blog.setImage3(request.get("image3").asText());
            } else {
                blog.setImage3(null);
            }
            if (request.has("image4")){
                blog.setImage4(request.get("image4").asText());
            } else {
                blog.setImage4(null);
            }
            if (request.has("image5")){
                blog.setImage5(request.get("image5").asText());
            } else {
                blog.setImage5(null);
            }
            if (request.has("image6")){
                blog.setImage6(request.get("image6").asText());
            } else {
                blog.setImage6(null);
            }
            if (request.has("image7")){
                blog.setImage7(request.get("image7").asText());
            } else {
                blog.setImage7(null);
            }
            if (request.has("image8")){
                blog.setImage8(request.get("image8").asText());
            } else {
                blog.setImage8(null);
            }
            if (request.has("image9")){
                blog.setImage9(request.get("image9").asText());
            } else {
                blog.setImage9(null);
            }
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        // 先查询是否已经创建过blog
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("query_id", blog.getQueryId());
        Blog hasBlog = blogService.getOne(blogQueryWrapper);
        if (hasBlog != null) {
            R.error("这个问卷已经有现成的blog了");
        }

        // 根据queryId查询type
        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", blog.getQueryId());
        Query query = queryService.getOne(queryWrapper);
        if (query == null){
            return R.error("没有在数据库中找到对应的问卷");
        }

        blog.setType(query.getType());

        try{
            if (blogService.save(blog)) {
                R<Blog> r = new R<>();
                r.setData(blog);
                r.setCode(1);
                r.setMsg("成功发布博客");
                return r;
            } else {
                return R.error("发布失败");
            }
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/getBlogsByDid")
    public R<List<BlogInfo>> getBlogsByDid(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        String did = null;

        try {
            JsonNode request = objectMapper.readTree(json);
            did = request.get("did").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode message = TDidUtil.getDidDocument(222223L, did);

        // 从json中提取document字段处理
        String document = message.get("document").asText();

        // 将字符串解析为JsonNode
        // 并继续提取document中的内容
        ObjectMapper mapper = new ObjectMapper();
        String type = null;
        try {
            JsonNode documentJson = mapper.readTree(document);
            type = documentJson.get("type").asText();
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        List<BlogInfo> blogList = blogMapper.selectBlogInfoList(type);

        return R.success(blogList);

    }

    @PostMapping("/getBlogById")
    public R<Blog> getBlogById(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        String blogId = null;
        try {
            JsonNode request = objectMapper.readTree(json);
            blogId = request.get("blogId").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        Blog blog = blogService.getOne(queryWrapper);

        return R.success(blog);
    }
}
