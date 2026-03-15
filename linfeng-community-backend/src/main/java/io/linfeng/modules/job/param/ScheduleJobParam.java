package io.linfeng.modules.job.param;

import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定时任务参数类
 */
@Schema(description = "定时任务请求体")
@Data
public class ScheduleJobParam {

    @Schema(description = "任务ID，更新时必填")
    @NotNull(message = "任务ID不能为空", groups = UpdateGroup.class)
    private Long jobId;

    @Schema(description = "spring bean名称", required = true)
    @NotBlank(message = "bean名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String beanName;

    @Schema(description = "参数")
    private String params;

    @Schema(description = "cron表达式", required = true)
    @NotBlank(message = "cron表达式不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String cronExpression;

    @Schema(description = "任务状态  0：暂停   1：正常")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}

