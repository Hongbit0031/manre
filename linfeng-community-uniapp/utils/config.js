const runtimeConfig =
	globalThis.__MANRE_UNIAPP_CONFIG__ || {};

const miniappName = runtimeConfig.miniappName || "慢热"; // 站点标题用于分享时的标题
const shareH5Url = runtimeConfig.shareH5Url || "https://m.scs.baby/#/"; // H5分享路径
let defaultBaseUrl = "localhost:8080";
// 小程序端默认使用线上域名，避免运行时配置缺失时回退到 localhost。
// #ifdef MP-WEIXIN
defaultBaseUrl = "m.scs.baby";
// #endif
const baseUrl = runtimeConfig.baseUrl || defaultBaseUrl; // 接口地址
const apiProtocol = runtimeConfig.apiProtocol || (baseUrl === "localhost:8080" ? "http" : "https");
const wsProtocol = runtimeConfig.wsProtocol || (apiProtocol === "https" ? "wss" : "ws");
const domain = runtimeConfig.domain || `${apiProtocol}://${baseUrl}/app/`; // 接口服务地址
const websocketUrl = runtimeConfig.websocketUrl || `${wsProtocol}://${baseUrl}/app/socket/`; // IM通信地址
const imfile = runtimeConfig.imfile || `${apiProtocol}://${baseUrl}/`; // IM文件资源地址
const emojiUrl = runtimeConfig.emojiUrl || "https://cc.scs.baby/big/"; // 私聊表情包资源地址
const imgResource = runtimeConfig.imgResource || "https://cc.scs.baby/resource"; // 用户端图标等静态资源
const wxMpAppId = runtimeConfig.wxMpAppId || "wxa3abc6f570231062"; // 微信公众号AppID
const wxh5Login = runtimeConfig.wxh5Login ?? true; // 是否使用微信公众号登录

export default {
	baseUrl: baseUrl,
	domain: domain,
	miniappName: miniappName,
	shareH5Url: shareH5Url,
	websocketUrl: websocketUrl,
	imfile:imfile,
	emojiUrl:emojiUrl,
	imgResource:imgResource,
	wxMpAppId:wxMpAppId,
	wxh5Login:wxh5Login
}
