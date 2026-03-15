
package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "关注")
public class AdminUserInfoParam {

    @Schema(description = "用户id")
    private Integer uid;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 0为普通用户  1官方账号 2马甲虚拟用户
     */
    @Schema(description = "0为普通用户  1官方账号 2马甲虚拟用户")
    private Integer type;

    /**
     *余额不修改1，修改0
     */
    @Schema(description = "余额不修改1，修改0")
    private Integer changeMoney;
    /**
     *余额 增加0 减少1
     */
    @Schema(description = "余额 增加0 减少1")
    private Integer upOrDown;
    /**
     * 余额增减数量
     */
    @Schema(description = "余额增减数量")
    private BigDecimal changeValue;

    /**
     *积分不修改1，修改0
     */
    @Schema(description = "积分不修改1，修改0")
    private Integer changeIntegral;
    /**
     *积分 增加0 减少1
     */
    @Schema(description = "积分 增加0 减少1")
    private Integer upOrDownIntegral;
    /**
     * 积分增减数量
     */
    @Schema(description = "积分增减数量")
    private Integer changeIntegralValue;
    /**
     * 是否设置为会员 0普通用户 1会员
     */
    private Integer vip;
    /**
     * 设置的会员天数
     */
    @Schema(description = "会员天数")
    private Integer vipValidDay;
    /**
     *会员不修改1，修改0
     */
    @Schema(description = "会员不修改1，修改0")
    private Integer changeVip;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "用户名")
    private String username;


    @Schema(description = "个性签名")
    private String intro;
}
