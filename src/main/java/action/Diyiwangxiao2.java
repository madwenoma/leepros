package action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/5/4.
 */
public class Diyiwangxiao2 {


    private static BasicClientCookie setWeiboCookies(String name, String value, String date) {
        BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
        cookie.setDomain("cas.1wangxiao.com");
        cookie.setPath("/");
        if (StringUtils.isNotBlank(date)) {
            cookie.setExpiryDate(new Date(date));
        } else {
            cookie.setExpiryDate(null);
        }
        return cookie;
    }

    private static BasicClientCookie setWeiboCookies2(String name, String value, String date) {
        BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
        cookie.setDomain("1wangxiao.com");
        cookie.setPath("/");
        if (StringUtils.isNotBlank(date)) {
            cookie.setExpiryDate(new Date(date));
        } else {
            cookie.setExpiryDate(null);
        }
        return cookie;
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        File file = new File("c:\\XITI.txt");
        String url = "http://cas.1wangxiao.com/MyPlan.aspx";
        HttpClient httpclient = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost();
        post.setURI(URI.create(url));
        HttpContext localContext = new BasicHttpContext();


        BasicCookieStore cookieStore = new BasicCookieStore();


        cookieStore.addCookie(setWeiboCookies("ASP.NET_SessionId", "gcvdxkn1o1m0ylrq3cfpxvam", null));
        cookieStore.addCookie(setWeiboCookies("app", "TTdJjQnK+Tk=", null));
        cookieStore.addCookie(setWeiboCookies("backurl", "TTdJjQnK+Tk=", null));
        cookieStore.addCookie(setWeiboCookies("expmenu", "menu1", null));
        cookieStore.addCookie(setWeiboCookies("ssid", "TTdJjQnK+Tk=", null));
        cookieStore.addCookie(setWeiboCookies("xclvkjl+dkjs", "NFlWbabTsLM=", null));
        cookieStore.addCookie(setWeiboCookies("us", "4UQe5p6WSlAnbulSQIakS8tArJ1UiccvBp/DTw3L2P74OWbgxAXckqpJCKROf79O9fR4FDnutp7ZFsWmG7ns6iSg25j2imyZZ6A3vMRtKUutIxg3B28iYwahjK7ctagN2Cqr2WY5WrTffOccUswd16sunG8z3vcQhvq/9cWdscz4XniV4/NFisQhO4V7oMFZqtoL05GIadcq8DjM8c27EIFBHF+0lUog7FjNgpV/9OyuR1UTW6RgkF9Zo8gdmy48naxz7fdd4yaamK2tCu2N0+pOhI1Gz6lv9pVKCem1tWCOIgidR8UKA8WBysgI1xFZnpub3yXSaaPQFQ/ublYYPuxYzYKVf/TsK0TD5mRniFjTYamakNgxj1HZEHMqF7akLzOE0o51JOng9x+gBOliXEUlx8wOUx6PEJMUr5CvsWwXAjR51md2QWFD7ZQfBqkfFwI0edZndkHgaX2vLzU2Spr7ciL1UzBBFS8rrHyFv27geUiJ6d+0ykEJm3cieVIFV/8f81/MsvpQ+R7+A3fYBAf/1YmM0JpcLu65e4VOIkum3uAHxMlYKSL3tRzrHk0PrH0L1WaDcx4todeh8cW0pdRCybZiiiLc8EfhbBM9ib/00L5IcPrsYw==", null));
        cookieStore.addCookie(setWeiboCookies2("ppkey", "S420kDxOFxQGn8NPDcvY/lojiAu6ket4/rBDLS6RcjRxmhXge4mChCggOOC/VVy41s9qSYcAZtbzRBYo35JPQBaSM/y7YC5+jcu0xpBQqU2Qhm/16mQSiiNO49da+Cxl", null));
        cookieStore.addCookie(setWeiboCookies2("safedog-flow-item", "5B235F2FEA4A5243A046C57D9F9E7662", null));
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.933.400 QQBrowser/9.4.8699.400");

        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        for (int i = 1; i <= 80; i++) {

            TimeUnit.SECONDS.sleep(3);
            HttpResponse response = null;
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("ctl00$ScriptManager1", "ctl00$main$UpdatePanel1|ctl00$main$AspNetPager1"));
            nvps.add(new BasicNameValuePair("ctl00$main$AspNetPager1_input", "4"));
            nvps.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUJLTQ0MzM2NDAwD2QWAmYPZBYCAgMPZBYCAgsPZBYCAgEPZBYCZg9kFgRmDxYCHgtfIUl0ZW1Db3VudAIFFgpmD2QWAmYPFQMv5Lit57qn5biILjM2OeWGheenkeaKpOeQhiDln7rnoYDnn6Xor4bvvIg0OO+8iSAQMjAxNy81LzEgOToyNToyMwwzNTMxNjQ5Mzk4MzBkAgEPZBYCZg8VAzXkuK3nuqfluIguMzY55YaF56eR5oqk55CGIOebuOWFs+S4k+S4muefpeivhu+8iDI277yJIBIyMDE3LzQvMzAgMTI6MjQ6MzIMMzU0MTI5OTkwOTkyZAICD2QWAmYPFQMu5Lit57qn5biILjM2OeWGheenkeaKpOeQhiDln7rnoYDnn6Xor4bvvIgz77yJIBIyMDE3LzQvMzAgMTI6MjQ6MzIMMzU0MTI5ODk2NjMxZAIDD2QWAmYPFQM15Lit57qn5biILjM2OeWGheenkeaKpOeQhiDnm7jlhbPkuJPkuJrnn6Xor4bvvIg2Ne+8iSARMjAxNy80LzMwIDk6MjQ6NTEMMzU0MTI5OTg0NDk0ZAIED2QWAmYPFQMv5Lit57qn5biILjM2OeWGheenkeaKpOeQhiDln7rnoYDnn6Xor4bvvIg1M++8iSARMjAxNy80LzMwIDk6MjQ6NTEMMzUzMTY0OTM4MDU5ZAIBDw8WBB4QQ3VycmVudFBhZ2VJbmRleAIEHgtSZWNvcmRjb3VudAKQA2RkZNYvYZuMYDJW4/8MxZ+BJWmpKomUPTcjZ4Jjep8JsWUe"));
            nvps.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "3C84066C"));
            nvps.add(new BasicNameValuePair("__EVENTTARGET", "ctl00$main$AspNetPager1"));
            nvps.add(new BasicNameValuePair("__EVENTARGUMENT", "" + i));
            nvps.add(new BasicNameValuePair("__ASYNCPOST", "true"));

            post.setEntity(new UrlEncodedFormEntity(nvps));

            response = httpclient.execute(post, localContext);
//        String content = EntityUtils.toString(response.getEntity(), "gbk");
            String content = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(content);

            FileUtils.write(file, content, "UTF-8", true);
        }

    }













}
