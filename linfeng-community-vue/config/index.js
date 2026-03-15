/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By linfeng.tech , linfengtech.cn
 *  宁波山觉网络科技有限公司
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服  
 *  项目官网：https://net.linfeng.tech
 *  备用官网：https://www.linfengtech.cn
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */

const runtimeConfig =
  typeof globalThis !== "undefined" && globalThis.__MANRE_ADMIN_CONFIG__
    ? globalThis.__MANRE_ADMIN_CONFIG__
    : {};

// 本地环境
export const DEV_API =
  runtimeConfig.devApi ||
  process.env.VUE_APP_DEV_API ||
  "http://localhost:8080";

// 线上环境
export const PROD_API =
  runtimeConfig.prodApi ||
  process.env.VUE_APP_PROD_API ||
  "https://wxapi.linfeng.tech";
