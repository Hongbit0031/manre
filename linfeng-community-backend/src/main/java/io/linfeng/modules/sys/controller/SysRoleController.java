package io.linfeng.modules.sys.controller;

import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.sys.entity.SysRoleEntity;
import io.linfeng.modules.sys.param.SysRoleParam;
import io.linfeng.modules.sys.service.SysRoleMenuService;
import io.linfeng.modules.sys.service.SysRoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 */
@Tag(name = "管理端——角色管理")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {

	@Autowired
	private SysRoleService sysRoleService;

	@Autowired
	private SysRoleMenuService sysRoleMenuService;


	@Operation(summary = "角色列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:role:list")
	public R list(@RequestParam Map<String, Object> params){
		//注意：若不是超级管理员则只查询自己创建的角色列表
		if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}

		PageUtils page = sysRoleService.queryPage(params);

		return R.ok().put("page", page);
	}
	

	@Operation(summary = "角色查询")
	@GetMapping("/select")
	@RequiresPermissions("sys:role:select")
	public R select(){
		Map<String, Object> map = new HashMap<>();
		
		//注意：若不是超级管理员则只查自己所拥有的角色列表
		if(getUserId() != Constant.SUPER_ADMIN){
			map.put("create_user_id", getUserId());
		}
		List<SysRoleEntity> list = sysRoleService.listByMap(map);
		
		return R.ok().put("list", list);
	}
	

	@Operation(summary = "根据id获取角色信息")
	@GetMapping("/info/{roleId}")
	@RequiresPermissions("sys:role:info")
	public R info(@PathVariable("roleId") Long roleId){
		SysRoleEntity role = sysRoleService.getById(roleId);
		
		//查询角色对应的菜单
		List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);
		
		return R.ok().put("role", role);
	}
	

	@Operation(summary = "保存角色")
	@SysLog("保存角色")
	@PostMapping("/save")
	@RequiresPermissions("sys:role:save")
	public R save(@RequestBody SysRoleParam param){
		ValidatorUtils.validateEntity(param);
		
		//转换为实体类
		SysRoleEntity role = new SysRoleEntity();
		role.setRoleName(param.getRoleName());
		role.setRemark(param.getRemark());
		role.setMenuIdList(param.getMenuIdList());
		role.setCreateUserId(getUserId());
		
		sysRoleService.saveRole(role);
		
		return R.ok();
	}
	

	@Operation(summary = "修改角色")
	@SysLog("修改角色")
	@PostMapping("/update")
	@RequiresPermissions("sys:role:update")
	public R update(@RequestBody SysRoleParam param){
		ValidatorUtils.validateEntity(param);
		
		//转换为实体类
		SysRoleEntity role = new SysRoleEntity();
		role.setRoleId(param.getRoleId());
		role.setRoleName(param.getRoleName());
		role.setRemark(param.getRemark());
		role.setMenuIdList(param.getMenuIdList());
		role.setCreateUserId(getUserId());
		
		sysRoleService.update(role);
		
		return R.ok();
	}
	

	@Operation(summary = "删除角色")
	@SysLog("删除角色")
	@PostMapping("/delete")
	@RequiresPermissions("sys:role:delete")
	public R delete(@RequestBody Long[] roleIds){
		sysRoleService.deleteBatch(roleIds);
		
		return R.ok();
	}
}
