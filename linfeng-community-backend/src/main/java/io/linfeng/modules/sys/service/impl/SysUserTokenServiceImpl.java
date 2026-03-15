package io.linfeng.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.R;
import io.linfeng.modules.sys.dao.SysUserTokenDao;
import io.linfeng.modules.sys.entity.SysUserTokenEntity;
import io.linfeng.modules.sys.oauth2.TokenGenerator;
import io.linfeng.modules.sys.service.SysUserTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {

	//过期时间  单位秒
	private final static int EXPIRE = 3600 * 12;


	@Override
	public R createToken(long userId) {
		//生成token
		String token = TokenGenerator.generateValue();

		Date now = new Date();
		Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

		//判断是否生成过token
		SysUserTokenEntity userToken = this.getById(userId);
		if(userToken == null){
			userToken = new SysUserTokenEntity();
			userToken.setUserId(userId);
			userToken.setToken(token);
			userToken.setUpdateTime(now);
			userToken.setExpireTime(expireTime);
			this.save(userToken);
		}else{
			userToken.setToken(token);
			userToken.setUpdateTime(now);
			userToken.setExpireTime(expireTime);
			this.updateById(userToken);
		}

		R result = R.ok().put("token", token).put("expire", EXPIRE);

		return result;
	}

	@Override
	public void logout(long userId) {
		String token = TokenGenerator.generateValue();
		SysUserTokenEntity userToken = new SysUserTokenEntity();
		userToken.setUserId(userId);
		userToken.setToken(token);
		this.updateById(userToken);
	}
}
