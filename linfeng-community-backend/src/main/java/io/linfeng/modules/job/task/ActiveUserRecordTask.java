package io.linfeng.modules.job.task;

import cn.hutool.core.util.NumberUtil;
import io.linfeng.modules.admin.service.ActiveUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 访客记录清理定时任务
 *
 * 访客记录由于需要进行业务分析，所以存储在MySQL中，需要定期清理n天前的所有访客记录
 * 入参param就代表清理多少天前的访客记录
 * @author linfengtech
 * @date 2024/5/30 10:47
 */
@Component("activeUserRecordTask")
public class ActiveUserRecordTask implements ITask{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActiveUserService activeUserService;


    @Override
    public void run(String params) {
        logger.info("activeUserRecordTask定时任务正在执行，参数为：{}", params);
        //必须是整数而且为正数，代表清除几个月前的数据
        if(NumberUtil.isInteger(params)&&Integer.valueOf(params)>0){
            activeUserService.cleanRecordByDay(Integer.valueOf(params));
        }
    }
}
