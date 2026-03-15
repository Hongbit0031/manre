package io.linfeng.modules.job.controller;

import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.modules.job.entity.ScheduleJobLogEntity;
import io.linfeng.modules.job.service.ScheduleJobLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 定时任务日志
 *
 */
@Tag(name = "管理端——定时任务日志")
@RestController
@RequestMapping("/sys/scheduleLog")
public class ScheduleJobLogController {
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;
	


	@GetMapping("/list")
	@RequiresPermissions("sys:schedule:log")
	@Operation(summary = "定时任务日志列表")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = scheduleJobLogService.queryPage(params);
		
		return R.ok().put("page", page);
	}
	

	@GetMapping("/info/{logId}")
	@Operation(summary = "定时任务日志列表")
	public R info(@PathVariable("logId") Long logId){
		ScheduleJobLogEntity log = scheduleJobLogService.getById(logId);
		
		return R.ok().put("log", log);
	}

}
