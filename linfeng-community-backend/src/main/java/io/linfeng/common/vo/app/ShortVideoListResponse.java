package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短视频列表响应体")
public class ShortVideoListResponse extends PostListResponse {

	@Schema(description = "帖子id")
	private String _id;

	@Schema(description = "状态")
	private String state;

	@Schema(description = "封面加载")
	private boolean playIng;

	@Schema(description = "视频是否禁音 默认false")
	private boolean isplay;

	@Schema(description = "播放源")
	private String src;
}
