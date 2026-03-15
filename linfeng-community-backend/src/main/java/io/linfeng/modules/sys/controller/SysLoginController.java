package io.linfeng.modules.sys.controller;

import io.linfeng.common.utils.HttpContextUtils;
import io.linfeng.common.utils.IPUtils;
import io.linfeng.common.utils.R;
import io.linfeng.modules.sys.entity.SysUserEntity;
import io.linfeng.modules.sys.param.SysLoginForm;
import io.linfeng.modules.sys.service.SysCaptchaService;
import io.linfeng.modules.sys.service.SysLogService;
import io.linfeng.modules.sys.service.SysUserService;
import io.linfeng.modules.sys.service.SysUserTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 后台管理系统 登录相关模块
 *
 */
@Tag(name = "管理端——后台登录")
@RestController
public class SysLoginController extends AbstractController {

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private SysCaptchaService sysCaptchaService;
	@Autowired
	private SysLogService sysLogService;


	@Operation(summary = "验证码")
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}


	@Operation(summary = "登录")
	@PostMapping("/sys/login")
	public Map<String, Object> login(@RequestBody SysLoginForm form) {
		long beginTime = System.currentTimeMillis();
		boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}

		//用户信息
		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());

		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(new Sha256Hash(form.getPassword(), user.getSalt()).toHex())) {
			return R.error("账号或密码不正确");
		}

		//账号锁定
		if(user.getStatus() == 0){
			return R.error("账号已被禁用 请联系管理员");
		}

		//生成token，并保存到数据库
		R r = sysUserTokenService.createToken(user.getUserId());
		//生成登录日志
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		String ip = IPUtils.getIpAddr(request);
		long time = System.currentTimeMillis() - beginTime;
		sysLogService.saveLog(ip,time,user.getUsername());
		return r;
	}


	/**
	 * 退出
	 */
	@Operation(summary = "退出")
	@PostMapping("/sys/logout")
	public R logout() {
		sysUserTokenService.logout(getUserId());
		return R.ok();
	}

    @Operation(summary = "直接访问后端地址响应")
    @GetMapping("/")
    public R index(){
        String msg = "linfeng-community-backend后端启动成功！";
        return R.ok(msg);
    }
	
}
