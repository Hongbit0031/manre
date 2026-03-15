package io.linfeng.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.sys.entity.SysConfigEntity;

import java.util.Map;

/**
 * 系统配置信息
 *
 */
public interface SysConfigService extends IService<SysConfigEntity> {

	PageUtils queryPage(Map<String, Object> params);
	

	void saveConfig(SysConfigEntity config);
	

	void update(SysConfigEntity config);
	

	void updateValueByKey(String key, String value);
	
	/**
	 * 删除配置信息
	 */
	void deleteBatch(Long[] ids);
	
	/**
	 * 根据key，获取配置的value值
	 */
	String getValue(String key);
	
	/**
	 * 根据key，获取value的Object对象
	 */
	<T> T getConfigObject(String key, Class<T> clazz);
	
}
