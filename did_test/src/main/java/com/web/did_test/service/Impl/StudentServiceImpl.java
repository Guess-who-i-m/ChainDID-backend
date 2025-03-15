package com.web.did_test.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.did_test.mapper.*;
import com.web.did_test.pojo.Student;
import com.web.did_test.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    private final ItemsMapper itemsMapper;
    private final OrganizationMapper organizationMapper;
    private final StudentMapper studentMapper;
    private final QueryMapper queryMapper;
    private final QuestionMapper questionMapper;
    private final GuestMapper guestMapper;


}
