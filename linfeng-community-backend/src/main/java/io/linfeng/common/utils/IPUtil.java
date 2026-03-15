package io.linfeng.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * IP查询工具类
 * @author linfeng
 * @date 2022/2/7 19:18
 */
@Slf4j
public class IPUtil {

    //填写腾讯地图申请的key
    private static final String key="";

    private static final String UNKNOWN = "unknown";
    // IP归属地查询
    private static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";
    // IP归属地查询 备用地址
    private static final String IP_URL_2 = "http://ip-api.com/json/%s?lang=zh-CN";
    /**
     * 获取ip地址
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        String comma = ",";
        String localhost = "127.0.0.1";
        if(ip==null){
            return localhost;
        }
        if (ip.contains(comma)) {
            ip = ip.split(",")[0];
        }
        if  (localhost.equals(ip))  {
            // 获取本机真正的ip地址
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return ip;
    }

    /**
     * 根据ip获取归属地 地址1
     * @param ip 用户ip
     * @return 地址
     */
    public static String getCityInfo(String ip) {
        String api = String.format(IP_URL,ip);
        try {
            String s = HttpUtil.get(api, 5000);
            if(s == null || s.isEmpty()){
                return "";
            }
            JSONObject object;
            object = JSONUtil.parseObj(s);
            String addr = object.get("addr", String.class);
            if(!WechatUtil.isEmpty(addr)){
                return addr.split(" ")[0];
            }
            return addr;
        } catch (Exception e) {
            log.error("获取地理位置1异常 {}", ip);
            return getCityInfo2(ip); //调用第二个接口
        }
    }

    /**
     * 根据ip获取归属地 地址2
     * @param ip 用户ip
     * @return 地址
     */
    public static String getCityInfo2(String ip) {
        try {
            String api = String.format(IP_URL_2,ip);
            String s = HttpUtil.get(api,8000);
            if(s == null || s.isEmpty()){
                return "";
            }
            JSONObject object;
            object = JSONUtil.parseObj(s);
            String regionName = object.get("regionName", String.class);
            String city = object.get("city", String.class);
            if(regionName.equals("null")||city.equals("null")){
                return "";
            }
            return regionName+city;
        } catch (Exception e) {
            log.error("获取地理位置2异常 {}", ip);
            return "";
        }
    }



    /**
     * 根据ip获取归属地 地址3 调用腾讯云需要配置密钥
     * @param ip 用户ip
     * @return 地址
     */
    public static String getCityInfo3(String ip) {

        String url = "https://apis.map.qq.com/ws/location/v1/ip?ip="+ ip +"&key=" + key ;
        String str = HttpUtil.get(url);
        if (!StrUtil.hasBlank(str)) {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(str);
            com.alibaba.fastjson.JSONObject res = jsonObject.getJSONObject("result");
            if(res!=null){
                com.alibaba.fastjson.JSONObject result = res.getJSONObject("ad_info");
                if(result!=null){
                    String province = result.getString("province");
                    if(province.equals("")){
                        return result.getString("nation");
                    }
                    String city = result.getString("city");
                    String district = result.getString("district");
                    if(province.equals(city)){
                        return province+district;
                    }
                    return province+city+district;
                }
            }
        }
        return "";
    }

}
