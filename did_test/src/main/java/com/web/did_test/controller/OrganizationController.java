package com.web.did_test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.did_test.common.R;
import com.web.did_test.pojo.Organization;
import com.web.did_test.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {
    private final ItemService itemService;
    private final OrganizationService organizationService;
    private final InsiderService insiderService;
    private final QueryService queryService;
    private final QuestionService questionService;
    private final OutsiderService outsiderService;

    @PostMapping("/login")
    public R<Boolean> login(@RequestBody String json){
        ObjectMapper objectMapper = new ObjectMapper();
        String did = null;
        try{
            JsonNode request = objectMapper.readTree(json);
            did = request.get("did").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", did);
        Organization organization = organizationService.getOne(queryWrapper);

        if (organization == null) {
            return R.success(false);    // 登录失败
        } else {
            return R.success(true);
        }
    }

    @PostMapping("/register")
    public R<String> register(@RequestBody String json){
        ObjectMapper objectMapper = new ObjectMapper();
        String did = null;
        String orgName = null;
        String orgLicence = null;
        String logo = null;

        // 目前没有做审核，可以考虑添加一个字段或者单开一张表
        try {
            JsonNode request = objectMapper.readTree(json);
            did = request.get("did").asText();
            orgName = request.get("orgName").asText();
            orgLicence = request.get("orgLicence").asText();
            logo = request.get("logo").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", did);
        Organization organization1 = organizationService.getOne(queryWrapper);

        if (organization1 != null) {
            return R.error("已经注册过相关机构了");
        }

        Organization organization = new Organization();
        organization.setDid(did);
        organization.setOrgName(orgName);
        organization.setOrgLicence(orgLicence);
        organization.setLogo(logo);
        organizationService.save(organization);

        return R.success("创建成功！");
    }

}
