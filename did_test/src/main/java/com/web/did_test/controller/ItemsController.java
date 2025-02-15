package com.web.did_test.controller;

import com.web.did_test.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/Items")
@RequiredArgsConstructor
public class ItemsController {
    private final ItemsService itemsService;
    private final OrganizationService organizationService;
    private final PersonService personService;
    private final QueryService queryService;
    private final QuestionService questionService;

}
