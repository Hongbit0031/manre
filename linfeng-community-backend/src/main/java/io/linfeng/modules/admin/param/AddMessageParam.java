package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 13:15:30
 */
@Data
@Schema(description = "消息发送请求体")
public class AddMessageParam implements Serializable {


	/**
	 * 接收者uid
	 */
	@Schema(description = "接收者uid")
	private String toUid;

	/**
	 * 推送标题
	 */
	@Schema(description = "推送标题")
	private String title;
	/**
	 * 消息内容
	 */
	@Schema(description = "消息内容")
	private String content;


}
