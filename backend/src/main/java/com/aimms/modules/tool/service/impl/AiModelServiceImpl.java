package com.aimms.modules.tool.service.impl;

import com.aimms.modules.tool.entity.AiModel;
import com.aimms.modules.tool.mapper.AiModelMapper;
import com.aimms.modules.tool.service.AiModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> implements AiModelService {

    @Override
    public List<AiModel> listByToolId(Integer toolId) {
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiModel::getToolId, toolId);
        return list(wrapper);
    }
}
