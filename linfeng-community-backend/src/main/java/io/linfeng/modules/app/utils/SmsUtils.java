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
package io.linfeng.modules.app.utils;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import io.linfeng.common.utils.Constant;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云
 * 手机短信发送
 * @author linfeng
 * @date 2022/4/24 12:57
 */
@Slf4j
@Configuration
public class SmsUtils {


    @Autowired
    private SysConfigService configService;


    /**
     * 发送短信
     * @param phoneNumbers 手机号
     * @param templateParam 短信模板变量对应的实际值，JSON格式
     */
    public void sendSms(String phoneNumbers, String templateParam) throws ClientException {
        String regionId = configService.getValue(Constant.SMS_REGION);
        String accessKeyId = configService.getValue(Constant.SMS_KEY_ID);
        String accessKeySecret = configService.getValue(Constant.SMS_KEY_SECRET);
        String sign = configService.getValue(Constant.SMS_SIGN);
        String templateId = configService.getValue(Constant.SMS_TEMPLATE_ID);

        DefaultProfile profile = DefaultProfile.getProfile(
                regionId,
                accessKeyId,
                accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();

        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", sign);
        request.putQueryParameter("TemplateCode", templateId);
        request.putQueryParameter("TemplateParam", templateParam);
        CommonResponse response = client.getCommonResponse(request);
        log.info(response.getData());
    }



}
