package io.linfeng.common.vo.admin;

import io.linfeng.modules.admin.entity.AppUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@Schema(description = "用户签到响应体")
public class AdminUserSignResponse implements Serializable {
	private static final long serialVersionUID = 1L;



	@Schema(description = "ID")
	private Long id;
	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	private Long uid;
	/**
	 * 签到说明
	 */
	@Schema(description = "签到说明")
	private String title;
	/**
	 * 获得积分
	 */
	@Schema(description = "获得积分")
	private Integer number;
	/**
	 * 剩余积分
	 */
	@Schema(description = "剩余积分")
	private Integer balance;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;


	@Schema(description = "用户信息")
	private AppUserEntity user;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "昵称")
	private String username;
}
