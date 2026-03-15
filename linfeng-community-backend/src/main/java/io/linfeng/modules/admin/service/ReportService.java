/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.AppReportListResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.ReportEntity;
import io.linfeng.modules.app.param.ReportAddForm;

import java.util.Map;

/**
 * 用户举报
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-09-01 12:55:12
 */
public interface ReportService extends IService<ReportEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addReport(ReportAddForm request, AppUserEntity user);

    void dealByAdmin(ReportEntity report);

    AppPageUtils<AppReportListResponse> listByUser(Integer page,Integer status, AppUserEntity user);

    AppReportListResponse detail(Integer id);
}

