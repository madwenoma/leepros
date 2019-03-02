package action;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public class LiepinJobs {

    //    private static final String JOB_URL = "https://www.liepin.com/zhaopin/?ckid=a854cfcf2ecd7675&fromSearchBtn=2&init=-1&flushckid=1&dqs=010&jobKind=2&key=java&headckid=8e6ff9bcc40ff45f&d_pageSize=40&siTag=k_cloHQj_hyIn0SLM9IfRg%7EF5FSJAXvyHmQyODXqGxdVw&d_headId=3eced685cf7bc02dd93ce87912d422bf&d_ckId=e6d4915bb674508e8daf6b921b73e7d0&d_sfrom=search_fp_bar&d_curPage=0";

    private static final String JOB_URL = "https://www.liepin.com/zhaopin/?pubTime=30&ckid=7ea8dc1742572ee1&fromSearchBtn=2&compkind=&isAnalysis=&init=-1&searchType=1&flushckid=1&dqs=010&industryType=&jobKind=2&sortFlag=15&industries=&salary=&compscale=&clean_condition=&key=java&headckid=67bf5b4181e46ffe&d_pageSize=40&siTag=k_cloHQj_hyIn0SLM9IfRg%7ENw_YksyhAxvGdx7jL2ZbaQ&d_headId=78484a4c4094624b4e7d8c0d0d3aead1&d_ckId=8585f1a0f456b513c70a6eed38661c05&d_sfrom=search_prime&d_curPage=";
    private static final String PREIFX = "https://www.liepin.com/";

    public static void main(String[] args) throws IOException, InterruptedException, WriteException, IllegalAccessException {
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

        FileOutputStream os = new FileOutputStream(new File("d://jobs.xls"), true);
        //创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(os);
        //创建新的一页
        WritableSheet sheet = workbook.createSheet("First Sheet", 0);
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.933.400 QQBrowser/9.4.8699.400";


        String getUrl = JOB_URL + 0;
        HttpGet httpGet = new HttpGet(getUrl);
        mockCookie();
        httpGet.setHeader("User-Agent", userAgent);
        httpGet.setHeader("Referer", "https://www.liepin.com/");
        HttpEntity entity = httpclient.execute(httpGet).getEntity();
        // 显示结果
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String html = sb.toString();
        Document doc = Jsoup.parse(html);


        Elements validUrls = doc.select(".pagerbar > a");
        String nextUrl = "";
        for (Element validUrl : validUrls) {
            if (validUrl.attr("class").equals("last")) {
                nextUrl = validUrls.get(validUrls.indexOf(validUrl) - 1).attr("href");
            }
        }
        List<Job> jobs = new ArrayList<>();

        for (int id = 0; id < 20; id++) {
            Thread.sleep(1500);

            nextUrl = PREIFX + nextUrl;
            System.out.println(nextUrl);
            httpGet = new HttpGet(nextUrl);

            httpGet.setHeader("User-Agent", userAgent);
            httpGet.setHeader("Referer", "https://www.liepin.com/");
            mockCookie();


            entity = httpclient.execute(httpGet).getEntity();
            // 显示结果
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            line = null;
            sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            html = sb.toString();
            System.out.println(html);

            Document jobsDoc = Jsoup.parse(html);
            Element jobList = jobsDoc.select(".sojob-list").get(0);
            Elements li = jobList.select("li");
            for (Element element : li) {
                String edu = element.select(".edu").text();
                String desc = element.select(".condition").attr("title");

                if (edu.contains("统招本科") || desc.contains("统招本科"))
                    continue;

                Elements jobInfo = element.select(".job-info");
                String title = jobInfo.select("a").text();
                String link = jobInfo.select("a").attr("href");

                Elements companyEle = element.select(".company-name");
                String company = companyEle.select("a").text();
                String companyLink = companyEle.select("a").attr("href");

                String address = element.select(".area").text();
                String temptation = element.select(".temptation").text();
                Job job = new Job(title, link, company, companyLink, address, edu, desc, temptation);
                System.out.println(job);
                jobs.add(job);
            }

            validUrls = jobsDoc.select(".pagerbar > a");
            for (Element validUrl : validUrls) {
                if (validUrl.attr("class").equals("last")) {
                    nextUrl = validUrls.get(validUrls.indexOf(validUrl) - 1).attr("href");
                }
            }
        }
        Class jobClass = Job.class;
        Field[] dFields = jobClass.getDeclaredFields();
        int fieldSize = dFields.length;
        for (int i = 0; i < jobs.size(); i++) { //行数
            for (int j = 0; j < fieldSize; j++) {
                Field field = dFields[j];
                field.setAccessible(true);
                Job job = jobs.get(i);
                Label label = new Label(j, i, field.get(job).toString());
                sheet.addCell(label);
            }
        }
        workbook.write();
        workbook.close();
        os.close();

    }

    private static void mockCookie() {
        BasicCookieStore cookieStore = new BasicCookieStore();

//        cookieStore.addCookie(setWeiboCookies("__uuid", "1526454337108.30", null));
//        cookieStore.addCookie(setWeiboCookies("_uuid", "67C4D419FFA842196E7FD23DD8D726CD", null));
//        cookieStore.addCookie(setWeiboCookies("c_flag", "2f97f3268d84bd971a294f0a51c16d8b", null));
//        cookieStore.addCookie(setWeiboCookies("new_user", "false", null));
//        cookieStore.addCookie(setWeiboCookies("need_bind_tel", "false", null));
//        cookieStore.addCookie(setWeiboCookies("gr_user_id", "c5310df4-05a9-47d5-9707-ee8246158940", null));
//        cookieStore.addCookie(setWeiboCookies("fe_work_exp_add", "true", null));
//        cookieStore.addCookie(setWeiboCookies("6ab8742", "beaf496daefa34b09a897ee26e6345b5", null));
//        cookieStore.addCookie(setWeiboCookies("user_kind", "0", null));
//        cookieStore.addCookie(setWeiboCookies("is_lp_user", "true", null));
//        cookieStore.addCookie(setWeiboCookies("login_temp", "islogin", null));
//        cookieStore.addCookie(setWeiboCookies("user_vip", "0", null));
//        cookieStore.addCookie(setWeiboCookies("user_name", "%E6%9D%8E%E9%A3%9E", null));
//        cookieStore.addCookie(setWeiboCookies("user_photo", "55557f3b28ee44a8919620ce01a.gif", null));
//        cookieStore.addCookie(setWeiboCookies("firsIn", "1", null));
//        cookieStore.addCookie(setWeiboCookies("gr_session_id_bad1b2d9162fab1f80dde1897f7a2972", "3efe7b8f-c9e7-458d-8fa9-d115d09ff9b4", null));
//        cookieStore.addCookie(setWeiboCookies("gr_cs1_3efe7b8f-c9e7-458d-8fa9-d115d09ff9b4", "UniqueKey%3Ac2ed4a8bc6cabd111a667e00ad0d9493", null));
//        cookieStore.addCookie(setWeiboCookies("verifycode", "67f204b841614f329d01ac788b960a29", null));
//        cookieStore.addCookie(setWeiboCookies("ADHOC_MEMBERSHIP_CLIENT_ID1.0", "932e1163-b308-2238-3551-1838c6bb8d03", null));
//        cookieStore.addCookie(setWeiboCookies("_fecdn_", "1", null));
//        cookieStore.addCookie(setWeiboCookies("JSESSIONID", "649C9EBA6E14F32A8894221EA000CD38", null));
//        cookieStore.addCookie(setWeiboCookies("__tlog", "1528792489150.42%7C00000000%7CR000000075%7Cs_00_pz0%7Cs_00_pz0", null));
//        cookieStore.addCookie(setWeiboCookies("__session_seq", "79", null));
//        cookieStore.addCookie(setWeiboCookies("__uv_seq", "72", null));
//        cookieStore.addCookie(setWeiboCookies("_mscid", "s_00_pz0", null));
//        cookieStore.addCookie(setWeiboCookies("Hm_lvt_a2647413544f5a04f00da7eee0d5e200", "1526454338,1526609820,1528792489", null));
//        cookieStore.addCookie(setWeiboCookies("Hm_lpvt_a2647413544f5a04f00da7eee0d5e200", "1528883976", null));
//        cookieStore.addCookie(setWeiboCookies("abtest", "0", null));
        cookieStore.addCookie(setWeiboCookies("__uuid", "1526454337108.30", null));
        cookieStore.addCookie(setWeiboCookies("c_flag", "2f97f3268d84bd971a294f0a51c16d8b", null));
        cookieStore.addCookie(setWeiboCookies("new_user", "false", null));
        cookieStore.addCookie(setWeiboCookies("need_bind_tel", "false", null));
        cookieStore.addCookie(setWeiboCookies("gr_user_id", "c5310df4-05a9-47d5-9707-ee8246158940", null));
        cookieStore.addCookie(setWeiboCookies("fe_work_exp_add", "true", null));
        cookieStore.addCookie(setWeiboCookies("6ab8742", "beaf496daefa34b09a897ee26e6345b5", null));
        cookieStore.addCookie(setWeiboCookies("user_kind", "0", null));
        cookieStore.addCookie(setWeiboCookies("is_lp_user", "true", null));
        cookieStore.addCookie(setWeiboCookies("user_name", "%E6%9D%8E%E9%A3%9E", null));
        cookieStore.addCookie(setWeiboCookies("gr_session_id_bad1b2d9162fab1f80dde1897f7a2972", "3efe7b8f-c9e7-458d-8fa9-d115d09ff9b4", null));
        cookieStore.addCookie(setWeiboCookies("gr_cs1_3efe7b8f-c9e7-458d-8fa9-d115d09ff9b4", "UniqueKey%3Ac2ed4a8bc6cabd111a667e00ad0d9493", null));
        cookieStore.addCookie(setWeiboCookies("_uuid", "FC0F8EB9FFA844180005C15952483B29", null));
        cookieStore.addCookie(setWeiboCookies("ADHOC_MEMBERSHIP_CLIENT_ID1.0", "932e1163-b308-2238-3551-1838c6bb8d03", null));
        cookieStore.addCookie(setWeiboCookies("abtest", "0", null));
        cookieStore.addCookie(setWeiboCookies("_fecdn_", "1", null));
        cookieStore.addCookie(setWeiboCookies("__tlog", "1536054989883.93%7C00000000%7C00000000%7C00000000%7C00000000", null));
        cookieStore.addCookie(setWeiboCookies("__session_seq", "1", null));
        cookieStore.addCookie(setWeiboCookies("__uv_seq", "1", null));
        cookieStore.addCookie(setWeiboCookies("_mscid", "00000000", null));
        cookieStore.addCookie(setWeiboCookies("Hm_lvt_a2647413544f5a04f00da7eee0d5e200", "1535451780,1536054990", null));
        cookieStore.addCookie(setWeiboCookies("Hm_lpvt_a2647413544f5a04f00da7eee0d5e200", "1536054990", null));
    }

    private static BasicClientCookie setWeiboCookies(String name, String value, String date) {
        BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
        cookie.setDomain(".liepin.com");
        cookie.setPath("/");
        if (StringUtils.isNotBlank(date)) {
            cookie.setExpiryDate(new Date(date));
        } else {
            cookie.setExpiryDate(null);
        }
        return cookie;
    }

}

class Job {
    private String title;
    private String company;
    private String link;
    private String companyLink;
    private String address;
    private String edu;
    private String desc;
    //    String industry;
    private String temptation;

    public Job(String title, String link, String company, String companyLink, String address, String edu, String desc, String temptation) {
        this.title = title;
        this.link = link;
        this.company = company;
        this.companyLink = companyLink;
        this.address = address;
        this.edu = edu;
        this.desc = desc;
        this.temptation = temptation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyLink() {
        return companyLink;
    }

    public void setCompanyLink(String companyLink) {
        this.companyLink = companyLink;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEdu() {
        return edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTemptation() {
        return temptation;
    }

    public void setTemptation(String temptation) {
        this.temptation = temptation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Job{");
        sb.append("title='").append(title).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append(", company='").append(company).append('\'');
        sb.append(", companyLink='").append(companyLink).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", edu='").append(edu).append('\'');
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", temptation='").append(temptation).append('\'');
        sb.append('}');
        return sb.toString();
    }
}