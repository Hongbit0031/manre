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
package io.linfeng.modules.app.utils.baidu.censor;

/**
 * 内容审核服务
 *
 * @author JiangCX
 * @date 2024-07-17 13:40:17
 */
public interface AppCensorService {

    /**
     * 审核文本
     * @param text 文本内容
     */
    void censorText(String text);

    /**
     * 审核文本
     * @param text 文本内容
     * @return  异常 false   正常 true
     */
    boolean censorTextCheck(String text);

    /**
     * 审核图片
     * @param url 图片url
     */
    void censorImage(String url);

    /**
     * 审核图片
     * @param url 图片url
     * @return  异常 false   正常 true
     */
    boolean censorImageCheck(String url);


    /**
     * 审核视频
     * @param url 视频url
     */
    void censorVideo(String url);

    /**
     * 审核视频
     * @param url 视频url
     * @return  异常 false   正常 true
     */
    boolean censorVideoCheck(String url);
}
