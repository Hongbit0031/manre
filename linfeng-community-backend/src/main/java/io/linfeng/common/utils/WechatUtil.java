package io.linfeng.common.utils;

/**
 * @author linfeng
 * @date 2022/1/20 13:15
 */
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Base64;
import io.linfeng.modules.app.param.LoginPhoneParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
public class WechatUtil {

    private static final int IMAGE_CONNECT_TIMEOUT = 8000;
    private static final int IMAGE_READ_TIMEOUT = 10000;

    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };

    private static final HostnameVerifier TRUST_ALL_HOSTS = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    static {
        try {
            ImageIO.scanForPlugins();
        } catch (Throwable throwable) {
            log.warn("ImageIO插件扫描失败，可能导致部分格式图片无法解码", throwable);
        }
    }

    /**
     * 获取小程序codeid换取openid
     * @param code
     * @return
     */
    public static JSONObject getOpenId(String code,String appId,String secret) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+ appId +"&secret=" +
                secret +"&js_code="+code+"&grant_type=authorization_code";
        PrintWriter out = null;
        BufferedReader in = null;
        String line;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            // 设置通用的请求属性 设置请求格式
            //设置返回类型
            conn.setRequestProperty("contentType", "text/plain");
            //设置请求类型
            conn.setRequestProperty("content-type", "application/x-www-param-urlencoded");
            //设置超时时间
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setDoOutput(true);
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应    设置接收格式
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取小程序token
     * @return
     */
    public static String getAccessToken(String appId,String secret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?" +
                "appid="+ appId + "&secret=" + secret + "&grant_type=client_credential";
        PrintWriter out = null;
        BufferedReader in = null;
        String line;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            // 设置通用的请求属性 设置请求格式
            //设置返回类型
            conn.setRequestProperty("contentType", "text/plain");
            //设置请求类型
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //设置超时时间
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setDoOutput(true);
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应    设置接收格式
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
            return jsonObject.getString("access_token");

        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 生成小程序二维码
     * @param accessToken 微信小程序token
     * @param path 置顶路径
     * @return
     */
    public static BufferedImage getWxCode(String accessToken, String path) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("path", path);
        paramMap.put("width", "120");
        String Json = JSONObject.toJSONString(paramMap);
        BufferedImage qrCodeImage = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacode?access_token=" + accessToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("accept","*/*");
            if (!TextUtils.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                conn.getResponseCode();
            }
            if (conn.getResponseCode() == 200) {
                qrCodeImage = ImageIO.read(conn.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return qrCodeImage;
    }


    /**
     * 获取微信小程序 session_key 和 openid
     * @param code
     * @param appId
     * @param secret
     * @return
     */
    public static JSONObject getSessionKeyOropenid(String code,String appId,String secret) {
        //微信端登录code值
        String wxCode = code;
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";  //请求地址 https://api.weixin.qq.com/sns/jscode2session
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("appid", appId);  //开发者设置中的appId
        requestUrlParam.put("secret", secret); //开发者设置中的appSecret
        requestUrlParam.put("js_code", wxCode); //小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");    //默认参数 authorization_code

        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识
        return JSON.parseObject(sendPost(requestUrl, requestUrlParam));
    }

    /**
     * 微信小程序绑定手机号
     * @param vo
     * @return
     */
    public static Map<String, Object> getPhoneNumber(LoginPhoneParam vo) {
        Map<String,Object> map=new HashMap<>();
        String openid= vo.getWechatOpenId();
        String session_key = vo.getSessionKey();
        if (!strIsEmpty(openid)) {

            if(strIsEmpty(session_key)){
                return null;
            }
            map.put("openid",openid);
            // 被加密的数据
            byte[] dataByte = Base64.getDecoder().decode(vo.getEncryptedData());
            // 加密秘钥
            byte[] keyByte = Base64.getDecoder().decode(session_key);
            // 偏移量
            byte[] ivByte = Base64.getDecoder().decode(vo.getIv());
            try {
                // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
                int base = 16;
                if (keyByte.length % base != 0) {
                    int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                    byte[] temp = new byte[groups * base];
                    Arrays.fill(temp, (byte) 0);
                    System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                    keyByte = temp;
                }
                // 初始化
                Security.addProvider(new BouncyCastleProvider());
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
                AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
                IvParameterSpec ivParameterSpec=new IvParameterSpec(ivByte);
                parameters.init(ivParameterSpec);
                cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
                byte[] resultByte = cipher.doFinal(dataByte);
                if (null != resultByte && resultByte.length > 0) {
                    String result = new String(resultByte, "UTF-8");
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    map.put("param",jsonObject);
                    return map;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 读取网络图片
     * @param imgUrl 图片地址
     * @return BufferedImage对象
     */
    public static BufferedImage readNetworkImage(String imgUrl) {
        String normalizedUrl = normalizeImgUrl(imgUrl);
        if (strIsEmpty(normalizedUrl)) {
            log.warn("图片地址为空或非法: {}", imgUrl);
            return null;
        }
        InputStream inputStream = null;
        try {
            URL url = new URL(normalizedUrl);
            URLConnection urlConnection = openImageConnection(url);
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).setRequestMethod("GET");
            }
            urlConnection.setConnectTimeout(IMAGE_CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(IMAGE_READ_TIMEOUT);
            urlConnection.connect();

            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection conn = (HttpURLConnection) urlConnection;
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    log.error("读取网络图片失败，响应码非200: {} => {}", imgUrl, conn.getResponseCode());
                    return null;
                }
            }

            inputStream = urlConnection.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                log.warn("ImageIO无法解码图片: {}", normalizedUrl);
            }
            return bufferedImage;
        } catch (Exception e) {
            log.error("读取网络图片失败: {}", normalizedUrl, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 读取图片
     * @param imgUrl 图片地址
     * @return BufferedImage对象
     */
    public static BufferedImage readImgInner(String imgUrl) {
        String normalizedUrl = normalizeImgUrl(imgUrl);
        if (strIsEmpty(normalizedUrl)) {
            log.warn("图片地址为空或非法: {}", imgUrl);
            return null;
        }
        try {
            // 先尝试使用原有的方法读取图片
            BufferedImage image = ImageIO.read(new URL(normalizedUrl));
            if (image != null) {
                return image;
            }
        } catch (Exception e) {
            log.warn("ImageIO直接读取图片失败，尝试备用方案: {}", normalizedUrl, e);
        }

        // 如果原有方法失败，尝试使用新的网络图片读取方法
        return readNetworkImage(normalizedUrl);
    }

    private static String normalizeImgUrl(String imgUrl) {
        if (strIsEmpty(imgUrl)) {
            return null;
        }
        String normalized = imgUrl.trim();
        while (normalized.startsWith("@")) {
            normalized = normalized.substring(1).trim();
        }
        if (strIsEmpty(normalized)) {
            return null;
        }
        if (normalized.startsWith("//")) {
            normalized = "https:" + normalized;
        }
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            return normalized;
        }
        return normalized;
    }



    /**
     * 将BufferedImage转换为InputStream
     * @param image
     * @return
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static URLConnection openImageConnection(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpsURLConnection) {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, TRUST_ALL_CERTS, new SecureRandom());
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            httpsURLConnection.setHostnameVerifier(TRUST_ALL_HOSTS);
        }
        return connection;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, ?> paramMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        String param = "";
        Iterator<String> it = paramMap.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            param += key + "=" + paramMap.get(key) + "&";
        }

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 手机号脱敏处理
     * @param phone 手机号
     */
    public static String maskMobile(String phone) {
        if (StrUtil.isBlank(phone)) {
            return "";
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String getRandomCode(int passLength, int type) {
        StringBuffer buffer = null;
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        r.setSeed(new Date().getTime());
        switch (type) {
            case 0:
                buffer = new StringBuffer("123456789");
                break;
            case 1:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
                break;
            case 2:
                buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 3:
                buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
                break;
            case 4:
                buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 5:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 6:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
                passLength -= 1;
                break;
            case 7:
                String s = UUID.randomUUID().toString();
                sb.append(s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24));
        }

        if (type != 7) {
            int range = buffer.length();
            for (int i = 0; i < passLength; ++i) {
                sb.append(buffer.charAt(r.nextInt(range)));
            }
        }
        return sb.toString();
    }

    /**
     * @Title: isEmpty
     * @Description: 判断对象是否为空
     * @param obj
     * @return
     * @return Integer
     */
    public static boolean isEmpty(Object obj) {
        return null == obj || "".equals(obj);
    }


    /**
     * @Title: isEmpty
     * @Description: 判断字符串是否为空，长度为0被认为是空字符串.
     * @param str
     * @return
     * @return Integer
     */
    public static boolean strIsEmpty(String str) {
        if (null != str) {
            return str.trim().length() == 0;
        } else {
            return true;
        }
    }


    /**
     * 随机用户名生成
     * @return
     */
    public static String generateRandomName(){
        return "LF_"+RandomUtil.randomNumbers(7);
    }

    /**
     * 手机验证码key
     * @return
     */
    public static String getMobileCodeKey(String mobile){
        return "code_" + mobile;
    }

    public static MultipartFile inputStreamToMultipartFile(InputStream inputStream, String fileName) throws IOException {
        // 读取 InputStream 到字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] bytes = baos.toByteArray();

        // 实现 MultipartFile 的匿名内部类
        return new MultipartFile() {
            @Override
            public String getName() {
                return fileName;
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return "image/jpeg";
            }

            @Override
            public boolean isEmpty() {
                return bytes.length == 0;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    fos.write(bytes);
                }
            }
        };
    }
}
