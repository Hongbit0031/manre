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
package io.linfeng.modules.app.utils.baidu;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.Constant;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author linfeng
 * @date 2024/7/17 10:36
 */
@Service
@Slf4j
public class BaiduCensorTransport {


    protected SysConfigService configSystemService;

    protected BaiduCensorTransport(SysConfigService configSystemService) {
        this.configSystemService = configSystemService;
    }

    /**
     * 文本审核
     * 文档：https://ai.baidu.com/ai-doc/ANTIPORN/tk3h6xdji
     * 审核结果类型，conclusionType可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
     * error_code错误提示码，失败才返回，成功不返回
     * @param text
     */
    public void textCensor(String text){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.textCensorUserDefined(text);
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            throw new LinfengException("内容审核服务异常");
        }
        if(jsonObject.getInt("conclusionType") == 2){
            throw new LinfengException("文本包含违规内容");
        }
        //疑似违规可处理，可不处理，看自己需求
        if(jsonObject.getInt("conclusionType") == 3){
            throw new LinfengException("内容疑似违规");
        }
    }

    /**
     * 文本审核
     * @param text
     * @return 异常 false   正常 true
     */
    public boolean textCensorCheck(String text){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.textCensorUserDefined(text);
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            return true;
        }
        if(jsonObject.getInt("conclusionType") == 2){
            return false;
        }
        if(jsonObject.getInt("conclusionType") == 3){
            return false;
        }
        return true;
    }



    public void imageCensor(String url){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.imageCensorUserDefined(url, EImgType.URL, new HashMap<>());
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            throw new LinfengException("内容审核服务异常");
        }
        if(jsonObject.getInt("conclusionType") == 2){
            throw new LinfengException("图片包含违规内容");
        }
        if(jsonObject.getInt("conclusionType") == 3){
            throw new LinfengException("内容疑似违规");
        }
    }

    public boolean imageCensorCheck(String url){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.imageCensorUserDefined(url, EImgType.URL, new HashMap<>());
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            return true;
        }
        if(jsonObject.getInt("conclusionType") == 2){
            return false;
        }
        if(jsonObject.getInt("conclusionType") == 3){
            return false;
        }
        return true;
    }



    public void videoCensor(String url){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.videoCensorUserDefined(null, url, null , new HashMap<>());
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            throw new LinfengException("内容审核服务异常");
        }
        if(jsonObject.getInt("conclusionType") == 2){
            throw new LinfengException("视频包含违规内容");
        }
        if(jsonObject.getInt("conclusionType") == 3){
            throw new LinfengException("内容疑似违规");
        }
    }

    public boolean videoCensorCheck(String url){
        AipContentCensor client = getAipContentCensor();
        JSONObject jsonObject = client.videoCensorUserDefined(null, url, null , new HashMap<>());
        if(!jsonObject.isNull("error_code")){
            log.info("内容审核响应报文[{}]", jsonObject.toString());
            return true;
        }
        if(jsonObject.getInt("conclusionType") == 2){
            return false;
        }
        if(jsonObject.getInt("conclusionType") == 3){
            return false;
        }
        return true;
    }

    private AipContentCensor getAipContentCensor() {
        String appId = configSystemService.getValue(Constant.BAIDU_APP_ID);
        String apiKey = configSystemService.getValue(Constant.BAIDU_API_KEY);
        String secretKey = configSystemService.getValue(Constant.BAIDU_SECRET_KEY);
        return new AipContentCensor(appId, apiKey, secretKey);
    }

}