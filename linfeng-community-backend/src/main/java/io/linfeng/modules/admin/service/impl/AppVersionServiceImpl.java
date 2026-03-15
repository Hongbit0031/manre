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
package io.linfeng.modules.admin.service.impl;

import io.linfeng.common.vo.app.AppVersionResponse;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.admin.dao.AppVersionDao;
import io.linfeng.modules.admin.entity.AppVersionEntity;
import io.linfeng.modules.admin.service.AppVersionService;


@Service("appVersionService")
public class AppVersionServiceImpl extends ServiceImpl<AppVersionDao, AppVersionEntity> implements AppVersionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AppVersionEntity> page = this.page(
                new Query<AppVersionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public AppVersionResponse checkUpdate(String currentVersion) {
        AppVersionEntity latestVersion = this.lambdaQuery()
                .eq(AppVersionEntity::getStatus, 1)
                .orderByDesc(AppVersionEntity::getId)
                .last("limit 1")
                .one();
        // 如果没有找到最新版本或版本号为空，返回不需要更新
        if (latestVersion == null || latestVersion.getVersion() == null) {
            return createNoUpdateResponse();
        }

        // 比较版本号
        boolean needUpdate = compareVersion(currentVersion, latestVersion.getVersion()) < 0;

        // 检查是否需要强制更新
        boolean forceUpdate = false;
        if (latestVersion.getMinVersion() != null) {
            forceUpdate = compareVersion(currentVersion, latestVersion.getMinVersion()) < 0;
        }

        if (needUpdate) {
            return createUpdateResponse(latestVersion, forceUpdate);
        } else {
            return createNoUpdateResponse();
        }
    }

    // 创建不需要更新的响应
    private AppVersionResponse createNoUpdateResponse() {
        AppVersionResponse response = new AppVersionResponse();
        response.setUpdate(false);
        return response;
    }

    // 创建需要更新的响应
    private AppVersionResponse createUpdateResponse(AppVersionEntity latestVersion, boolean forceUpdate) {
        AppVersionResponse response = new AppVersionResponse();
        response.setUpdate(true);
        response.setIsForce(forceUpdate ? 1 : 0);
        response.setVersion(latestVersion.getVersion());
        response.setContent(latestVersion.getContent());
        response.setUrl(latestVersion.getDownloadUrl());
        response.setAndroidUrl(latestVersion.getAndroidUrl());
        response.setIosUrl(latestVersion.getIosUrl());
        return response;
    }


    // 比较版本号
    private int compareVersion(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        int len = Math.max(v1.length, v2.length);

        for (int i = 0; i < len; i++) {
            int num1 = (i < v1.length) ? Integer.parseInt(v1[i]) : 0;
            int num2 = (i < v2.length) ? Integer.parseInt(v2[i]) : 0;

            if (num1 != num2) {
                return num1 - num2;
            }
        }

        return 0;
    }
}