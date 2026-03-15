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
import io.linfeng.common.vo.app.AppLinkResponse;
import io.linfeng.modules.admin.service.LinkService;
import io.linfeng.modules.app.param.LinkListForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 
 * 轮播图管理
 * @author linfeng
 */
@RestController
@RequestMapping("app/link")
@Tag(name = "移动端——轮播图模块")
public class AppLinkController {

    @Autowired
    private LinkService linkService;


    @PostMapping("/list")
    @Operation(summary = "获取轮播图列表")
    public Result<List<AppLinkResponse>> list(@RequestBody LinkListForm request){
        List<AppLinkResponse> result = DataConvertUtils.sourceToTarget(linkService.getPageList(request), AppLinkResponse.class);
        return new Result<List<AppLinkResponse>>().ok(result);
    }

}
