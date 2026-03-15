<template>
    <div class="topPadding">
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="微信设置" name="first">
                <div class="app-container">
                    <el-form
                        :model="form"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="微信小程序AppId">
                            <el-input
                                v-model="form.WxAppId"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="微信小程序密钥">
                            <el-input
                                v-model="form.wxAppSecret"
                                style="width: 370px"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="微信公众号AppId">
                            <el-input
                                v-model="form.WxMpAppId"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="微信公众号密钥">
                            <el-input
                                v-model="form.WxMpSecret"
                                style="width: 370px"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="APP微信支付ID">
                            <el-input
                                v-model="form.app_appid"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="微信支付商户号">
                            <el-input
                                v-model="form.wxPayKey"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="微信支付商户秘钥">
                            <el-input
                                v-model="form.wxPaySecret"
                                style="width: 370px"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="微信支付回调地址">
                            <el-input
                                v-model="form.appNotifyurl"
                                style="width: 370px"
                                placeholder="例如: https://api.xxx.com/app/pay/rolBack"
                            />
                        </el-form-item>
                        <p class="formInfos">
                            支付回调地址= api接口 + /app/pay/rolBack
                        </p>
                        <el-form-item label="h5支付后跳转地址">
                            <el-input
                                v-model="form.redirectUrl"
                                style="width: 370px"
                                placeholder="例如: https://www.linfeng.tech/#/pages/bill/bill?types=0"
                            />
                        </el-form-item>
                        <p class="formInfos">
                            h5支付之后跳转地址= h5端访问域名 +
                            /#/pages/bill/bill?types=0
                        </p>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="短信设置" name="second">
                <div class="app-container">
                    <el-form :model="form2" label-width="150px">
                        <el-form-item label="短信开启">
                            <el-radio v-model="form2.sms_open" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.sms_open" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px" v-if="form2.sms_open==1">
                           短信关闭后验证码会自动返回给用户端
                        </p>
                        
                        <el-form-item label="短信厂商">
                            <el-radio v-model="form2.chooseSms" :label="0"
                                >阿里云</el-radio
                            >
                            <el-radio v-model="form2.chooseSms" :label="1"
                                >腾讯云</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="阿里云模板id" v-if="form2.chooseSms==0">
                            <el-input
                                v-model="form2.sms_templateId"
                                style="width: 300px"
                            />
                        </el-form-item>
                        <el-form-item label="签名" v-if="form2.chooseSms==0">
                            <el-input
                                v-model="form2.sms_sign"
                                style="width: 300px"
                            />
                        </el-form-item>
                        <el-form-item label="地区" v-if="form2.chooseSms==0">
                            <el-input
                                v-model="form2.sms_region"
                                style="width: 300px"
                            />
                        </el-form-item>
                        <el-form-item label="短信key" v-if="form2.chooseSms==0">
                            <el-input
                                v-model="form2.sms_access_key"
                                style="width: 300px"
                            />
                        </el-form-item>
                        <el-form-item label="短信密钥" v-if="form2.chooseSms==0">
                            <el-input
                                v-model="form2.sms_access_secret"
                                style="width: 300px"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="腾讯云SecretId" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSecretId"
                                style="width: 350px"
                            />
                        </el-form-item>
                        <el-form-item label="腾讯云SecretKey" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSecretKey"
                                style="width: 350px"
                            />
                        </el-form-item>
                        <el-form-item label="腾讯云短信地域" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSmsRegion"
                                style="width: 350px"
                            />
                        </el-form-item>
                        <el-form-item label="短信SdkAppId" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSmsSdkAppId"
                                style="width: 350px"
                            />
                        </el-form-item>
                        <el-form-item label="腾讯云短信签名" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSmsSignName"
                                style="width: 350px"
                            />
                        </el-form-item>
                        <el-form-item label="腾讯云短信模板ID" v-if="form2.chooseSms==1">
                            <el-input
                                v-model="form2.tencentSmsTemplateId"
                                style="width: 350px"
                            />
                        </el-form-item>


                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form2)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="帖子设置" name="third">
                <div class="app-container">
                    <el-form :model="form" label-width="150px">
                        <el-form-item label="普通贴人工审核">
                            <el-radio v-model="form.normalPost" :label="0"
                                >人工审核</el-radio
                            >
                            <el-radio v-model="form.normalPost" :label="1"
                                >自动过审</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="付费贴人工审核">
                            <el-radio v-model="form.vipPost" :label="0"
                                >人工审核</el-radio
                            >
                            <el-radio v-model="form.vipPost" :label="1"
                                >自动过审</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="付费贴发布开关">
                            <el-radio v-model="form.payPostbtn" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form.payPostbtn" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="付费贴发布权限">
                            <el-radio v-model="form.payPostVip" :label="0"
                                >所有用户</el-radio
                            >
                            <el-radio v-model="form.payPostVip" :label="1"
                                >仅会员可发布</el-radio
                            >
                        </el-form-item>

                        <el-form-item label="付费贴抽成 (%)">
                            <el-input
                                v-model="form.postPrice"
                                style="width: 200px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="评论审核开关">
                            <el-radio v-model="form.commentCheck" :label="0"
                                >直接过审</el-radio
                            >
                            <el-radio v-model="form.commentCheck" :label="1"
                                >需要审核</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="圈子审核开关">
                            <el-radio v-model="form.circleCheck" :label="0"
                                >直接过审</el-radio
                            >
                            <el-radio v-model="form.circleCheck" :label="2"
                                >需要审核</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="前台设置" name="four">
                <div class="app-container">
                    <el-form :model="form2" label-width="150px">
                        <el-form-item label="视频入口开关">
                            <el-radio v-model="form2.isOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.isOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="充值入口开关">
                            <el-radio v-model="form2.chargeIsOpen" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.chargeIsOpen" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="提现入口开关">
                            <el-radio v-model="form2.canCashOut" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.canCashOut" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="积分兑换余额开关">
                            <el-radio v-model="form2.exchange" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.exchange" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="余额兑换积分开关">
                            <el-radio v-model="form2.yuetoint" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.yuetoint" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="积分兑换余额比例">
                            <el-input
                                v-model="form2.integral"
                                style="width: 200px"
                                type="number"
                            />
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px">
                            兑换一块钱需要的积分数 必须为整数
                        </p>
                        <el-form-item label="余额兑换积分比例">
                            <el-input
                                v-model="form2.yuetointratio"
                                style="width: 200px"
                                type="number"
                            />
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px">
                            一块钱能兑换的积分数 必须为整数
                        </p>
                        <el-form-item label="广场页公告">
                            <el-input
                                v-model="form2.noticeContent"
                                style="width: 400px"
                            />
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px">
                            如果不填写就不显示
                        </p>
                        <el-form-item label="项目logo" prop="coverImage">
                            <el-upload
                                class="avatar-uploader"
                                :action="url"
                                :show-file-list="false"
                                :on-success="handleIconSuccess"
                            >
                                <img
                                    v-if="form2.img"
                                    :src="form2.img"
                                    class="avatar"
                                />
                                <i
                                    v-else
                                    class="el-icon-plus avatar-uploader-icon"
                                />
                            </el-upload>
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px">
                            建议尺寸：200*200像素，jpg、png图片类型
                        </p>
                        <el-form-item
                            label="用户个人页背景图"
                            prop="coverImage"
                        >
                            <el-upload
                                class="avatar-uploader"
                                :action="url"
                                :show-file-list="false"
                                :on-success="handleIconSuccess3"
                            >
                                <img
                                    v-if="form2.bgImg"
                                    :src="form2.bgImg"
                                    class="avatar2"
                                />
                                <i
                                    v-else
                                    class="el-icon-plus avatar-uploader-icon"
                                />
                            </el-upload>
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 80px">
                            建议尺寸：750*400像素，jpg、png图片类型
                        </p>
                        <el-form-item label="帖子详情页打赏开关">
                            <el-radio v-model="form2.rewardBtn" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.rewardBtn" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="好友聊天消息入口">
                            <el-radio v-model="form2.socialBtn" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form2.socialBtn" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form2)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="客服设置" name="five">
                <div class="app-container">
                    <el-form :model="form2" label-width="150px">
                        <el-form-item label="客服工作时间">
                            <el-input
                                v-model="form3.contactTime"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="客服微信号">
                            <el-input
                                v-model="form3.contactWechat"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="客服电话">
                            <el-input
                                v-model="form3.contactPhone"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item
                            label="客服微信二维码"
                            prop="contactWechatQr"
                        >
                            <el-upload
                                class="avatar-uploader"
                                :action="url"
                                :show-file-list="false"
                                :on-success="handleIconSuccess2"
                            >
                                <img
                                    v-if="form3.contactWechatQr"
                                    :src="form3.contactWechatQr"
                                    class="avatar"
                                />
                                <i
                                    v-else
                                    class="el-icon-plus avatar-uploader-icon"
                                />
                            </el-upload>
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 30px">
                            建议尺寸：430*430像素，jpg、png图片类型
                        </p>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form3)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="抽奖设置" name="six">
                <div class="app-container">
                    <el-form :model="form4" label-width="150px">
                        <el-form-item label="抽奖是否开启">
                            <el-radio v-model="form4.luckDrawStatus" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form4.luckDrawStatus" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="每次抽奖消耗积分数">
                            <el-input
                                v-model="form4.luckDrawIntegral"
                                style="width: 250px"
                                type="number"
                            />
                        </el-form-item>

                        <el-form-item label="每天抽奖次数">
                            <el-input
                                v-model="form4.surplus"
                                style="width: 250px"
                                type="number"
                            />
                        </el-form-item>

                        <el-form-item label="抽奖规则">
                            <el-input
                                v-model="form4.luckDrawRule"
                                style="width: 500px"
                                type="textarea"
                                :autosize="{ minRows: 5, maxRows: 50 }"
                            />
                        </el-form-item>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form4)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="注册设置" name="seven">
                <div class="app-container">
                    <el-form :model="form4" label-width="200px">
                        <el-form-item label="微信小程序登录方式">
                            <el-radio v-model="form5.loginType" :label="0"
                                >openid一键登录</el-radio
                            >
                            <el-radio v-model="form5.loginType" :label="1"
                                >手机号登录</el-radio
                            >
                        </el-form-item>

                        <el-form-item label="小程序登录头像昵称弹框">
                            <el-radio v-model="form5.loginInfoPop" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form5.loginInfoPop" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="邮箱登录是否开启">
                            <el-radio v-model="form5.emailLogin" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form5.emailLogin" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 120px" v-if="form5.emailLogin==1">
                            邮箱登录开启后请在后端yml文件中配置邮箱相关参数
                        </p>
                        <el-form-item label="允许邮箱注册账号" v-if="form5.emailLogin==1">
                            <el-radio v-model="form5.openEmailRegister" :label="1"
                                >允许</el-radio
                            >
                            <el-radio v-model="form5.openEmailRegister" :label="0"
                                >禁止</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="注册默认头像" prop="defaultHead">
                            <el-upload
                                class="avatar-uploader"
                                :action="url"
                                :show-file-list="false"
                                :on-success="handleIconSuccess4"
                            >
                                <img
                                    v-if="form5.defaultHead"
                                    :src="form5.defaultHead"
                                    class="avatar"
                                />
                                <i
                                    v-else
                                    class="el-icon-plus avatar-uploader-icon"
                                />
                            </el-upload>
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 120px">
                            建议尺寸：200*200像素，jpg、png图片类型
                        </p>
                        
                        <el-form-item label="个人隐私协议" prop="privacy">
                            <TextEditor ref="childRef3" />
                        </el-form-item>
                        <el-form-item label="用户服务协议" prop="protocol">
                            <TextEditor ref="childRef2" />
                        </el-form-item>
                        <el-form-item label="社区规范协议" prop="standard">
                            <TextEditor ref="childRef" />
                        </el-form-item>
                        <p class="formInfos" style="margin-left: 120px">内容不要超过5000字符</p>
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form5)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="会员设置" name="eight">
                <div class="app-container">
                    <el-form
                        :model="form4"
                        label-width="200px"
                        label-position="left"
                    >
                        <el-form-item label="会员充值协议">
                            <el-input
                                v-model="form6.vipAgreeContent"
                                style="width: 500px"
                                type="textarea"
                                autosize
                            />
                        </el-form-item>
                        <p class="formInfos">内容不要超过1500字符</p>
                        <el-form-item label="会员积分奖励翻倍数">
                            <el-input
                                v-model="form6.vipIntegral"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="发帖积分奖励">
                            <el-input
                                v-model="form6.addPostIntegral"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="每天限制发帖积分奖励次数">
                            <el-input
                                v-model="form6.addPostIntegralLimit"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="会员每月改名次数">
                            <el-input
                                v-model="form6.vipRename"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="普通用户每月改名数">
                            <el-input
                                v-model="form6.commonRename"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="会员可创建圈子数">
                            <el-input
                                v-model="form6.vipTopicNumber"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="普通用户可创建圈子">
                            <el-input
                                v-model="form6.commonTopicNumber"
                                style="width: 180px"
                                type="number"
                            />
                        </el-form-item>
                        <el-form-item label="私密圈创建开关">
                            <el-radio v-model="form6.privateCirclesOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form6.privateCirclesOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="私密圈创建权限">
                            <el-radio v-model="form6.privateCircleNeedVip" :label="0"
                                >所有人可创建</el-radio
                            >
                            <el-radio v-model="form6.privateCircleNeedVip" :label="1"
                                >仅会员可创建</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="会员入口总开关">
                            <el-radio v-model="form6.vipShow" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form6.vipShow" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="IOS端虚拟支付开关">
                            <el-radio v-model="form6.iosClose" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form6.iosClose" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <p class="formInfos">微信小程序苹果手机端不支持会员充值等虚拟支付业务，此按钮可一键设置IOS端相关功能开关</p>

                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form6)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="流量主广告" name="nine">
                <div class="app-container">
                    <el-form
                        :model="form7"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="流量主广告是否开启">
                            <el-radio v-model="form7.adIsOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="form7.adIsOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="微信小程序adpid">
                            <el-input
                                v-model="form7.wxAdpid"
                                style="width: 200px"
                            />
                        </el-form-item>
                        <el-form-item label="H5的adpid">
                            <el-input
                                v-model="form7.h5Adpid"
                                style="width: 200px"
                            />
                        </el-form-item>

                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form7)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="页面配置" name="ten">
                <div class="app-container">
                    <el-form
                        :model="form8"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="首页-最新-帖子样式">
                            <el-radio v-model="form8.indexStyle1" :label="0"
                                >普通列表-暗黑系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle1" :label="2"
                                >普通列表-经典系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle1" :label="1"
                                >瀑布流列表</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="首页-圈子-帖子样式">
                            <el-radio v-model="form8.indexStyle2" :label="0"
                                >普通列表-暗黑系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle2" :label="2"
                                >普通列表-经典系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle2" :label="1"
                                >瀑布流列表</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="首页-关注-帖子样式">
                            <el-radio v-model="form8.indexStyle3" :label="0"
                                >普通列表-暗黑系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle3" :label="2"
                                >普通列表-经典系列</el-radio
                            >
                            <el-radio v-model="form8.indexStyle3" :label="1"
                                >瀑布流列表</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="帖子详情页-布局样式">
                            <el-radio v-model="form8.showType" :label="0"
                                >格子布局</el-radio
                            >
                            <el-radio v-model="form8.showType" :label="1"
                                >轮播布局</el-radio
                            >
                        </el-form-item>
                        <div style="display: flex">
                            <p
                                style="
                                    font-size: 16px;
                                    font-weight: 500;
                                    color: #000;
                                    margin-right: 10px;
                                "
                            >
                                帖子列表页样式区别
                            </p>
                            <el-button
                                type="primary"
                                text
                                size="small"
                                style="margin-top: 14px"
                                @click="showListStyle = true"
                                >点击查看</el-button
                            >
                        </div>
                        <div style="display: flex">
                            <p
                                style="
                                    font-size: 16px;
                                    font-weight: 500;
                                    color: #000;
                                    margin-right: 10px;
                                "
                            >
                                帖子详情页样式区别
                            </p>
                            <el-button
                                type="primary"
                                text
                                size="small"
                                style="margin-top: 14px"
                                @click="showListStyle2 = true"
                                >点击查看</el-button
                            >
                        </div>

                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form8)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                    <el-dialog
                        title="帖子列表样式区别"
                        v-model="showListStyle"
                        width="70%"
                    >
                        <el-row>
                            <el-col :span="8">
                                <el-card :body-style="{ padding: '0px' }">
                                    <img
                                        src="~@/assets/setting/post-style1.png"
                                        class="image"
                                    />
                                    <div style="padding: 20px">
                                        <span class="tit"
                                            >普通列表-暗黑系列</span
                                        >
                                        <div class="bottom clearfix">
                                            <time class="time"
                                                >显示圈子，不显示浏览量，暗黑风格</time
                                            >
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                            <el-col :span="8">
                                <el-card :body-style="{ padding: '0px' }">
                                    <img
                                        src="~@/assets/setting/post-style2.png"
                                        class="image"
                                    />
                                    <div style="padding: 20px">
                                        <span class="tit">瀑布流列表</span>
                                        <div class="bottom clearfix">
                                            <time class="time"
                                                >帖子呈现瀑布流形式，内容呈现简洁</time
                                            >
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                            <el-col :span="8">
                                <el-card :body-style="{ padding: '0px' }">
                                    <img
                                        src="~@/assets/setting/post-style3.png"
                                        class="image"
                                    />
                                    <div style="padding: 20px">
                                        <span class="tit"
                                            >普通列表-经典系列</span
                                        >
                                        <div class="bottom clearfix">
                                            <time class="time"
                                                >不显示圈子，显示浏览量，经典风格</time
                                            >
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
                        <template v-slot:footer>
                            <span class="dialog-footer">
                                <el-button @click="showListStyle = false"
                                    >取 消</el-button
                                >
                                <el-button
                                    type="primary"
                                    @click="showListStyle = false"
                                    >确 定</el-button
                                >
                            </span>
                        </template>
                    </el-dialog>

                    <el-dialog
                        title="帖子详情页样式区别"
                        v-model="showListStyle2"
                        width="70%"
                    >
                        <el-row>
                            <el-col :span="8">
                                <el-card
                                    :body-style="{
                                        padding: '0px',
                                        marginRight: '15px',
                                    }"
                                >
                                    <img
                                        src="~@/assets/setting/detail1.png"
                                        class="image2"
                                    />
                                    <div style="padding: 20px">
                                        <span class="tit">轮播布局</span>
                                        <div class="bottom clearfix">
                                            <time class="time"
                                                >图文贴图片呈现轮播布局</time
                                            >
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                            <el-col :span="8">
                                <el-card
                                    :body-style="{
                                        padding: '0px',
                                        marginLeft: '15px',
                                    }"
                                >
                                    <img
                                        src="~@/assets/setting/detail2.png"
                                        class="image2"
                                    />
                                    <div style="padding: 20px">
                                        <span class="tit">格子布局</span>
                                        <div class="bottom clearfix">
                                            <time class="time"
                                                >图文贴图片呈现格子布局</time
                                            >
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
                        <template v-slot:footer>
                            <span class="dialog-footer">
                                <el-button @click="showListStyle2 = false"
                                    >取 消</el-button
                                >
                                <el-button
                                    type="primary"
                                    @click="showListStyle2 = false"
                                    >确 定</el-button
                                >
                            </span>
                        </template>
                    </el-dialog>
                </div>
            </el-tab-pane>
            <el-tab-pane label="弹框公告" name="eleven">
                <div class="app-container">
                    <el-form
                        :model="form9"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="首页-弹框是否开启">
                            <el-radio v-model="form9.popupOpen" :label="0"
                                >开启</el-radio
                            >
                            <el-radio v-model="form9.popupOpen" :label="1"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="弹框标题">
                            <el-input
                                v-model="form9.popTitle"
                                style="width: 200px"
                            />
                        </el-form-item>
                        <el-form-item label="弹框内容">
                            <el-input
                                v-model="form9.popContent"
                                style="width: 500px"
                                type="textarea"
                            />
                        </el-form-item>
                        <el-form-item label="展示频率/天">
                            <el-input
                                v-model="form9.popTime"
                                style="width: 200px"
                                type="number"
                            />
                        </el-form-item>
                        <p class="formInfos">
                            展示频率表示出现弹框公告间隔的最短天数
                        </p>

                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(form9)"
                                >提交</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="内容审核" name="twelve">
                <div class="app-container">
                    <el-form
                        :model="baiduForm"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="百度AppId">
                            <el-input
                                v-model="baiduForm.baiduAppId"
                                style="width: 370px"
                            />
                        </el-form-item>
                        <el-form-item label="百度ApiKey">
                            <el-input
                                v-model="baiduForm.baiduApiKey"
                                style="width: 370px"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="百度SecretKey">
                            <el-input
                                v-model="baiduForm.baiduSecretKey"
                                style="width: 370px"
                                 type="password"
                            />
                        </el-form-item>
                        <el-form-item label="百度文本审核开关">
                            <el-radio v-model="baiduForm.baiduCensorTextOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="baiduForm.baiduCensorTextOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        <el-form-item label="百度图片审核开关">
                            <el-radio v-model="baiduForm.baiduCensorImageOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="baiduForm.baiduCensorImageOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        
                        <el-form-item label="百度视频审核开关">
                            <el-radio v-model="baiduForm.baiduCensorVideoOpen" :label="1"
                                >开启</el-radio
                            >
                            <el-radio v-model="baiduForm.baiduCensorVideoOpen" :label="0"
                                >关闭</el-radio
                            >
                        </el-form-item>
                        
                        <el-form-item label="">
                            <el-button type="primary" @click="doSubmit(baiduForm)"
                                >确定保存</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
            <el-tab-pane label="工具栏" name="thirteen">
                <div class="app-container">
                    <el-form
                        :model="form10"
                        label-width="150px"
                        label-position="left"
                    >
                        <el-form-item label="视频贴管理">
                            <!-- <el-radio v-model="form10.status" :label="2"
                                >全部下架</el-radio
                            > -->
                            <el-radio v-model="form10.status" :label="0"
                                >全部上架</el-radio
                            >
                            <el-radio v-model="form10.status" :label="1"
                                >全部待审核</el-radio
                            >
                        </el-form-item>
                        
                        <el-form-item label="">
                            <el-button type="primary" @click="doJob(form10)"
                                >确定执行</el-button
                            >
                        </el-form-item>
                    </el-form>
                </div>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import TextEditor from "@/components/content/textEdit.vue";
const { proxy } = getCurrentInstance();

const showListStyle = ref(false);
const showListStyle2 = ref(false);
const url = ref("");
const activeName = ref("first");
const rules = reactive({});
const dataList = ref([]);
const childRef = ref(null);
const childRef2 = ref(null);
const childRef3 = ref(null);
const form = reactive({
    WxAppId: "",
    wxAppSecret: "",
    WxMpAppId: "",
    WxMpSecret: "",
    normalPost: 0,
    vipPost: 0,
    postPrice: 0,
    wxPaySecret: "",
    wxPayKey: "",
    appNotifyurl: "",
    redirectUrl: "",
    app_appid: "",
    payPostbtn:  "",
    payPostVip:  "",
    commentCheck: "",
    circleCheck: ""
});

const form2 = reactive({
    sms_sign: "",
    sms_region: "",
    sms_access_key: "",
    sms_access_secret: "",
    sms_templateId: "",
    sms_open: 0,
    chooseSms:0,
    tencentSecretId:"",
    tencentSecretKey:"",
    tencentSmsRegion:"",
    tencentSmsSdkAppId:"",
    tencentSmsSignName:"",
    tencentSmsTemplateId:"",

    isOpen: 0,
    chargeIsOpen: 0,
    canCashOut: 0,
    exchange: 0,
    yuetoint: 0,
    rewardBtn: 0,
    socialBtn:0,
    integral: 0,
    yuetointratio: 0,
    noticeContent: "",
    img: "",
    bgImg: "",
});

const form3 = reactive({
    contactTime: "",
    contactPhone: "",
    contactWechatQr: "",
    contactWechat: "",
});

const form4 = reactive({
    surplus: "",
    luckDrawStatus: "",
    luckDrawRule: "",
    luckDrawIntegral: "",
});

const form5 = reactive({
    protocol: "",
    privacy: "",
    standard: "",
    emailLogin: "",
    openEmailRegister: "",
    defaultHead: "",
    loginType: "",
    loginInfoPop:""
});

const form6 = reactive({
    vipAgreeContent: "",
    vipIntegral: 0,
    addPostIntegral: 0,
    addPostIntegralLimit: 0,
    vipRename: 0,
    commonRename: 0,
    vipTopicNumber: 0,
    commonTopicNumber: 0,
    vipShow: 0,
    iosClose: '',
    privateCirclesOpen: 0,
    privateCircleNeedVip: 0,
});

const form7 = reactive({
    adIsOpen: "",
    wxAdpid: "",
    h5Adpid: "",
});

const form8 = reactive({
    indexStyle1: "",
    indexStyle2: "",
    indexStyle3: "",
    showType: "",
});

const form9 = reactive({
    popupOpen: "",
    popTitle: "",
    popContent: "",
    popTime: "",
});

const form10 = reactive({
    status: "",
});

const baiduForm = reactive({
    baiduAppId: "",
    baiduApiKey: "",
    baiduSecretKey: "",
    baiduCensorTextOpen: "",
    baiduCensorImageOpen: "",
    baiduCensorVideoOpen: "",
});


function handleClick(tab, event) {
    activeName.value = tab.name;
}
// 获取数据列表
function getDataList() {
    url.value = proxy.$http.adornUrl(
        `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
    );

    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/config/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: 1,
                limit: 10000,
            }),
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                data.page.list.map(function (key, value) {
                    const keyName = key.paramKey;
                    const newValue = key.paramValue;
                    if (keyName in form) {
                        form[keyName] = newValue;
                    } else if (keyName in form2) {
                        form2[keyName] = newValue;
                    } else if (keyName in form3) {
                        form3[keyName] = newValue;
                    } else if (keyName in form4) {
                        form4[keyName] = newValue;
                    } else if (keyName in form5) {
                        form5[keyName] = newValue;
                    } else if (keyName in form6) {
                        form6[keyName] = newValue;
                    } else if (keyName in form7) {
                        form7[keyName] = newValue;
                    } else if (keyName in form8) {
                        form8[keyName] = newValue;
                    } else if (keyName in form9) {
                        form9[keyName] = newValue;
                    } else if (keyName in baiduForm) {
                        baiduForm[keyName] = newValue;
                    }
                });
                form.normalPost = parseInt(form.normalPost);
                form.vipPost = parseInt(form.vipPost);
                form.payPostbtn = parseInt(form.payPostbtn);
                form.payPostVip = parseInt(form.payPostVip);
                form.commentCheck = parseInt(form.commentCheck);
                form.circleCheck = parseInt(form.circleCheck);
                form.postPrice = parseInt(form.postPrice);
                form2.sms_open = parseInt(form2.sms_open);
                form2.chooseSms = parseInt(form2.chooseSms);
                
                form2.isOpen = parseInt(form2.isOpen);
                form2.chargeIsOpen = parseInt(form2.chargeIsOpen);
                form2.canCashOut = parseInt(form2.canCashOut);
                form2.exchange = parseInt(form2.exchange);
                form2.yuetoint = parseInt(form2.yuetoint);
                form2.rewardBtn = parseInt(form2.rewardBtn);
                form2.socialBtn = parseInt(form2.socialBtn);
                form2.integral = parseInt(form2.integral);
                form2.yuetointratio = parseInt(form2.yuetointratio);
                form4.surplus = parseInt(form4.surplus);
                form4.luckDrawStatus = parseInt(form4.luckDrawStatus);
                form4.luckDrawIntegral = parseInt(form4.luckDrawIntegral);
                form5.emailLogin = parseInt(form5.emailLogin);
                form5.loginType = parseInt(form5.loginType);
                form5.loginInfoPop = parseInt(form5.loginInfoPop);
                
                form5.openEmailRegister = parseInt(form5.openEmailRegister);
                form6.vipIntegral = parseInt(form6.vipIntegral);
                form6.addPostIntegral = parseInt(form6.addPostIntegral);
                form6.addPostIntegralLimit = parseInt(
                    form6.addPostIntegralLimit
                );
                form6.vipRename = parseInt(form6.vipRename);
                form6.commonRename = parseInt(form6.commonRename);
                form6.vipTopicNumber = parseInt(form6.vipTopicNumber);
                form6.commonTopicNumber = parseInt(form6.commonTopicNumber);
                form6.vipShow = parseInt(form6.vipShow);
                form6.iosClose = parseInt(form6.iosClose);
                form6.privateCirclesOpen = parseInt(form6.privateCirclesOpen);
                form6.privateCircleNeedVip = parseInt(form6.privateCircleNeedVip);
                
                form7.adIsOpen = parseInt(form7.adIsOpen);
                form8.indexStyle1 = parseInt(form8.indexStyle1);
                form8.indexStyle2 = parseInt(form8.indexStyle2);
                form8.indexStyle3 = parseInt(form8.indexStyle3);
                form8.showType = parseInt(form8.showType);
                form9.popupOpen = parseInt(form9.popupOpen);
                form9.popTime = parseInt(form9.popTime);

                baiduForm.baiduCensorTextOpen = parseInt(baiduForm.baiduCensorTextOpen);
                baiduForm.baiduCensorImageOpen = parseInt(baiduForm.baiduCensorImageOpen);
                baiduForm.baiduCensorVideoOpen = parseInt(baiduForm.baiduCensorVideoOpen);

                childRef.value.valueHtml=form5.standard //富文本内容单独处理
                childRef2.value.valueHtml=form5.protocol //富文本内容单独处理
                childRef3.value.valueHtml=form5.privacy //富文本内容单独处理
            }
        });
}
function doSubmit(forms) {
    form5.standard = childRef.value.valueHtml  //富文本内容单独处理
    form5.protocol = childRef2.value.valueHtml  //富文本内容单独处理
    form5.privacy = childRef3.value.valueHtml  //富文本内容单独处理
    proxy
        .$http({
            url: proxy.$http.adornUrl(`/sys/config/updateBatch`),
            method: "post",
            data: forms,
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                ElMessage({
                    message: "设置成功",
                    type: "success",
                    duration: 1500,
                });
            } else {
                ElMessage.error(data.msg);
            }
        });
}

function doJob(forms) {
    proxy
        .$http({
            url: proxy.$http.adornUrl(`/admin/post/doJob`),
            method: "post",
            data: forms,
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                ElMessage({
                    message: "执行成功",
                    type: "success",
                    duration: 1500,
                });
            } else {
                ElMessage.error(data.msg);
            }
        });
}

function handleIconSuccess(response) {
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        form2.img = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '项目logo上传失败';
        ElMessage.error(errorMessage);
    }
}

function handleIconSuccess2(response) {
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        form3.contactWechatQr = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '客服微信二维码上传失败';
        ElMessage.error(errorMessage);
    }
}

function handleIconSuccess3(response) {
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        form2.bgImg = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '用户个人页背景图上传失败';
        ElMessage.error(errorMessage);
    }
}

function handleIconSuccess4(response) {
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        form5.defaultHead = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '注册默认头像上传失败';
        ElMessage.error(errorMessage);
    }
}

onMounted(() => {
    getDataList();
});
</script>

<style scoped>
.app-container {
    padding: 20px 20px 45px 20px;
}
.topPadding {
    padding-left: 30px;
    font-size: 25px;
    font-weight: 600;
}

.formInfos {
    line-height: 0px;
    color: #999999;
    font-size: 12px;
}
.notice {
    line-height: 0px;
    color: #656161;
}
.avatar-uploader .el-upload {
    border: 3px dashed #979494;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}
.avatar-uploader .el-upload:hover {
    border-color: #409eff;
}
.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
}
.avatar {
    width: 100px;
    height: 100px;
    display: block;
}
.avatar2 {
    width: 200px;
    height: 80px;
    display: block;
}
.time {
    font-size: 13px;
    color: #999;
}

.bottom {
    margin-top: 13px;
    line-height: 12px;
}

.button {
    padding: 0;
    float: right;
}

.image {
    width: 100%;
    height: 560px;
    display: block;
}
.image2 {
    width: 100%;
    height: 783px;
    display: block;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both;
}
.tit {
    font-weight: 600;
}
</style>
