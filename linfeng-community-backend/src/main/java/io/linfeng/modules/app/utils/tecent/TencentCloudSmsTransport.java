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
package io.linfeng.modules.app.utils.tecent;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.Constant;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 腾讯云短信
 *
 */
@Slf4j
@Service
public class TencentCloudSmsTransport {

    @Autowired
    private SysConfigService configSystemService;

    public void sendSmsCode(String mobileNo, String code) {
        try{
            String secretId = configSystemService.getValue(Constant.TENCENT_SECRET_ID);
            String secretKey = configSystemService.getValue(Constant.TENCENT_SECRET_KEY);
            Credential cred = new Credential(secretId, secretKey);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, configSystemService.getValue(Constant.TENCENT_SMS_REGION), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {mobileNo};
            req.setPhoneNumberSet(phoneNumberSet1);
            req.setSmsSdkAppId(configSystemService.getValue(Constant.TENCENT_SMS_SDK_APP_ID));
            req.setSignName(configSystemService.getValue(Constant.TENCENT_SMS_SIGN_NAME));
            req.setTemplateId(configSystemService.getValue(Constant.TENCENT_SMS_TEMPLATE_ID));
            String[] templateParamSet1 = {code};
            req.setTemplateParamSet(templateParamSet1);
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            log.info("手机号[{}]短信发送响应报文[{}]", mobileNo, SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            log.error("短信发送异常", e);
            throw new LinfengException("短信发送异常");
        }

    }

}
