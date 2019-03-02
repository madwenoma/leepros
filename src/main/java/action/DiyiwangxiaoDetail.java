package action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Created by Administrator on 2017/5/4.
 */
public class DiyiwangxiaoDetail {


    private static BasicClientCookie setWeiboCookies(String name, String value, String date) {
        BasicClientCookie2 cookie = new BasicClientCookie2(name, value);
        cookie.setDomain("newexam.1wangxiao.com");
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
        dealFile();


        String[] idsArr = {"354129987703", "354129894667", "354129985949", "354129895271", "354129990301", "354129895955", "354129983567", "354129891309", "354129984493", "354129896504", "354129983001", "354129896631", "354129987641", "354129896631", "354129986436", "353164939830", "354129990992", "354129896631", "354129984494", "353164938059", "354129983567", "354129894667", "354129992415", "354129895193", "354129986413", "354129892720", "354129988056", "353164940991", "354129985746", "354129896504", "354129989295", "353164939118", "354129989953", "354129892913", "354129985746", "353164940554", "354129987916", "354129893555", "354129985858", "354129896887", "354129985230", "354129893623", "354129989808", "354129892972", "354129984862", "354129896294", "354129988298", "354129894937", "354129990095", "354129891096", "354129990095", "353164938322", "354129989295", "354129896887", "354129984493", "353164940892", "354129992114", "354129896631", "354129992426", "354129891231", "354129992891", "353164940892", "354129992114", "354129892720", "354129991662", "354129896887", "354129993054", "353164939118", "354129990852", "354129895635", "354129991400", "354129894491", "354129989953", "354129894612", "354129985230", "353164938246", "354129984494", "353164940892", "354129987523", "354129895193", "354129989854", "353164939830", "354129986186", "354129894902", "354129990992", "354129896244", "354129994086", "354129891806", "354129983001", "353164938059", "354129989808", "354129890471", "354129991742", "354129896209", "354129992114", "353164939095", "354129994557", "353164940991", "354129986436", "353164938322", "354129983160", "354129892720", "354129986436", "354129893307", "354129992471", "354129891096", "354129993593", "354129890471", "354129986314", "354129896977", "354129994693", "354129895526", "354129991881", "354129893844", "354129991662", "354129896887", "354129993788", "354129895074", "354129983567", "354129893555", "354129990104", "354129894491", "354129986314", "353164938040", "354129988298", "354129893555", "354129994037", "354129892913", "354129992114", "354129893307", "354129984248", "354129891309", "354129993054", "353164940554", "354129989178", "354129892965", "354129994557", "354129893544", "354129989808", "354129896209", "354129987523", "354129895997", "354129988183", "354129893555", "354129986211", "353164937716", "354129993788", "354129896209", "354129988183", "354129894667", "354129990104", "354129896631", "354129993767", "354129896887", "354129989475", "353164940554", "354129983567", "354129896209", "354129984494", "354129894853", "354129992426", "353164940788", "354129989178", "354129896504", "354129989808", "354129895997", "354129985230", "353164938059", "354129993767", "354129896209", "354129990992", "353164940991", "354129992426", "354129892913", "354129983360", "353164940892", "354129991944", "354129896977", "354129986436", "354129894491", "354129990852", "353164937716", "354129985746", "354129893307", "354129988056", "353164940892", "354129987916", "354129895193", "354129986413", "353164938246", "354129983567", "354129895193", "354129986186", "354129891531", "354129989953", "353164938040", "354129992886", "354129894612", "354129993847", "354129894603", "354129989854", "354129893307", "354129991003", "354129895955", "354129990338", "354129896244", "354129985630", "354129891096", "354129989475", "354129896294", "354129983567", "354129891531", "354129990095", "354129896631", "354129993767", "354129893844", "354129990095", "353164940788", "354129988000", "353164937882", "354129994922", "353164938246", "354129992426", "353164939095", "354129992415", "354129895955", "354129993996", "354129895193", "354129986149", "354129892684", "354129990104", "354129895074", "354129994693", "354129892684", "354129991881", "354129894603", "354129985858", "353164940554", "354129992415", "354129891309", "354129993054", "354129895997", "354129986211", "354129896244", "354129994037", "354129892965", "354129990852", "354129895997", "354129991742", "354129892622", "354129985230", "353164940788", "354129989953", "353164938246", "354129984494", "354129891531", "354129986149", "354129896887", "354129983001", "354129892720", "354129983160", "354129895997", "354129983360", "354129894603", "354129983567", "354129892972", "354129984248", "354129895997", "354129984493", "354129896209", "354129984494", "353164938040", "354129984862", "354129895635", "354129985095", "354129893555", "354129985230", "354129894853", "354129985630", "354129895526", "354129985746", "354129896244", "354129985858", "354129891806", "354129985949", "354129893623", "354129986149", "354129894937", "354129986186", "353164937716", "354129986211", "353164937882", "354129986314", "353164938040", "354129986413", "353164938059", "354129986436", "353164938246", "354129986550", "353164938322", "354129987523", "353164939095", "354129987641", "353164939118", "354129987703", "353164939830", "354129987815", "353164939907", "354129987822", "353164940554", "354129987916", "353164940788", "354129988000", "353164940892", "354129988056", "353164940991", "354129988183", "354129890471", "354129988226", "354129891096", "354129988298", "354129891231", "354129988606", "354129891309", "354129988906", "354129891346", "354129989178", "354129891531", "354129989295", "354129891806", "354129989475", "354129892611", "354129989808", "354129892622", "354129989854", "354129892684", "354129989953", "354129892720", "354129990095", "354129892913", "354129990104", "354129892965", "354129990301", "354129892972", "354129990338", "354129893196", "354129990852", "354129893307", "354129990992", "354129893523", "354129991003", "354129893544", "354129991400", "354129893555", "354129991418", "354129893623", "354129991662", "354129893844", "354129991742", "354129894491", "354129991881", "354129894603", "354129991944", "354129894612", "354129992114", "354129894667", "354129992415", "354129894853", "354129992426", "354129894902", "354129992471", "354129894937", "354129992886", "354129895074", "354129992891", "354129895193", "354129993054", "354129895271", "354129993593", "354129895526", "354129993767", "354129895635", "354129993788", "354129895955", "354129993847", "354129895997", "354129993996", "354129896209", "354129994037", "354129896244", "354129994086", "354129896294", "354129994418", "354129896504", "354129994557", "354129896631", "354129994693", "354129896887", "354129994922", "354129896977"};

        HashSet<String> ids = new HashSet(Arrays.asList(idsArr));


        String url = "http://newexam.1wangxiao.com/Exam/PaperShow/";
        HttpClient httpclient = HttpClientBuilder.create().build();
        File file = new File("c:\\detail.txt");
        HttpContext localContext = new BasicHttpContext();


        BasicCookieStore cookieStore = new BasicCookieStore();

        cookieStore.addCookie(setWeiboCookies("SP.NET_SessionId", "oy3lyti432hqqv1ybgq1knti", null));
        cookieStore.addCookie(setWeiboCookies2("Hm_lpvt_57eee8030c0aeb053498e048cb50ce57", "1493896098", null));
        cookieStore.addCookie(setWeiboCookies("Hm_lpvt_69fc0ede830b701b222106005a9fd5ed", "1493799088", null));
        cookieStore.addCookie(setWeiboCookies2("Hm_lvt_57eee8030c0aeb053498e048cb50ce57", "149,379,908,514,938,000,000,000,000,000", null));
        cookieStore.addCookie(setWeiboCookies("Hm_lvt_69fc0ede830b701b222106005a9fd5ed", "1493799088", null));
        cookieStore.addCookie(setWeiboCookies("_exam_Wizard", "2075", null));
        cookieStore.addCookie(setWeiboCookies2("ppkey", "S420kDxOFxQGn8NPDcvY/lojiAu6ket4/rBDLS6RcjRxmhXge4mChCggOOC/VVy41s9qSYcAZtbzRBYo35JPQMjsxYa76tzwRniGHS9i0G9QMnJLDe9FOCNO49da+Cxl", null));
        cookieStore.addCookie(setWeiboCookies2("safedog-flow-item", "5B235F2FEA4A5243A046C57D9F9E7662", null));
        cookieStore.addCookie(setWeiboCookies("tUUU_asdscca", "FVI9M4Y3yTE+AoPW16ikFdaf2Pz8lqKr6k6EjUbPqW+Lc1cQ55Rl4CvVOKVd1RMPO1zdvLLT7BlfKArf3lNnpsmrg/EF9lWXbWOLXzqFs2Atodeh8cW0pbVaWYN5nwn3ZNTJNzAQDmb+hVZB3HablN6dfM8VZTfC", null));
        System.out.println(ids.size());
        for (String id : ids) {
            HttpGet get = new HttpGet();
            get.setURI(URI.create(url + id));
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.933.400 QQBrowser/9.4.8699.400");
            localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
            HttpResponse response = null;
            response = httpclient.execute(get, localContext);
            String content = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(content);

            List<String> lines = new ArrayList<String>();
            Document doc = Jsoup.parse(content);
            Elements title = doc.select("div.yuce > h2");
//            System.out.println(title.text());
            lines.add("");
            lines.add(title.text());

            Elements selectQ = doc.select("div#AA_1");
            selectQ = selectQ.select(".pageitem");
            for (Element question : selectQ) {
                String qTitle = question.select("div.tit").text();
//                System.out.println(qTitle);
                lines.add(qTitle);
                Elements answers = question.select("div.ib label");
                for (Element answer : answers) {
//                    System.out.println(answer.text());
                    lines.add(answer.text());
                }
                Elements answerLi = question.select(".q_error > ul > li");
                for (Element li : answerLi) {
//                    System.out.println(li.text());
                    lines.add(li.text());
                }

//            FileUtils.write(file, content, "UTF-8", true);
            }
            FileUtils.writeLines(file, lines, true);
            get.releaseConnection();
            Thread.sleep(10000);
        }


    }

    static String professional = "相关专业知识";
    static String basic = "基础知识";


    public static void dealFile() throws IOException {
        File file = new File("c:\\detail.txt");

        String content = FileUtils.readFileToString(file);
        String[] tests = content.split("===");
        File basicTest = new File("c:\\basic.txt");
        File professionalTest = new File("c:\\professional.txt");
        for (String test : tests) {
            String title = test.split("\r\n")[1];
            if (title.contains(basic)) {
                FileUtils.write(basicTest, test + "\r\n", true);
            }
            if (title.contains(professional)) {
                FileUtils.write(professionalTest, test + "\r\n", true);
            }
        }


        List<String> lines = FileUtils.readLines(file);
        for (String line : lines) {
            System.out.println(line);
        }
    }


}
