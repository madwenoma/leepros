package action;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/1/10.
 */
public class B {
    public static void main(String[] args) throws IOException, InterruptedException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");

            // set up a TrustManager that trusts everything
            try {
                sslContext.init(null,
                        new TrustManager[]{new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                System.out.println("getAcceptedIssuers =============");
                                return null;
                            }

                            public void checkClientTrusted(
                                    X509Certificate[] certs, String authType) {
                                System.out.println("checkClientTrusted =============");
                            }

                            public void checkServerTrusted(
                                    X509Certificate[] certs, String authType) {
                                System.out.println("checkServerTrusted =============");
                            }
                        }}, new SecureRandom());
            } catch (KeyManagementException e) {
            }
            SSLSocketFactory ssf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpclient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<String> answerUrls = new ArrayList<String>();

        HttpGet home = new HttpGet("https://www.zhihu.com/question/26582290");

        HttpEntity entity = httpclient.execute(home).getEntity();
        // 显示结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

        String line = null;
        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
            if (line.contains("<a href=\"/question") && line.contains("answer")) {
                answerUrls.add(StringUtils.substringBetween(line, "<a href=\"", "\""));
            }
        }

        for (int i = 1; i < 11; i++) {
            TimeUnit.MILLISECONDS.sleep(4000);
            HttpPost url = new HttpPost("https://www.zhihu.com/node/QuestionAnswerListV2");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("method", "next"));
            String params = "{\"url_token\":26582290,\"pagesize\":10,\"offset\":" + "" + i * 10 + "}";
            System.out.println(params);
            nvps.add(new BasicNameValuePair("params", params));
            url.setEntity(new UrlEncodedFormEntity(nvps));
            entity = httpclient.execute(url).getEntity();
            // 显示结果
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            line = null;
            String result = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            ResBody r = JSONObject.parseObject(result, ResBody.class);

            for (String content : r.getMsg()) {
                if (content.contains("data-entry-url")) {
                    answerUrls.add(StringUtils.substringBetween(content, "data-entry-url=\"", "\">"));
                }
            }
            if (entity != null) {
                entity.consumeContent();
            }
        }

        String allContent = "";
        int i = 0;
        File file = new File("c:\\chuangye.txt");
        file.createNewFile();
        for (String url : answerUrls) {
            TimeUnit.MILLISECONDS.sleep(4000);
            HttpGet getContent = new HttpGet("https://n.zhihu.com" + url);
            entity = httpclient.execute(getContent).getEntity();
            // 显示结果
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
            String answerHtml = "";
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                //<div class="zm-editable-content clearfix">
                answerHtml += line;
            }
            Document doc = Jsoup.parse(answerHtml);
            Element content = doc.select("div.zm-editable-content").get(1);

            FileUtils.write(file, "\n第" + (++i) + "个答案:" + content.text(), "UTF-8", true);
            if (entity != null) {
                entity.consumeContent();
            }

        }

//        FileUtil.writeFile("c:\\secondJob.txt", allContent, "utf-8");

    }
}


class ResBody {
    int r;
    List<String> msg;

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }
}