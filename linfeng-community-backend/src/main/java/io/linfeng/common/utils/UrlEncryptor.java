/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.common.utils;

/**
 * url自定义加密
 * 主要用户防止H5端视频模块播放地址直接暴露的潜在风险
 * @author linfeng
 * @date 2023/10/20 10:31
 */
public class UrlEncryptor {
    private static final String ENCRYPTION_KEY = "linfengcommunitySYKey"; // 自定义的加密密钥 与前端对应 请勿随意修改

    public static String encryptUrl(String url) {
        // 自定义加密逻辑
        // 将 URL 的每个字符 ASCII 码加上密钥的字符 ASCII 码作为密文
        StringBuilder encryptedUrl = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            char encryptedChar = (char) (c + ENCRYPTION_KEY.charAt(i % ENCRYPTION_KEY.length()));
            encryptedUrl.append(encryptedChar);
        }
        return encryptedUrl.toString();
    }
}
