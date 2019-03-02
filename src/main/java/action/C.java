//package action;
//
//import com.alibaba.fastjson.JSONObject;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.params.CookiePolicy;
//import org.apache.http.client.protocol.ClientContext;
//import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.impl.client.BasicCookieStore;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
//import org.apache.http.impl.cookie.BasicClientCookie;
//import org.apache.http.impl.cookie.BasicClientCookie2;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.ExecutionContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.io.*;
//import java.net.URI;
//import java.security.KeyManagementException;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by Administrator on 2017/1/10.
// */
//public class C {
//    public static void main(String[] args) throws IOException, InterruptedException {
//        test();
//    }
//
//
//    private static BasicClientCookie setWeiboCookies(String name, String value, String date) {
//        BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
//        cookie.setDomain(".weibo.com");
//        cookie.setPath("/");
//        if (StringUtils.isNotBlank(date)) {
//            cookie.setExpiryDate(new Date(date));
//        } else {
//            cookie.setExpiryDate(null);
//        }
//        return cookie;
//    }
//
//    public static void test() throws InterruptedException {
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        httpclient.getParams().setParameter("http.protocol.cookie-policy",
//                CookiePolicy.BROWSER_COMPATIBILITY);
//        HttpParams params = httpclient.getParams();
//        HttpConnectionParams.setConnectionTimeout(params, 5000);
//        HttpConnectionParams.setSoTimeout(params, 1000 * 60 * 10);
//        DefaultHttpRequestRetryHandler dhr = new DefaultHttpRequestRetryHandler(3, true);
//        HttpContext localContext = new BasicHttpContext();
//        HttpRequest request2 = (HttpRequest) localContext.getAttribute(
//                ExecutionContext.HTTP_REQUEST);
//        httpclient.setHttpRequestRetryHandler(dhr);
//        BasicCookieStore cookieStore = new BasicCookieStore();
//
//        /**
//         *  weibo.com
//         */
//        cookieStore.addCookie(setWeiboCookies("_s_tentry", "login.sina.com.cn",null));
//        cookieStore.addCookie(setWeiboCookies("ALF", "1515932125",null));
//        cookieStore.addCookie(setWeiboCookies("Apache", "1849963180720.8062.1484309427447",null));
//        cookieStore.addCookie(setWeiboCookies("SCF", "AmL7PaHoYd28VJ8b5GrIdlkylTF0J3VV2BepVxTz6XiN3CQdOouJp0oNS2C6wISXrAoGNVd8NcUwehys8nnc8vk.",null));
//        cookieStore.addCookie(setWeiboCookies("SINAGLOBAL", "7035811445675.79.1458610856295",null));
//        cookieStore.addCookie(setWeiboCookies("SSOLoginState", "1484309417",null));
//        cookieStore.addCookie(setWeiboCookies("SUB", "_2A251fmYPDeTxGedO4lMZ8CjKzz2IHXVWCtDHrDV8PUNbmtAKLWXEkW9eSaUN_XH4xRtN8VeGkY18JP3oMw..",null));
//        cookieStore.addCookie(setWeiboCookies("SUHB", "0jBZyNffWEH4BY",null));
//        cookieStore.addCookie(setWeiboCookies("ULV", "1484309428353:126:7:5:1849963180720.8062.1484309427447:1484277009880",null));
//        cookieStore.addCookie(setWeiboCookies("UOR", "www.nuandao.com,widget.weibo.com,login.sina.com.cn",null));
//        cookieStore.addCookie(setWeiboCookies("wvr", "6",null));
//        cookieStore.addCookie(setWeiboCookies("TC-Page-G0",  "07e0932d682fda4e14f38fbcb20fac81",null));
//        cookieStore.addCookie(setWeiboCookies("TC-Ugrow-G0", "5e22903358df63c5e3fd2c757419b456",null));
//        cookieStore.addCookie(setWeiboCookies("TC-V5-G0", "9ec894e3c5cc0435786b4ee8ec8a55cc",null));
//        cookieStore.addCookie(setWeiboCookies("wb_g_upvideo_1091806641", "1",null));
//        cookieStore.addCookie(setWeiboCookies("wb_publish_fist100_1091806641", "1",null));
//
//
//        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//        HttpGet request = new HttpGet();
//
//        for (int i = 1; i < 314; i++) {
//
//            try {
//                String url = "http://weibo.com/aj/v6/comment/big?ajwvr=6&id=4058036936449489&filter=all&page=" + i;
//                request.setURI(URI.create(url));
//                HttpResponse response = null;
//                response = httpclient.execute(request, localContext);
//                String content = EntityUtils.toString(response.getEntity(), "gbk");
////                String content = EntityUtils.toString(response.getEntity(), "utf-8");
//                System.out.println(content);
//                SinaResBody sinaResBody = JSONObject.parseObject(content, SinaResBody.class);
//
//
//                Document doc = Jsoup.parse(sinaResBody.data.getHtml());
//                Elements srcLinks = doc.select("img[src$=.jpg]");
//
//                for (Element link : srcLinks) {
//                    TimeUnit.MILLISECONDS.sleep(500);
//                    //:剔除标签，只剩链接路径
//                    String imagesPath = link.attr("src");
//                    if (imagesPath.contains("thumb180")) {
//                        String imgLargeUrl = StringUtils.replace(imagesPath, "thumb180", "large");
////                    imgs.add(imgLargeUrl);
//                        download(imgLargeUrl, "d:\\sina_images\\sichuan\\0000" + ++count + ".jpg");
//                    }
//                }
//
//            } catch (IOException e) {
//                System.out.println(e);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            TimeUnit.SECONDS.sleep(2);
//        }
//    }
//
//    static ArrayList<String> imgs = new ArrayList<String>();
//    static int count = 0;
//
//
//    public static void download(String url, String localFileName) {
//        System.out.println("开始下载文件：" + url + "，存为：" + localFileName);
//        DefaultHttpClient httpClient = new DefaultHttpClient();
//        OutputStream out = null;
//        InputStream in = null;
//
//        try {
//            HttpGet httpGet = new HttpGet(url);
//
////            httpGet.addHeader("fileName", remoteFileName);
//
//            HttpResponse httpResponse = httpClient.execute(httpGet);
//            HttpEntity entity = httpResponse.getEntity();
//            in = entity.getContent();
//
//            long length = entity.getContentLength();
//            if (length <= 0) {
//                System.out.println("下载文件不存在！");
//                return;
//            }
//
////            System.out.println("The response value of token:" + httpResponse.getFirstHeader("token"));
//
//            File file = new File(localFileName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            out = new FileOutputStream(file);
//            byte[] buffer = new byte[4096];
//            int readLength = 0;
//            while ((readLength = in.read(buffer)) > 0) {
//                byte[] bytes = new byte[readLength];
//                System.arraycopy(buffer, 0, bytes, 0, readLength);
//                out.write(bytes);
//            }
//
//            out.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//}
//
