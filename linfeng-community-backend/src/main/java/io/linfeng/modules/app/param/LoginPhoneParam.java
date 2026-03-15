package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 手机号授权
 * @author linfeng
 * @date 2022/11/1 13:42
 */
@Data
@Schema(description = "手机号授权")
public class LoginPhoneParam {

    /**
     * 微信openID
     */
    @Schema(description = "微信openID")
    private String wechatOpenId;

    /**
     * 加密秘钥
     */
    @Schema(description = "加密秘钥")
    private String sessionKey;

    /**
     * 加密数据
     */
    @Schema(description = "加密数据")
    private String encryptedData;

    /**
     * 加密算法初始向量
     */
    @Schema(description = "加密算法初始向量")
    private String iv;

}