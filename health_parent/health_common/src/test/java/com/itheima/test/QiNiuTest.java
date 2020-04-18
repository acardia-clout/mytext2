package com.itheima.test;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

/**
 * 七牛云入门案例
 */
public class QiNiuTest {
    /**
     * 文件上传测试
     */
    //@Test
    public void testUpload(){
        //Zone.zone0() == 华东
        //构造一个带指定 Region 对象的配置类  Region.region0()最新的版本
        Configuration cfg = new Configuration(Zone.zone0()); //跟创建存储空间的域名一致
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "A_jKJnB1bpEPHn1QdqzPpelrCPU6QfJbJnv-_RR4";
        String secretKey = "CldWf-r2Z6mEkuqQD8zEOVj5U_jIRK-Dcea6T9oB";
        String bucket = "heimahealth87";
//如果是Windows情况下，格式是 D:\\qiniu\\test.png
        String localFilePath = "C:\\Users\\Administrator\\Desktop\\1.jpg";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "1.jpg";//保证文件名称唯一 否则报错

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }

    }


    /**
     * 文件删除
     */
    //@Test
    public void deleteFile(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
//...其他参数参考类注释
        String accessKey = "A_jKJnB1bpEPHn1QdqzPpelrCPU6QfJbJnv-_RR4";
        String secretKey = "CldWf-r2Z6mEkuqQD8zEOVj5U_jIRK-Dcea6T9oB";
        String bucket = "heimahealth87";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "1.jpg";//删除的文件名

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
