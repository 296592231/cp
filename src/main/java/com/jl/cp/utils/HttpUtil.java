package com.jl.cp.utils;

import cn.hutool.core.io.IoUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * http工具类
 * Created by chenjunyi on 2017/12/26.
 */
public class HttpUtil {

    /** 从连接池中获取连接的超时时间 */
    private static final int CONNEC_REQUEST_TIME_OUT = 10000;

    /** http连接超时时间 */
    private static final int CONNEC_TIME_OUT = 12000;

    /** 套接字超时时间 */
    private static final int SOCKET_TIME_OUT = 30000;

    private static final String HTTP_REQUEST_ERROR = "CP00001";

    /**
     * @Title: createSSLInsecureClient
     * @Description: 创建发送https请求
     * @return
     * @throws GeneralSecurityException    设定文件
     */
    @SuppressWarnings("deprecation")
    protected static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain,
                                             String authType)
                                                             throws java.security.cert.CertificateException {
                        return true;
                    }
                }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                new X509HostnameVerifier() {

                    @Override
                    public boolean verify(String arg0, javax.net.ssl.SSLSession arg1) {
                        return true;
                    }

                    @Override
                    public void verify(String host, javax.net.ssl.SSLSocket ssl) throws IOException {
                    }

                    @Override
                    public void verify(String host, String[] cns, String[] subjectAlts)
                                                                                       throws SSLException {
                    }

                    @Override
                    public void verify(String host, X509Certificate cert) throws SSLException {
                    }
                });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }

    /**
    * get请求
    * @param host 请求地址
    * @param path 请求地址路径
    * @param headers 请求头
    * @param querys 请求参数
    * @param charset 字符集
    * @return 返回结果
    * Created by kz on 2019/3/14 11:24.
    */
    public static String doGet(String host, String path, Map<String, String> headers, Map<String, String> querys , String charset){
        try {
            HttpClient httpClient = wrapClient(host);
            HttpGet request = new HttpGet(buildUrl(host, path, querys , charset));
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
            HttpResponse httpResponse = httpClient.execute(request);
            return EntityUtils.toString(httpResponse.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR , e);
        }
    }

    private static String buildUrl(String host, String path, Map<String, String> querys , String charset) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), charset));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private static HttpClient wrapClient(String host) throws GeneralSecurityException {
        HttpClient httpClient;
        if (host.startsWith("https://")) {
            httpClient = createSSLInsecureClient();
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

   /**
    * 根据URL获取网络图片数据字节流
    * @param url 请求URL
    * @return 字节流
    * Created by jl on 2021/1/21 10:33.
    */
    public static byte[] getImageFromLocalByUrl(String url) {
        byte[] result = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            result = IoUtil.readBytes(connection.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR , e);
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     */
    public static String get(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR , e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param charset 字符集
     * @return 返回结果
     */
    public static String get(String url , String param , String charset) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL realUrl = new URL(url + "?" + param);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) realUrl.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：毫秒
            connection.setConnectTimeout(CONNEC_TIME_OUT);
            // 设置读取远程返回的数据时间：毫秒
            connection.setReadTimeout(SOCKET_TIME_OUT);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                reader = new BufferedReader(new InputStreamReader(is, charset));
                // 存放数据
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
            } else {
                throw new RuntimeException(HTTP_REQUEST_ERROR + ",responseCode：" + connection.getResponseCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR , e);
        } finally {     // 使用finally块来关闭输入流
            // 关闭资源
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
            connection.disconnect();// 关闭远程连接
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的URL
     * @param charset 字符集
     * @param connectTimeOut 连接超时时间
     * @param readTimeOut 读取超时时间
     * @return 返回结果
     */
    public static String get(String url , String charset , int connectTimeOut , int readTimeOut) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader reader = null;
        String result = null;// 返回结果字符串

        try {
            // 创建远程url连接对象
            URL realUrl = new URL(url);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) realUrl.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：毫秒
            connection.setConnectTimeout(connectTimeOut);
            // 设置读取远程返回的数据时间：毫秒
            connection.setReadTimeout(readTimeOut);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                reader = new BufferedReader(new InputStreamReader(is, charset));
                // 存放数据
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
            } else {
                throw new RuntimeException(HTTP_REQUEST_ERROR + ",responseCode：" + connection.getResponseCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR, e);
        } finally {     // 使用finally块来关闭输入流
            // 关闭资源
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
            connection.disconnect();// 关闭远程连接
        }
        return result;
    }

    /**
     * 发送httppost表单请求
     * @param request 请求字符串
     * @param url 请求url
     * @param charset 字符集
     * @return 响应字符串
     */
    public static String post(String request, String url, String charset, String contentType) {
        return post(request , url , charset , contentType , null);
    }

    /**
     * 发送表单请求
     * @param map 请求参数map
     * @param filterBlank 过滤空值，true-过滤；false-不过滤
     * @param url 请求url
     * @param charset 字符集
     * @return 响应字符串
     */
    public static String postForm(Map<String, String> map, boolean filterBlank, String url,
                                  String charset) {
        String request = formFormatToString(map, filterBlank);
        //        return post(request, url, charset, MediaType.APPLICATION_FORM_URLENCODED_VALUE); //TODO
        return post(request, url, charset, "application/x-www-form-urlencoded");
    }

    /**
     * 发送表单请求
     * @param map 请求参数map
     * @param filterBlank 过滤空值，true-过滤；false-不过滤
     * @param url 请求url
     * @param charset 字符集
     * @param urlEncoderFlag urlEncode编码，true：要编码 , false：不要编码
     * @return 响应字符串
     */
    public static String postForm(Map<String, String> map, boolean filterBlank, String url,
                                  String charset  , boolean urlEncoderFlag) {
        String request = formFormatToString(map, filterBlank , urlEncoderFlag , charset);
        return post(request, url, charset, "application/x-www-form-urlencoded");
    }

    /**
     * 格式化表单请求，将参数按照key=value&形式进行拼接
     * @param map 请求参数map
     * @param filterBlank 过滤空值，true-过滤；false-不过滤
     * @return 表单请求字符串
     */
    public static String formFormatToString(Map<String, String> map, boolean filterBlank) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (filterBlank && StringUtils.isBlank(entry.getValue())) {
                continue;
            }
            builder.append(entry.getKey()).append("=");
            builder.append(entry.getValue()).append("&");
        }
        return StringUtils.removeEnd(builder.toString(), "&");
    }

    /**
     * 格式化表单请求，将参数按照key=value&形式进行拼接
     * @param map 请求参数map
     * @param filterBlank 过滤空值，true-过滤；false-不过滤
     * @param urlEncoderFlag urlEncode编码，true：要编码 , false：不要编码
     * @param charset 编码格式
     * @return 表单请求字符串
     */
    public static String formFormatToString(Map<String, String> map, boolean filterBlank , boolean urlEncoderFlag , String charset) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (filterBlank && StringUtils.isBlank(entry.getValue())) {
                continue;
            }
            builder.append(entry.getKey()).append("=");
            if(urlEncoderFlag){
                try {
                    builder.append(URLEncoder.encode(entry.getValue(),charset));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(HTTP_REQUEST_ERROR , e);
                }
            } else {
                builder.append(entry.getValue());
            }
            builder.append("&");
        }
        return StringUtils.removeEnd(builder.toString(), "&");
    }

    /**
     * 格式化表单响应结果，将key=value&形式的响应结果解析为map
     * @param string 表单响应字符串
     * @return map
     */
    public static Map<String, String> formFormatToMap(String string) {
        Map<String, String> map = new HashMap<>();
        String[] params = string.split("&");
        for (String param : params) {
            String[] entrys = param.split("=");
            map.put(entrys[0], entrys[1]);
        }
        return map;
    }


    /**
     * 带图片发送http请求
     * @param builder 存放图片和参数
     * @param uploadUrl 请求地址
     * @param chartSet 字符集
     * @return
     */
    public static String uploadPost(MultipartEntityBuilder builder, String uploadUrl,
                                    String chartSet) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(uploadUrl);
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNEC_TIME_OUT)
                .setSocketTimeout(SOCKET_TIME_OUT).build();
            post.setConfig(requestConfig);
            httpResponse = httpClient.execute(post);
            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
                HttpEntity entitys = httpResponse.getEntity();
                if (entitys != null) {
                    return EntityUtils.toString(httpResponse.getEntity(), chartSet);
                }
            }else {
                HttpEntity entitys = httpResponse.getEntity();
                if (entitys != null) {
                    return EntityUtils.toString(httpResponse.getEntity(), chartSet);
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("http异常", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException var2) {
            }
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException var2) {

            }
        }
    }


    /**
     * 发送httppost表单请求
     * @param request 请求字符串
     * @param url 请求url
     * @param charset 字符集
     * @param contentType 请求类型
     * @param httpHeaderMap 请求头 map集
     * @return 响应字符串
     */
    public static String post(String request, String url, String charset, String contentType, Map<String , String> httpHeaderMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            if (url.contains("https")) {
                httpClient = createSSLInsecureClient();
            } else {
                httpClient = HttpClients.createDefault();
            }
            HttpPost httpPost = new HttpPost(url);

            StringEntity httpEntity = new StringEntity(request, charset);
            httpEntity.setContentType(contentType);
            httpPost.setEntity(httpEntity);
            if(httpHeaderMap != null){
                for(String headKey : httpHeaderMap.keySet()){
                    httpPost.setHeader(headKey , httpHeaderMap.get(headKey));
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNEC_TIME_OUT).setConnectionRequestTimeout(CONNEC_REQUEST_TIME_OUT)
                    .setSocketTimeout(SOCKET_TIME_OUT).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpClient.execute(httpPost);
            return EntityUtils.toString(httpResponse.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR, e);
        } finally {
            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(httpClient);
        }
    }

    /**
     * 发送httppost表单请求
     * @param url 请求url
     * @param charset 字符集
     * @return 响应字符串
     */
    public static String postAndFile(String url, String charset,
                                     Map<String, StringBody> stringBodyMap,
                                     Map<String, FileBody> fileBodyMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            if (url.contains("https")) {
                httpClient = createSSLInsecureClient();
            } else {
                httpClient = HttpClients.createDefault();
            }
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity();
            if (stringBodyMap != null) {
                for (String headKey : stringBodyMap.keySet()) {
                    entity.addPart(headKey, stringBodyMap.get(headKey));
                }
            }
            if (fileBodyMap != null) {
                for (String headKey : fileBodyMap.keySet()) {
                    entity.addPart(headKey, fileBodyMap.get(headKey));
                }
            }

            httpPost.setEntity(entity);

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNEC_TIME_OUT)
                .setSocketTimeout(SOCKET_TIME_OUT).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpClient.execute(httpPost);
            return httpResponse.getStatusLine().getStatusCode()+"~"+EntityUtils.toString(httpResponse.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException(HTTP_REQUEST_ERROR, e);
        } finally {
            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(httpClient);
        }
    }
}
