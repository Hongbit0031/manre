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
import io.linfeng.common.vo.app.AppVipBenefitResponse;
import io.linfeng.modules.admin.entity.VipBenefitEntity;
import io.linfeng.modules.admin.service.VipBenefitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 会员权益
 *
 * @author Pity
 * @email linfengtech002@163.com
 * @date 2023-12-11 22:51:19
 */
@RestController
@RequestMapping("app/vipbenefit")
@Tag(name = "移动端——会员权益模块")
public class AppVipBenefitController {

    @Autowired
    private VipBenefitService vipBenefitService;


    @GetMapping("/getList")
    @Operation(summary = "会员权益列表")
    public Result<List<AppVipBenefitResponse>> list(){
        List<VipBenefitEntity> entities = vipBenefitService.getList();
        List<AppVipBenefitResponse> result = DataConvertUtils.sourceToTarget(
                entities, AppVipBenefitResponse.class
        );
        return new Result<List<AppVipBenefitResponse>>().ok(result);
    }

}
