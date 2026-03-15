package io.linfeng.modules.sys.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.sys.entity.SysConfigEntity;
import io.linfeng.modules.sys.param.SysConfigParam;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端系统配置信息
 *
 */
@Tag(name = "管理端——系统配置信息")
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {

	@Autowired
	private SysConfigService sysConfigService;
	

	@Operation(summary = "所有配置列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:config:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysConfigService.queryPage(params);

		return R.ok().put("page", page);
	}


	@Operation(summary = "根据ID配置信息")
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:config:info")
	public R info(@PathVariable("id") Long id){
		SysConfigEntity config = sysConfigService.getById(id);
		
		return R.ok().put("config", config);
	}
	

	@Operation(summary = "保存配置")
	@SysLog("保存配置")
	@PostMapping("/save")
	@RequiresPermissions("sys:config:save")
	public R save(@RequestBody SysConfigParam param){
		ValidatorUtils.validateEntity(param);

		SysConfigEntity config = new SysConfigEntity();
		config.setParamKey(param.getParamKey());
		config.setParamValue(param.getParamValue());
		config.setRemark(param.getRemark());
		sysConfigService.saveConfig(config);
		
		return R.ok();
	}
	

	@Operation(summary = "修改配置")
	@SysLog("修改配置")
	@PostMapping("/update")
	@RequiresPermissions("sys:config:update")
	public R update(@RequestBody SysConfigParam param){
		ValidatorUtils.validateEntity(param);
		
		SysConfigEntity config = new SysConfigEntity();
		config.setId(param.getId());
		config.setParamKey(param.getParamKey());
		config.setParamValue(param.getParamValue());
		config.setRemark(param.getRemark());
		sysConfigService.update(config);
		
		return R.ok();
	}
	

	@Operation(summary = "删除配置")
	@SysLog("删除配置")
	@PostMapping("/delete")
	@RequiresPermissions("sys:config:delete")
	public R delete(@RequestBody Long[] ids){
		sysConfigService.deleteBatch(ids);
		
		return R.ok();
	}


	@Operation(summary = "配置项的批量修改操作")
	@SysLog("配置项的批量修改操作")
	@PostMapping("/updateBatch")
	@RequiresPermissions("sys:config:update")
	public R updateBatch(@RequestBody String jsonStr){
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		jsonObject.forEach(
				(key,value)->{
					SysConfigEntity sysConfig = sysConfigService.lambdaQuery().eq(SysConfigEntity::getParamKey, key).one();
					SysConfigEntity sysConfigDto=new SysConfigEntity();
					sysConfigDto.setParamKey(key);
					sysConfigDto.setParamValue(value.toString());
					if(ObjectUtil.isNull(sysConfig)){
						sysConfigService.saveConfig(sysConfigDto);
					}else{
						sysConfigDto.setId(sysConfig.getId());
						sysConfigService.update(sysConfigDto);
					}
				});

		return R.ok();
	}

}
