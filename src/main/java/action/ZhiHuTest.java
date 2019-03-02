package action;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ZhiHuTest {

    public static void main(String[] args) throws java.text.ParseException {
        String name = "username";
        String password = "password";

        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore).build();

        CloseableHttpResponse res = null;

        try {
            try {
                HttpGet get = new HttpGet("http://wap.cnki.net/touch/web");

                res = httpClient.execute(get, context);
                for (Cookie c : cookieStore.getCookies()) {
                    System.out.println(c.getName() + ": " + c.getValue());
                }
                res.close();

                List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
                valuePairs.add(new BasicNameValuePair("email", name));
                valuePairs.add(new BasicNameValuePair("password", password));
                valuePairs.add(new BasicNameValuePair("remember_me", "true"));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
                entity.setContentType("application/x-www-form-urlencoded");

                HttpPost post = new HttpPost("https://www.zhihu.com/login/email");
                post.setEntity(entity);
                res = httpClient.execute(post, context);

                System.out.println(EntityUtils.toString(res.getEntity(), "UTF-8"));
                res.close();

                for (Cookie c : context.getCookieStore().getCookies()) {
                    System.out.println(c.getName() + ": " + c.getValue());
                }

                HttpGet newGet = new HttpGet("http://www.zhihu.com/question/following");
                res = httpClient.execute(newGet, context);
                String content = EntityUtils.toString(res.getEntity());
                System.out.println(content);
                res.close();

            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
