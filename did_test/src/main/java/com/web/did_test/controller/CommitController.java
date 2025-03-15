package com.web.did_test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.pojo.Commit;
import com.web.did_test.service.CommitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/commit")
@RequiredArgsConstructor
public class CommitController {
    private final CommitService commitService;

    @RequestMapping("/getCommits")
    public R<List<Commit>> getCommits(@RequestBody String json){
        ObjectMapper objectMapper = new ObjectMapper();
        int queryId = 0;

        try {
            JsonNode request = objectMapper.readTree(json);
            queryId = request.get("queryId").asInt();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Commit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("query_id", queryId);
        List<Commit> commits = commitService.list(queryWrapper);

        System.out.println(commits);

        return R.success(commits);
    }

    @RequestMapping("/modifyAccount")
    public R<String> modifyAccount(@RequestBody String json){
        ObjectMapper objectMapper = new ObjectMapper();
        int commitId = -1;
        Integer value = null;

        try {
            JsonNode request = objectMapper.readTree(json);
            commitId = request.get("commitId").asInt();
            value = request.get("value").asInt();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Commit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("commit_id", commitId);
        Commit commit = commitService.getOne(queryWrapper);

        if (commit == null) {
            return R.error("不存在这张卡");
        }

        if ( commit.getAccount() <= value) {
            int remain = value - commit.getAccount();
            commit.setAccount(0);
            commitService.removeById(commitId);
            return R.successMsg("余额耗尽，卡片自动从后台删除，还需要补交" + remain);
        }

        Integer account = commit.getAccount();
        account = account - value;

        commit.setAccount(account);
        commitService.updateById(commit);

        return R.successMsg("成功扣掉额度："+ value.toString());

    }
}
