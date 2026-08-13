package com.aimms.modules.tool.service;

import com.aimms.modules.tool.entity.AiModel;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface AiModelService extends IService<AiModel> {

    List<AiModel> listByToolId(Integer toolId);
}
