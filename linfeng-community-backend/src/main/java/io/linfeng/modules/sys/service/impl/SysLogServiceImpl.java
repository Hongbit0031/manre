package io.linfeng.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.IPUtil;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;
import io.linfeng.modules.sys.dao.SysLogDao;
import io.linfeng.modules.sys.entity.SysLogEntity;
import io.linfeng.modules.sys.service.SysLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");
        String ip = (String)params.get("ip");
        QueryWrapper<SysLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(key),SysLogEntity::getUsername, key)
                .or()
                .eq(StringUtils.isNotBlank(key),SysLogEntity::getOperation, key);
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(ip),SysLogEntity::getIp, ip);
        queryWrapper.lambda().orderByDesc(SysLogEntity::getId);
        IPage<SysLogEntity> page = this.page(
            new Query<SysLogEntity>().getPage(params),
            queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveLog(String ip,long time, String username) {
        SysLogEntity sysLog = new SysLogEntity();
        sysLog.setOperation("后台登录");
        sysLog.setIp(ip);
        sysLog.setUsername(username);
        sysLog.setCreateDate(new Date());
        sysLog.setTime(time);
        sysLog.setAddress(IPUtil.getCityInfo(ip));
        //保存系统日志
        this.save(sysLog);
    }
}
