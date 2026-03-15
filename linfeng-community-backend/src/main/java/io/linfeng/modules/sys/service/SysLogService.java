package io.linfeng.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.sys.entity.SysLogEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;


/**
 * 系统日志
 *
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

    @Async
    void saveLog(String ip,long time,String username);

}
