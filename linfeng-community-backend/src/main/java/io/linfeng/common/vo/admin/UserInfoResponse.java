package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "用户响应对象")
public class UserInfoResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "用户id")
	private Integer uid;

	@Schema(description = "手机号")
	private String mobile;

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "性别(0未知，1男，2女)")
	private Integer gender;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "个性签名")
	private String intro;

	@Schema(description = "用户标签")
	private String tagStr;

	@Schema(description = "0为普通用户  1官方账号 2马甲虚拟用户")
	private Integer type;

	@Schema(description = "是否为圈主或管理员")
	private Boolean isAdmin;

	@Schema(description = "是否关注")
	private Integer hasFollow;

	@Schema(description = "用户等级")
	private Integer level;
}
