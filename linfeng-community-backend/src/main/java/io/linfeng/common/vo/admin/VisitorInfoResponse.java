package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "访客统计数据对象")
public class VisitorInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @Schema(description = "今天新注册用户")
    private Object newRegisterUserCount;

    @Schema(description = "昨日新增用户")
    private Object yesterdayNewUserNum;

    @Schema(description = "今日IP总数")
    private Object todayIp;

    @Schema(description = "昨日IP总数")
    private Object yesterdayIp;

    @Schema(description = "今日活跃用户总数")
    private Object todayUser;

    @Schema(description = "昨日活跃用户总数")
    private Object yesterdayUser;

    @Schema(description = "今日活跃总数")
    private Object totalActiveToday;

    @Schema(description = "昨日活跃总数")
    private Object totalActiveYesterday;






}
