package com.aimms.modules.alert.service;

import com.aimms.modules.alert.entity.AlertLog;
import com.aimms.modules.alert.entity.AlertRule;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface AlertService {

    List<AlertRule> listRules();

    AlertRule saveRule(AlertRule rule);

    void deleteRule(Integer id);

    List<AlertLog> listLogs();

    void checkAndTrigger(Integer userId, Integer deptId, String period);
}
