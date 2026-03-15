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
import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.admin.entity.TopicEntity;
import io.linfeng.modules.admin.param.TopicParam;
import io.linfeng.modules.admin.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 17:01:12
 */
@RestController
@RequestMapping("admin/topic")
@Tag(name = "管理端——圈子管理")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private RedisUtils redisUtils;


    @GetMapping("/list")
    @RequiresPermissions("admin:topic:list")
    @Operation(summary = "圈子列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = topicService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:topic:info")
    @Operation(summary = "圈子详情")
    public R info(@PathVariable("id") Integer id){
		TopicEntity topic = topicService.getById(id);

        return R.ok().put("topic", topic);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:topic:save")
    @Operation(summary = "保存圈子")
    public R save(@RequestBody TopicParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        TopicEntity topic = new TopicEntity();
        BeanUtils.copyProperties(param, topic);
		topicService.saveTopicByAdmin(topic);

        return R.ok();
    }


    @SysLog("修改圈子")
    @PostMapping("/update")
    @RequiresPermissions("admin:topic:update")
    @Operation(summary = "修改圈子")
    public R update(@RequestBody TopicParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        TopicEntity topic = new TopicEntity();
        BeanUtils.copyProperties(param, topic);
		topicService.updateByAdmin(topic);
        redisUtils.delete(ConfigConstant.H5_TOPIC_POSTER_KEY + topic.getId());
        redisUtils.delete(ConfigConstant.WX_TOPIC_POSTER_KEY + topic.getId());
        return R.ok();
    }



    @SysLog("删除圈子")
    @PostMapping("/delete")
    @RequiresPermissions("admin:topic:delete")
    @Operation(summary = "删除圈子")
    public R delete(@RequestBody Integer[] ids){
        topicService.topicDeleteByAdmin(Arrays.asList(ids));
        return R.ok();
    }


    @GetMapping("/getJoinTopicList/{id}")
    @Operation(summary = "查询用户加入的圈子")
    public R getJoinTopicList(@PathVariable("id") Integer id){
        List<TopicEntity> list = topicService.getJoinTopicList(id);

        return R.ok().put("result",list);
    }

}
