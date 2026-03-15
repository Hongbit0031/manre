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
package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppUserMenuResponse;
import io.linfeng.modules.admin.service.UserMenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户菜单
 *
 * @author linfeng
 */
@RestController
@RequestMapping("app/userMenu")
@Tag(name = "移动端——用户菜单模块")
public class AppUserMenuController {

    @Autowired
    private UserMenuService userMenuService;


    @GetMapping("/list")
    @Operation(summary = "用户菜单列表查询")
    public Result<List<AppUserMenuResponse>> list(){
        List<AppUserMenuResponse> list = DataConvertUtils.sourceToTarget(
                userMenuService.menuList(), AppUserMenuResponse.class
        );
        return new Result<List<AppUserMenuResponse>>().ok(list);
    }

}
