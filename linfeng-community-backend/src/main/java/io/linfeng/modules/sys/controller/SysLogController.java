package io.linfeng.modules.sys.controller;

import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.modules.sys.service.SysLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 系统日志
 *
 */
@Tag(name = "管理端——系统日志")
@Controller
@RequestMapping("/sys/log")
public class SysLogController {

	@Autowired
	private SysLogService sysLogService;
	

	@Operation(summary = "日志列表")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysLogService.queryPage(params);

		return R.ok().put("page", page);
	}
	
}
