package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.SearchService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.utils.LocalUser;
import io.linfeng.common.vo.app.AppSearchHistoryResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linfeng
 * @date 2023/4/26 20:26
 */
@Tag(name = "移动端——历史搜索模块")
@RestController
@RequestMapping("app/search")
public class AppSearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private LocalUser localUser;


    @GetMapping("/getUserSearchHistory")
    @Operation(summary = "获取用户搜索历史列表")
    public Result<List<AppSearchHistoryResponse>> getUserSearchHistory(){
        List<AppSearchHistoryResponse> list=new ArrayList<>();
        AppUserEntity user = localUser.getUser();
        if(user!=null){
            list = DataConvertUtils.sourceToTarget(
                    searchService.getSearchListByUid(user.getUid()),
                    AppSearchHistoryResponse.class
            );
        }
        return new Result<List<AppSearchHistoryResponse>>().ok(list);
    }


    @GetMapping("/getHotSearchHistory")
    @Operation(summary = "获取热门搜索历史列表")
    public Result<List<String>> getHotSearchHistory(){
        List<String> list=searchService.selectHotSearch();
        return new Result<List<String>>().ok(list);
    }


    @PostMapping("/deleteSearchById/{id}")
    @Operation(summary = "根据searchId删除搜索历史")
    public Result deleteSearchById(@PathVariable Long id){
        searchService.removeById(id);
        return new Result<>().ok();
    }


    @Login
    @PostMapping("/deleteSearchByUId")
    @Operation(summary = "删除该用户全部搜索历史")
    public Result deleteSearchByUId(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        searchService.deleteSearchByUId(user.getUid());
        return new Result<>().ok();
    }

}
