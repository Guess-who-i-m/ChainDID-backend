package com.web.did_test.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.did_test.mapper.*;
import com.web.did_test.pojo.Person;
import com.web.did_test.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {
    private final ItemsMapper itemsMapper;
    private final OrganizationMapper organizationMapper;
    private final PersonMapper personMapper;
    private final QueryMapper queryMapper;
    private final QuestionMapper questionMapper;


}
