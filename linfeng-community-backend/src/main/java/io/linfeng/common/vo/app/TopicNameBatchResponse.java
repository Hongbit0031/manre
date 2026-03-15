package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linfeng
 * @date 2023/8/4 13:45
 */
@Data
@Schema(description = "圈子名称响应体")
public class TopicNameBatchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "圈子名称")
    private String topicName;

}
