/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.dao;

import io.linfeng.modules.admin.entity.ActiveUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户访问统计
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2024-05-29 12:25:03
 */
@Mapper
public interface ActiveUserDao extends BaseMapper<ActiveUserEntity> {
	
}
