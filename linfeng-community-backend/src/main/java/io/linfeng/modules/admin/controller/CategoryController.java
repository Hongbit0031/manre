/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.modules.admin.entity.TopicEntity;
import io.linfeng.modules.admin.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.CategoryEntity;
import io.linfeng.modules.admin.param.CategoryParam;
import io.linfeng.modules.admin.service.CategoryService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;



/**
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 14:32:52
 */
@RestController
@RequestMapping("admin/category")
@Tag(name = "管理端——圈子分类管理")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TopicService topicService;


    @GetMapping("/list")
    @RequiresPermissions("admin:category:list")
    @Operation(summary = "分类列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/getList")
    @Operation(summary = "分类列表-全部-不分页")
    public R getList(){
        List<CategoryEntity> list = categoryService.getList();

        return R.ok().put("result", list);
    }



    @GetMapping("/info/{cateId}")
    @RequiresPermissions("admin:category:info")
    @Operation(summary = "分类详情")
    public R info(@PathVariable("cateId") Integer cateId){
		CategoryEntity category = categoryService.getById(cateId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:category:save")
    @Operation(summary = "分类保存")
    public R save(@RequestBody CategoryParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		CategoryEntity category = new CategoryEntity();
		BeanUtils.copyProperties(param, category);
        redisUtils.delete(ConfigConstant.TOPIC_CATEGORY_KEY);
		categoryService.saveCategory(category);

        return R.ok();
    }


    @SysLog("修改圈子分类")
    @PostMapping("/update")
    @RequiresPermissions("admin:category:update")
    @Operation(summary = "修改分类")
    public R update(@RequestBody CategoryParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		CategoryEntity category = new CategoryEntity();
		BeanUtils.copyProperties(param, category);
        redisUtils.delete(ConfigConstant.TOPIC_CATEGORY_KEY);
		categoryService.updateById(category);

        return R.ok();
    }


    @SysLog("删除圈子分类")
    @PostMapping("/delete")
    @RequiresPermissions("admin:category:delete")
    @Operation(summary = "删除分类")
    public R delete(@RequestBody Integer[] cateIds){
        List<Integer> list=Arrays.asList(cateIds);
        list.forEach(id->{
            Long count = topicService.lambdaQuery().eq(TopicEntity::getCateId, id).count();
            if(count>0){
                throw new LinfengException("分类下存在圈子无法删除");
            }
        });
        redisUtils.delete(ConfigConstant.TOPIC_CATEGORY_KEY);
		categoryService.removeByIds(Arrays.asList(cateIds));

        return R.ok();
    }

}
