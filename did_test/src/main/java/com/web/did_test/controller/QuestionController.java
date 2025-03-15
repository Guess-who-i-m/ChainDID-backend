package com.web.did_test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.dto.QuestionWithOption;
import com.web.did_test.pojo.Item;
import com.web.did_test.pojo.Question;
import com.web.did_test.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final ItemService itemService;
    private final OrganizationService organizationService;
    private final InsiderService insiderService;
    private final QueryService queryService;
    private final QuestionService questionService;
    private final OutsiderService outsiderService;

    @PostMapping("/getQuestion")
    public R<List<QuestionWithOption>> getQuestion(@RequestBody String json) {
        // 声明一个json格式的映射器
        ObjectMapper mapper = new ObjectMapper();
        // 声明待提取的id
        int id;

        // 提取id
        try {
            JsonNode queryId = mapper.readTree(json);
            id = queryId.get("queryId").asInt();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 对照id进行查询
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", id);
        List<Question> questionsList = questionService.list(queryWrapper); // 调用 list 方法

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
        // 返回结果
        return R.success(result);
    }
}
