package com.aimms.modules.application.service;

import com.aimms.modules.application.entity.QuotaApplication;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface QuotaApplicationService extends IService<QuotaApplication> {

    QuotaApplication submit(QuotaApplication application);

    QuotaApplication approve(Integer id, Integer approverId, String comment);

    QuotaApplication reject(Integer id, Integer approverId, String comment);

    QuotaApplication upgradeToAdmin(Integer id);

    QuotaApplication adminApprove(Integer id, Integer adminId, String comment);

    List<QuotaApplication> listPending(Integer deptId);
}
