package com.aimms.modules.tool.service.impl;

import com.aimms.modules.tool.entity.AiTool;
import com.aimms.modules.tool.mapper.AiToolMapper;
import com.aimms.modules.tool.service.AiToolService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AiToolServiceImpl extends ServiceImpl<AiToolMapper, AiTool> implements AiToolService {
}
