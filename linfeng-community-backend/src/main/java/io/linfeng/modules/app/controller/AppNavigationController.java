package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppNavigationResponse;
import io.linfeng.modules.admin.service.NavigationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 导航栏模块
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 */
@RestController
@RequestMapping("app/navigation")
@Tag(name = "移动端——导航栏模块")
public class AppNavigationController {


    @Autowired
    private NavigationService navigationService;


    @PostMapping("/getNav")
    @Operation(summary = "导航栏查询")
    public Result<List<AppNavigationResponse>> getNav(){
		List<AppNavigationResponse> list = DataConvertUtils.sourceToTarget(
                navigationService.getNav(), AppNavigationResponse.class);
        return new Result<List<AppNavigationResponse>>().ok(list);
    }

}
