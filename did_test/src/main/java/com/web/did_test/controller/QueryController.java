package com.web.did_test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.dto.QueryDetail;
import com.web.did_test.dto.QueryInfo;
import com.web.did_test.dto.QueryWithBlogId;
import com.web.did_test.dto.QuestionWithOption;
import com.web.did_test.pojo.*;
import com.web.did_test.service.*;
import com.web.did_test.utils.TDidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.web.did_test.common.R;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
public class QueryController {
    private final ItemService itemService;
    private final OrganizationService organizationService;
    private final InsiderService insiderService;
    private final QueryService queryService;
    private final QuestionService questionService;
    private final OutsiderService outsiderService;
    private final BlogService blogService;

    @PostMapping("/getQueries")
    public R<List<Query>> getQueries(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = null;

        String did = null;

        try {
            jsonNode = objectMapper.readTree(json);
            did = jsonNode.get("did").asText();
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

        QueryWrapper<Query> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type).or().eq("type", "both");
        List<Query> queryList = queryService.list(wrapper);
        return R.success(queryList);
    }

    @PostMapping("getQueriesByDid")
    public R<List<QueryWithBlogId>> getQueriesByDid(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        String did = null;

        // 从json数据中获取did
        try {
            jsonNode = objectMapper.readTree(json);
            did = jsonNode.get("did").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("org_did", did);
        List<Query> queryList = queryService.list(queryWrapper);

        List<QueryWithBlogId> queryWithBlogIdList = new ArrayList<>();

        for (Query query : queryList) {
            QueryWithBlogId queryWithBlogId = new QueryWithBlogId();

            QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
            blogQueryWrapper.eq("query_id", query.getQueryId());
            Blog blog = blogService.getOne(blogQueryWrapper);

            queryWithBlogId.setQueryId(query.getQueryId());
            queryWithBlogId.setTitle(query.getTitle());
            queryWithBlogId.setVcName(query.getVcName());
            queryWithBlogId.setOrgDid(query.getOrgDid());
            queryWithBlogId.setDeadline(query.getDeadline());
            queryWithBlogId.setExpireTime(query.getExpireTime());
            queryWithBlogId.setLogo(query.getLogo());
            queryWithBlogId.setType(query.getType());

            if (blog != null) {
                queryWithBlogId.setBlogId(blog.getBlogId());
            } else {
                queryWithBlogId.setBlogId(-1);
            }
            queryWithBlogIdList.add(queryWithBlogId);
        }

        return R.success(queryWithBlogIdList);

    }

    @PostMapping("/issueQuery")
    public R<Integer> issueQuery(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        // 用于处理时间格式
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // ISO格式解析器
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 目标格式

        String title = null;        // 问卷标题
        String vcName = null;       // 凭证标题
        String issuerDid = null;    // 发布凭证的组织的DID
        String logo = null;
        String type = null;

        // 处理问卷截止时间
        String deadline = null;
        String deadlineStr = null;
        LocalDateTime deadlineL = null;

        // 处理凭证过期时间
        String expireTime = null;
        String expireTimeStr = null;
        LocalDateTime expireTimeL = null;
        List<QuestionWithOption> questions = null;

        // 处理账户额度
        Integer account = -1;

        try {
            JsonNode request = objectMapper.readTree(json);

            title = request.get("title").asText();
            vcName = request.get("vcName").asText();
            issuerDid = request.get("issuerDid").asText();
            logo = request.get("logo").asText();
            type = request.get("type").asText();

            // 处理时间格式转换
            if (request.has("deadline")) {
                deadlineStr = request.get("deadline").asText();

                // 解析ISO时间（包含时区信息）
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(deadlineStr, isoFormatter);

                // 转换为系统默认时区（可选步骤，根据需求决定是否保留）
                LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

                // 格式化为目标字符串
                deadline = localDateTime.format(targetFormatter);

                deadlineL = LocalDateTime.parse(deadline, targetFormatter);
            }

            // 处理时间格式转换
            if (request.has("expireTime")) {
                expireTimeStr = request.get("expireTime").asText();

                // 解析ISO时间（包含时区信息）
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(expireTimeStr, isoFormatter);

                // 转换为系统默认时区（可选步骤，根据需求决定是否保留）
                LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

                // 格式化为目标字符串
                expireTime = localDateTime.format(targetFormatter);

                expireTimeL = LocalDateTime.parse(expireTime, targetFormatter);
            }

            // 处理QuestionWithOption列表
            if (request.has("fields") && request.get("fields").isArray()) {
                System.out.println("检测到问题列表" + request.get("fields").asText());
                JavaType typeQuestion = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, QuestionWithOption.class);
                questions = objectMapper.readValue(request.get("fields").traverse(), typeQuestion);
            }

            account = request.get("account").asInt();

        } catch (DateTimeParseException | IOException | IllegalArgumentException e) {
            return R.error(e.getMessage());
        }

        Query query = new Query();

        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title).eq("org_did", issuerDid);
        query = queryService.getOne(queryWrapper);

        if (query != null) {
            return R.error("已发布过相同名称的问卷");
        }

        // 首先检查orgDID
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("did", issuerDid);
        try{
            Organization organization = organizationService.getOne(organizationQueryWrapper);

            if (organization == null) {
                return R.error("你输入的DID尚不具备机构资质");
            }
        } catch (Exception e){
            return R.error(e.getMessage());
        }

        Query newQuery = new Query();
        newQuery.setTitle(title);
        newQuery.setVcName(vcName);
        newQuery.setOrgDid(issuerDid);
        newQuery.setDeadline(deadlineL);
        newQuery.setExpireTime(expireTimeL);
        newQuery.setLogo(logo);
        newQuery.setType(type);
        newQuery.setAccount(account);

        try{
            // 将newQuery添加到数据库
            if (!queryService.save(newQuery)) {
                return R.error("问卷信息插入数据库失败");
            }
        } catch (Exception e){
            return R.error(e.getMessage());
        }

        Integer queryId = newQuery.getQueryId();

        // 根据queryId设定每一道题目
        if (questions != null) {
            char map = 'a';
            for (QuestionWithOption questionWithOption : questions) {
                Question question = new Question();

                // 设置字段
                question.setType(questionWithOption.getType());
                question.setTitle(questionWithOption.getTitle());
                question.setTitleMap(String.valueOf(map));
                question.setQueryId(queryId);
                question.setRequired(questionWithOption.getRequired());

                map = (char) (map + 1);

                questionService.save(question);

                // 条件：标题和组织did一起作为检索条件
                QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
                questionQueryWrapper.eq("query_id", queryId).eq("title", questionWithOption.getTitle());
                Question question1 = questionService.getOne(questionQueryWrapper);

                Integer questionId = question1.getQuestionId();

                // 将从题目列表中提取出的类型与"下拉选择作比较
                if (Objects.equals(questionWithOption.getType(), Question.QuestionType.fromValue("下拉选择"))) {
                    List<String> options = questionWithOption.getOptions();
                    for (String option : options) {
                        Item item = new Item();
                        item.setContent(option);
                        item.setQuestionId(questionId);
                        itemService.save(item);
                    }
                }
            }
        } else {
            return R.error("未解析到问题");
        }
        return R.success(queryId);
    }

    @PostMapping("/queryList")
    public R<List<QueryInfo>> queryList(@RequestBody String json) {
        // 1. 从前端请求的json字符串中获得did
        ObjectMapper objectMapper = new ObjectMapper();
        String did = null;
        try {
            JsonNode request = objectMapper.readTree(json);
            did = request.get("did").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. 获取DID文档，获得type字段判断是校内还是校外
        JsonNode message = TDidUtil.getDidDocument(222223L, did);

        // 从json中提取document字段处理
        String document = message.get("document").asText();

        // 将字符串解析为JsonNode，并继续提取document中的内容
        ObjectMapper mapper = new ObjectMapper();
        String type = null;
        try {
            JsonNode documentJson = mapper.readTree(document);
            type = documentJson.get("type").asText();
        } catch (JsonProcessingException e) {
            return R.error(e.getMessage());
        }

        // 3. 根据type获得全部问卷信息，组成第一个列表
        List<Query> queryList = new ArrayList<>();
        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type).or().eq("type", "both");
        queryList = queryService.list(queryWrapper);

        List<QueryInfo> queryInfoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Query query : queryList) {

            if (now.isAfter(query.getDeadline())) {
                queryService.removeById(query.getQueryId());
                continue;
            }

            QueryInfo queryInfo = new QueryInfo();
            queryInfo.setQueryId(query.getQueryId());
            queryInfo.setTitle(query.getTitle());
            queryInfo.setDeadline(query.getDeadline());
            queryInfo.setPhoto(query.getLogo());
            queryInfo.setType(query.getType());
            queryInfo.setTag("凭证问卷");
            queryInfo.setBlogId(-1);    // 在没有查询到对应博客时，统一设定为-1



            // 根据机构DID查询机构名称
            QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
            organizationQueryWrapper.eq("did", query.getOrgDid());
            Organization organization = organizationService.getOne(organizationQueryWrapper);
            // 如果没查到则说明输入机构DID有误
            if (organization == null) {
                return R.error("输入的机构DID有误，没有查询到对应机构");
            }
            queryInfo.setOrgName(organization.getOrgName());
            queryInfoList.add(queryInfo);
        }

        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq("type", type).or().eq("type", "both");
        List<Blog> blogList = blogService.list(blogQueryWrapper);

        for (Blog blog : blogList) {
            for (QueryInfo queryInfo : queryInfoList) {
                // 如果问卷号相同，说明有对应的博客
                if (queryInfo.getQueryId().equals(blog.getQueryId())) {
                    queryInfo.setBlogId(blog.getBlogId());
                    queryInfo.setTitle(blog.getTitle());
                    queryInfo.setTag(blog.getTag());
                    queryInfo.setPhoto(blog.getImage1());
                }
            }
        }
        return R.success(queryInfoList);
    }

    @PostMapping("/getQueryDetails")
    public R<QueryDetail> getQueryDetails(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Integer queryId = null;
        Integer blogId = null;

        // 根据queryId查询问卷信息
        try {
            JsonNode request = objectMapper.readTree(json);
            queryId = request.get("queryId").asInt();
            blogId = request.get("blogId").asInt();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Query> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", queryId);
        Query query = queryService.getOne(queryWrapper);

        Blog blog = null;
        if (blogId > 0){
            QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
            blogQueryWrapper.eq("blog_id", blogId);
            blog = blogService.getOne(blogQueryWrapper);
        }

        QueryDetail queryDetail = new QueryDetail();
        if (query != null) {
            if (blog != null) {
                queryDetail.setBlogId(blog.getBlogId());
                queryDetail.setBlogTitle(blog.getTitle());
                queryDetail.setContent(blog.getContent());
                queryDetail.setImage1(blog.getImage1());
                queryDetail.setImage2(blog.getImage2());
                queryDetail.setImage3(blog.getImage3());
                queryDetail.setImage4(blog.getImage4());
                queryDetail.setImage5(blog.getImage5());
                queryDetail.setImage6(blog.getImage6());
                queryDetail.setImage7(blog.getImage7());
                queryDetail.setImage8(blog.getImage8());
                queryDetail.setImage9(blog.getImage9());
            }
            queryDetail.setQueryId(query.getQueryId());
            queryDetail.setQueryTitle(query.getTitle());

        } else {
            return R.error("未检索到对应问卷。");
        }

        // 根据queryId查询问题列表
        // 对照id进行查询
        Integer id = query.getQueryId();
        QueryWrapper<Question> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("query_id", id);
        List<Question> questionsList = questionService.list(queryWrapper1); // 调用 list 方法

        int i = 0;
        Integer questionId;

        List<QuestionWithOption> result = new ArrayList<>();

        // 按照前端输入的格式重新组建答案
        for (i = 0; i < questionsList.size(); i++) {

            Question question = questionsList.get(i);
            QuestionWithOption resultItem = new QuestionWithOption();

            resultItem.setRequired(question.getRequired());
            resultItem.setTitle(question.getTitle());
            resultItem.setType(question.getType());

            // 如果类型是下拉选择，那么单独查询选项并设置option字段
            if (resultItem.getType().getValue().equals("下拉选择")) {
                questionId = question.getQuestionId();

                // 查询满足question_id的item
                QueryWrapper<Item> itemsQueryWrapper = new QueryWrapper<>();
                itemsQueryWrapper.eq("question_id", questionId);
                List<Item> itemList = itemService.list(itemsQueryWrapper);

                // 抽取内容字符串
                List<String> itemsListStr = new ArrayList<>();
                for (Item item : itemList) {
                    itemsListStr.add(item.getContent());
                }

                resultItem.setOptions(itemsListStr);
            }
            // 将结果的一项添加到结果列表中
            result.add(resultItem);
        }

        queryDetail.setResult(result);

        return R.success(queryDetail);

    }
}
