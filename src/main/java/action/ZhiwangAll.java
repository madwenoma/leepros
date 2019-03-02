package action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

public class ZhiwangAll {
    public static void main(String[] args) throws ClientProtocolException,
            IOException, InterruptedException {

        List<String> lines = FileUtils.readLines(new File("c:\\1.txt"));
        List<Topic> topics = new ArrayList<Topic>();


        for (String line : lines) {
            System.out.println("start..");
            String url = "http://wap.cnki.net/touch/web/Article/Search";

            String kw = URLEncoder.encode(line.trim(), "utf-8");
//
            String param = "searchtype=0&dbtype=&pageindex=1&pagesize=10&theme_kw=&title_kw=&full_kw=&author_kw=&depart_kw=&key_kw=&abstract_kw=&source_kw=&teacher_md=&catalog_md=&depart_md=&refer_md=&name_meet=&collect_meet=&keyword=" +
                    kw + "&fieldtype=103&sorttype=0&articletype=-1&screentype=0&isscreen=&subject_sc=&research_sc=&depart_sc=&sponsor_sc=&author_sc=&teacher_sc=&subjectcode_sc=&researchcode_sc=&departcode_sc=&sponsorcode_sc=&authorcode_sc=&teachercode_sc=&starttime_sc=&endtime_sc=&timestate_sc=";


            String result = sendHttpPost(url, param);
            System.out.println(result);

            Document doc = Jsoup.parse(result);
//			Element div = doc.select("div.c-nav__double-bottom").first();

            String countStr = doc.select("span#totalcount").text();
//			String countStr = extractNum(div.text());
            Topic topic = new Topic();
            topic.title = line;
            topic.count = Integer.parseInt(countStr);
            topics.add(topic);
            System.out.println("add topic " + topic);
            Random r = new Random();
            int t = r.nextInt(3) + 2;
            Thread.sleep(t);

        }

        Collections.sort(topics, new Comparator<Topic>() {
            public int compare(Topic o1, Topic o2) {
                return o2.count - o1.count;
            }

        });
        File out = new File("c:\\topicout.txt");
        List<String> outLines = new ArrayList<String>();
        for (Topic topic : topics) {
            System.out.println(topic);
            outLines.add(topic.toString());
        }
        FileUtils.writeLines(out, outLines);
    }


    private static RequestConfig requestConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).setSocketTimeout(15000)
            .setConnectTimeout(15000).setConnectionRequestTimeout(15000)
            .build();

    public static String sendHttpPost(String httpUrl, String params) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {

            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }


    public static String sendHttpPost(String httpUrl, Map<String, String> maps) {
        HttpPost httpPost = new HttpPost(httpUrl);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (String key : maps.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }


    // public String sendHttpPost(String httpUrl, Map<String, String> maps,
    // List<File> fileLists) {
    // HttpPost httpPost = new HttpPost(httpUrl);// 鍒涘缓httpPost
    // MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
    // for (String key : maps.keySet()) {
    // meBuilder.addPart(key, new StringBody(maps.get(key),
    // ContentType.TEXT_PLAIN));
    // }
    // for(File file : fileLists) {
    // FileBody fileBody = new FileBody(file);
    // meBuilder.addPart("files", fileBody);
    // }
    // HttpEntity reqEntity = meBuilder.build();
    // httpPost.setEntity(reqEntity);
    // return sendHttpPost(httpPost);
    // }
    //

    private static String sendHttpPost(HttpPost httpPost) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            httpClient = HttpClients.createDefault();
            httpPost.setConfig(requestConfig);
            response = httpClient.execute(httpPost);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    public static String sendHttpGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet);
    }


    private static String sendHttpGet(HttpGet httpGet) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext context = HttpClientContext.create();
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultCookieStore(cookieStore).build();
//			setCookie(cookieStore, "ASPSESSIONIDCAAQDTTR","GPOKJANBCOLIDGFCPLNNAFEA");
//			setCookie(cookieStore, "ASP.NET_SessionId","cvsqdamxgzwehkpxkkwacmhf");
//			setCookie(cookieStore, "Ecp_ClientId","3171010214704876992");
//			setCookie(cookieStore, "Ecp_IpLoginFail","171010106.39.67.60");
//			setCookie(cookieStore, "ASPSESSIONIDCABTDSTR","KADEAGNBLFOBCHEIAOMKEMMP");
//			setCookie(cookieStore, "kc_cnki_net_uid","36e55847-d86f-b3af-380e-a7c1cfda99c2");
//			setCookie(cookieStore, "RsPerPage","20");
//			setCookie(cookieStore, "cnkiUserKey","13172c17-9ee0-4366-cc62-f0fa202b645a");


//			context.setCookieStore(cookieStore);
            httpGet.setHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseContent;
    }

    private static void setCookie(CookieStore cs, String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setVersion(0);
        cookie.setDomain("epub.cnki.net");
        cookie.setPath("/");

        cs.addCookie(cookie);

    }

}
