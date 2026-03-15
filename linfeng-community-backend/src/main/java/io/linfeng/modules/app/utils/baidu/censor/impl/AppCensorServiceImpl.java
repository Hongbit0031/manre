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
package io.linfeng.modules.app.utils.baidu.censor.impl;

import io.linfeng.common.utils.Constant;
import io.linfeng.modules.app.utils.baidu.BaiduCensorTransport;
import io.linfeng.modules.app.utils.baidu.censor.AppCensorService;
import io.linfeng.modules.sys.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 内容审核服务
 *
 * @author JiangCX
 * @date 2024-07-17 13:47:37
 */
@Service("appCensorService")
public class AppCensorServiceImpl implements AppCensorService {

    protected final SysConfigService configSystemService;


    private final BaiduCensorTransport baiduCensorTransport;

    public AppCensorServiceImpl(SysConfigService configSystemService, BaiduCensorTransport baiduCensorTransport) {
        this.configSystemService = configSystemService;
        this.baiduCensorTransport = baiduCensorTransport;
    }

    @Override
    public void censorText(String text) {
        boolean baiduCensorTextOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_TEXT_OPEN).equals("1");
        if(!baiduCensorTextOpen){
            return;
        }

        baiduCensorTransport.textCensor(text);

    }

    @Override
    public boolean censorTextCheck(String text) {
        boolean baiduCensorTextOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_TEXT_OPEN).equals("1");
        if(!baiduCensorTextOpen){
            return true;
        }
        return baiduCensorTransport.textCensorCheck(text);
    }

    @Override
    public void censorImage(String url) {
        boolean baiduCensorImageOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_IMAGE_OPEN).equals("1");
        if(!baiduCensorImageOpen){
            return;
        }

        baiduCensorTransport.imageCensor(url);

    }

    @Override
    public boolean censorImageCheck(String url) {
        boolean baiduCensorImageOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_IMAGE_OPEN).equals("1");
        if(!baiduCensorImageOpen){
            return true;
        }
        return baiduCensorTransport.imageCensorCheck(url);
    }


    @Override
    public void censorVideo(String url) {
        boolean baiduCensorVideoOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_VIDEO_OPEN).equals("1");
        if(!baiduCensorVideoOpen){
            return;
        }

        baiduCensorTransport.videoCensor(url);

    }

    @Override
    public boolean censorVideoCheck(String url) {
        boolean baiduCensorVideoOpen = configSystemService.getValue(Constant.BAIDU_CENSOR_VIDEO_OPEN).equals("1");
        if(!baiduCensorVideoOpen){
            return true;
        }

        return baiduCensorTransport.videoCensorCheck(url);
    }
}