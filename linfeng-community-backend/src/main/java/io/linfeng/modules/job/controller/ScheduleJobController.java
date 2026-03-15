package io.linfeng.modules.job.controller;

import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.job.entity.ScheduleJobEntity;
import io.linfeng.modules.job.param.ScheduleJobParam;
import io.linfeng.modules.job.service.ScheduleJobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 定时任务
 *
 */
@Tag(name = "管理端——定时任务")
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
	@Autowired
	private ScheduleJobService scheduleJobService;
	

	@GetMapping("/list")
	@RequiresPermissions("sys:schedule:list")
	@Operation(summary = "定时任务列表")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = scheduleJobService.queryPage(params);

		return R.ok().put("page", page);
	}
	

	@GetMapping("/info/{jobId}")
	@RequiresPermissions("sys:schedule:info")
	@Operation(summary = "定时任务详情")
	public R info(@PathVariable("jobId") Long jobId){
		ScheduleJobEntity schedule = scheduleJobService.getById(jobId);
		
		return R.ok().put("schedule", schedule);
	}
	

	@SysLog("保存定时任务")
	@PostMapping("/save")
	@RequiresPermissions("sys:schedule:save")
	@Operation(summary = "保存定时任务")
	public R save(@RequestBody ScheduleJobParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		//转换为实体类
		ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
		scheduleJob.setBeanName(param.getBeanName());
		scheduleJob.setParams(param.getParams());
		scheduleJob.setCronExpression(param.getCronExpression());
		scheduleJob.setStatus(param.getStatus());
		scheduleJob.setRemark(param.getRemark());
		
		scheduleJobService.saveJob(scheduleJob);
		
		return R.ok();
	}
	

	@SysLog("修改定时任务")
	@PostMapping("/update")
	@RequiresPermissions("sys:schedule:update")
	@Operation(summary = "修改定时任务")
	public R update(@RequestBody ScheduleJobParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		//转换为实体类
		ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
		scheduleJob.setJobId(param.getJobId());
		scheduleJob.setBeanName(param.getBeanName());
		scheduleJob.setParams(param.getParams());
		scheduleJob.setCronExpression(param.getCronExpression());
		scheduleJob.setStatus(param.getStatus());
		scheduleJob.setRemark(param.getRemark());
				
		scheduleJobService.update(scheduleJob);
		
		return R.ok();
	}
	

	@SysLog("删除定时任务")
	@PostMapping("/delete")
	@RequiresPermissions("sys:schedule:delete")
	@Operation(summary = "删除定时任务")
	public R delete(@RequestBody Long[] jobIds){
		scheduleJobService.deleteBatch(jobIds);
		
		return R.ok();
	}
	

	@SysLog("立即执行任务")
	@PostMapping("/run")
	@RequiresPermissions("sys:schedule:run")
	@Operation(summary = "删除定时任务")
	public R run(@RequestBody Long[] jobIds){
		scheduleJobService.run(jobIds);
		
		return R.ok();
	}
	


	@SysLog("暂停定时任务")
	@PostMapping("/pause")
	@RequiresPermissions("sys:schedule:pause")
	@Operation(summary = "暂停定时任务")
	public R pause(@RequestBody Long[] jobIds){
		scheduleJobService.pause(jobIds);
		
		return R.ok();
	}
	


	@SysLog("恢复定时任务")
	@PostMapping("/resume")
	@RequiresPermissions("sys:schedule:resume")
	@Operation(summary = "恢复定时任务")
	public R resume(@RequestBody Long[] jobIds){
		scheduleJobService.resume(jobIds);
		
		return R.ok();
	}

}
