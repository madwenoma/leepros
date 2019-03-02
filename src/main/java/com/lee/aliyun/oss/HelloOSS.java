package com.lee.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;

import java.io.*;
import java.net.URL;
import java.util.Date;

public class HelloOSS {

    // Endpoint以杭州为例，其它Region请按实际情况填写。
    static String endpoint = "oss-cn-beijing.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    static String accessKeyId = "LTAIj71dLljrB6H3";
    static String accessKeySecret = "JIoaH9RLI4yyCdQ0RItHO5SjRnIDlu";

    static String bucketName = "buketbysdk";
    static String objectName = "firstfilebysdk2.txt";

    public static void main(String[] args) throws IOException {
//        createBucket();
        uploadFile();
//        downloadFile();

    }

    private static void createBucket() {

        String bucketName = "bukit2";

// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// 创建存储空间。
        ossClient.createBucket(bucketName);

        //设置bucket权限
        ossClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
// 关闭OSSClient。
        ossClient.shutdown();
    }

    private static void uploadFile() throws IOException {
        String accessKeyId = "LTAIj71dLljrB6H3";
        String accessKeySecret = "JIoaH9RLI4yyCdQ0RItHO5SjRnIDlu";

        String bucketName = "yunzongtest1";
        String objectName = "firstfilebysdk22.txt";
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        String content = "Hello OSS";
//        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

//        ossClient.shutdown();


        File file = new File("C:\\Users\\Administrator\\gopath\\src\\github.com\\gpmgo\\gopm\\gopm.go");

        final String keySuffixWithSlash = "MyObjectKey/";
//        ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
//        PutObjectResult result =ossClient.putObject(bucketName, keySuffixWithSlash + "gopm.go", new ByteArrayInputStream(content.getBytes()));
        PutObjectResult result =ossClient.putObject(bucketName, keySuffixWithSlash + "gopm.go", file);
        System.out.println("Creating an empty folder " + keySuffixWithSlash + "\n");
        System.out.println(result.getETag());
        /*
         * Verify whether the size of the empty folder is zero
         */
        OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash + "gopm.go");
        System.out.println("Size of the empty folder '" + object.getKey() + "' is " +
                object.getObjectMetadata().getContentLength());
        object.getObjectContent().close();

    }


    private static void downloadFile() throws IOException {

        accessKeyId = "LTAI6WjW5Ev6488M";
        accessKeySecret = "033ohKorc23XtOkSBKstN4Re8KNssg";
// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        //cvs/isv1/基金净值查询.xlsx
        bucketName = "bukit";
        objectName = "cvs/isv1/README.md";
        // 获取存储空间的访问权限。LTAI6WjW5Ev6488M
        AccessControlList acl = ossClient.getBucketAcl(bucketName);
        System.out.println(acl.toString());

        // 判断文件是否存在。
        boolean found = ossClient.doesObjectExist(bucketName, objectName);
        System.out.println(found);

// 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
// 获取文件的访问权限。
        ObjectAcl objectAcl = ossClient.getObjectAcl(bucketName, objectName);
        System.out.println(objectAcl.getPermission().toString());


// 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
        InputStream content = ossObject.getObjectContent();
        if (content != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("\n" + line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            content.close();
        }

// 关闭OSSClient。
        ossClient.shutdown();
    }


    private static void listFile() {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "<yourAccessKeyId>";
        String accessKeySecret = "<yourAccessKeySecret>";
        String bucketName = "<yourBucketName>";

// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// ossClient.listObjects返回ObjectListing实例，包含此次listObject请求的返回结果。
        ObjectListing objectListing = ossClient.listObjects(bucketName);
// objectListing.getObjectSummaries获取所有文件的描述信息。
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + ")");
        }

// 关闭OSSClient。
        ossClient.shutdown();
    }

    private static void deleteFile() {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "<yourAccessKeyId>";
        String accessKeySecret = "<yourAccessKeySecret>";
        String bucketName = "<yourBucketName>";
        String objectName = "<yourObjectName>";

// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// 删除文件。
        ossClient.deleteObject(bucketName, objectName);

// 关闭OSSClient。
        ossClient.shutdown();
    }
}
