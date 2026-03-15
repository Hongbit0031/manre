package io.linfeng.modules.sys.controller;

import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.Assert;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.sys.entity.SysUserEntity;
import io.linfeng.modules.sys.param.PasswordForm;
import io.linfeng.modules.sys.param.SysUserParam;
import io.linfeng.modules.sys.service.SysUserRoleService;
import io.linfeng.modules.sys.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 */
@Tag(name = "管理端——系统用户")
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;



	@Operation(summary = "所有用户列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public R list(@RequestParam Map<String, Object> params){
		//注意：只有超级管理员，才能查看所有管理员列表 你也可以注释掉代码
		if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}
	

	@Operation(summary = "获取登录的用户信息")
	@GetMapping("/info")
	public R info(){
		return R.ok().put("user", getUser());
	}
	

	@Operation(summary = "修改密码")
	@SysLog("修改密码")
	@PostMapping("/password")
	@RequiresPermissions("sys:user:update")
	public R password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");
		String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
		String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}
		return R.ok();
	}
	

	@Operation(summary = "根据id获取用户信息")
	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	public R info(@PathVariable("userId") Long userId){
		SysUserEntity user = sysUserService.getById(userId);
		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		
		return R.ok().put("user", user);
	}


	@Operation(summary = "保存用户")
	@SysLog("保存用户")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	public R save(@RequestBody SysUserParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		//转换为实体类
		SysUserEntity user = new SysUserEntity();
		user.setUsername(param.getUsername());
		user.setPassword(param.getPassword());
		user.setEmail(param.getEmail());
		user.setMobile(param.getMobile());
		user.setStatus(param.getStatus());
		user.setRoleIdList(param.getRoleIdList());
		user.setCreateUserId(getUserId());
		
		sysUserService.saveUser(user);
		
		return R.ok();
	}
	

	@Operation(summary = "修改用户")
	@SysLog("修改用户")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public R update(@RequestBody SysUserParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);

		//转换为实体类
		SysUserEntity user = new SysUserEntity();
		user.setUserId(param.getUserId());
		user.setUsername(param.getUsername());
		user.setPassword(param.getPassword());
		user.setEmail(param.getEmail());
		user.setMobile(param.getMobile());
		user.setStatus(param.getStatus());
		user.setRoleIdList(param.getRoleIdList());
		user.setCreateUserId(getUserId());
		
		//如果更新密码，需要从数据库获取salt
		if(StringUtils.isNotBlank(user.getPassword())){
			SysUserEntity existUser = sysUserService.getById(param.getUserId());
			if(existUser != null){
				user.setSalt(existUser.getSalt());
			}
		}
		
		sysUserService.update(user);
		
		return R.ok();
	}
	

	@Operation(summary = "删除用户")
	@SysLog("删除用户")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public R delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}
		if(ArrayUtils.contains(userIds, getUserId())){
			return R.error("当前用户不能删除");
		}
		sysUserService.deleteBatch(userIds);
		
		return R.ok();
	}
}
