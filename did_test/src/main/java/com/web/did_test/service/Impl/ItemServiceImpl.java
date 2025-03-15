package com.web.did_test.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.did_test.mapper.*;
import com.web.did_test.pojo.Item;
import com.web.did_test.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    private final BlogMapper blogMapper;
    private final OrganizationMapper organizationMapper;
    private final InsiderMapper insiderMapper;
    private final QueryMapper queryMapper;
    private final QuestionMapper questionMapper;
    private final OutsiderMapper outsiderMapper;
    private final ItemMapper itemMapper;
    private final CommitMapper commitMapper;
}
