package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author linfeng
 * @date 2023/6/26 15:55
 */
@Data
@Schema(description = "用户入圈申请响应体")
public class ApplyInfoResponse {

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户id")
    private Integer uid;

    @Schema(description = "圈子id")
    private Integer topicId;

    @Schema(description = "状态0待审核1已通过2已拒绝")
    private Integer status;

    @Schema(description = "回答")
    private String answer;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "审核用户id")
    private Integer operateId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "用户信息")
    private AppUserShortInfoResponse userInfo;

}
