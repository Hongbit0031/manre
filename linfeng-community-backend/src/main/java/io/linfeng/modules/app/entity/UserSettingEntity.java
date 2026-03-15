package io.linfeng.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户隐私设置表
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-24 15:17:15
 */
@Data
@TableName("lf_user_setting")
public class UserSettingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer uid;
	/**
	 * 隐藏粉丝 0否1是
	 */
	private Integer isFollow;
	/**
	 * 隐藏关注 0否1是
	 */
	private Integer isWatch;
	/**
	 * 隐藏作品 0否1是
	 */
	private Integer isPost;

}
