package io.linfeng.modules.oss.factory;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 本地存储配置信息
 *
 */
@Schema(description = "本地存储配置信息信息请求参数对象")
@Data
public class LocalStorageConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "本地存储策略0云存储1本地存储")
    private String local;
    @Schema(description = "本地存储后端路径")
    private String localUrl;

}
