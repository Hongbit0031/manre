/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.common.aspect;

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.modules.app.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 接口防重复操作处理
 * @author linfeng
 * @date 2022/4/7 11:15
 */
@Aspect
@Component
@Slf4j
@SuppressWarnings("all")
public class RepeatSubmitAspect {

    public static final String  KEYPREX="noRepeat:user:";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtUtils jwtUtils;
    /**
     * 进行接口防重复操作处理
     * @param pjp
     * @param noRepeatSubmit
     * @return
     */
    @Around("(execution(* io.linfeng.modules.app.controller.*.*(..)) || execution(* io.linfeng.modules.admin.controller.*.*(..))) && @annotation(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        //拿到token和请求路径
        StringBuilder sb = new StringBuilder();
        //获取token
        String token = request.getHeader(jwtUtils.getHeader());
        if(StringUtils.isBlank(token)){
            token = request.getParameter(jwtUtils.getHeader());
        }
        sb.append(KEYPREX).append(token).append(request.getRequestURI().toString());
        //获取现在时间
        long now = System.currentTimeMillis();
        if (redisTemplate.hasKey(sb.toString())){
            //上次请求时间
            long lastTime= Long.valueOf(redisTemplate.opsForValue().get(sb.toString()).toString()) ;
            // 如果现在距离上次提交时间小于设置的默认时间 则 判断为重复提交  否则 正常提交 -> 进入业务处理
            if ((now - lastTime)>noRepeatSubmit.lockTime()){
                //重新记录时间 10分钟过期时间
                redisTemplate.opsForValue().set(sb.toString(),String.valueOf(now),10, TimeUnit.MINUTES);
                //处理业务
                Object result =  pjp.proceed();
                return result;
            }else {
                throw new LinfengException("点太快啦，稍后再试");
            }
        }else {
            //第一次操作
            redisTemplate.opsForValue().set(sb.toString(),String.valueOf(now),10, TimeUnit.MINUTES);
            Object result =  pjp.proceed();
            return result;
        }
    }
}