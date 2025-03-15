package com.web.did_test.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.did_test.mapper.*;
import com.web.did_test.pojo.Question;
import com.web.did_test.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    private final BlogMapper blogMapper;
    private final OrganizationMapper organizationMapper;
    private final InsiderMapper insiderMapper;
    private final QueryMapper queryMapper;
    private final QuestionMapper questionMapper;
    private final OutsiderMapper outsiderMapper;
    private final ItemMapper itemMapper;
    private final CommitMapper commitMapper;
}
