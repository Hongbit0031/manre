/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppCategoryResponse;
import io.linfeng.modules.admin.entity.CategoryEntity;
import io.linfeng.modules.admin.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 圈子分类
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 14:32:52
 */
@RestController
@RequestMapping("app")
@Tag(name = "移动端——圈子分类模块")
public class AppCategoryController {


    @Autowired
    private CategoryService categoryService;


    @GetMapping("/topic/classList")
    @Operation(summary = "分类列表")
    public Result<List<AppCategoryResponse>> list(){
        List<CategoryEntity> entityList = categoryService.getList();
        List<AppCategoryResponse> result = DataConvertUtils.sourceToTarget(entityList, AppCategoryResponse.class);
        return new Result<List<AppCategoryResponse>>().ok(result);
    }


}
