package io.linfeng.common.vo.admin;

import io.linfeng.modules.admin.entity.AppUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@Schema(description = "管理端转盘抽奖记录响应体")
public class AdminLuckdrawRecordResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Schema(description = "ID")
	private Integer id;
	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private Integer userId;
	/**
	 * 奖品ID
	 */
	@Schema(description = "奖品ID")
	private Integer prizeId;
	/**
	 * 奖品类型
	 */
	@Schema(description = "奖品类型")
	private Integer prizeType;
	/**
	 * 奖品名称
	 */
	@Schema(description = "奖品名称")
	private String prizeName;
	/**
	 * 奖品图片
	 */
	@Schema(description = "奖品图片")
	private String prizeImage;
	/**
	 * 获得数量
	 */
	@Schema(description = "获得数量")
	private Integer number;
	/**
	 * 抽奖时间
	 */
	@Schema(description = "抽奖时间")
	private Date createTime;

	@Schema(description = "用户信息")
	private AppUserEntity user;

	@Schema(description = "头像")
	private String avatar;
}
