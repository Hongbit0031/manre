/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.app.entity.NoticeEntity;
import io.linfeng.modules.app.param.ReadNoticeForm;
import io.linfeng.modules.app.param.RejectNoticeForm;

import java.util.Map;

/**
 * IM通知
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-16 15:04:19
 */
public interface NoticeService extends IService<NoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<String,Object> getNoticeList(Integer uid);

    void readById(ReadNoticeForm param);

    void reject(RejectNoticeForm param);
}

