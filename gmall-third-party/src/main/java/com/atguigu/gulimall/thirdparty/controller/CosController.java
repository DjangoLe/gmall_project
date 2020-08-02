package com.atguigu.gulimall.thirdparty.controller;

import com.atguigu.common.utils.R;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class CosController {
    @Autowired
    COSClient cosClient;

    @Value("cos.ap-beijing.myqcloud.com")
    private String endpoint;

    @Value("gyl-1301252219")
    private String bucketName;

    @Value("AKIDFo7B0ai0f5sPglJBUZnmXqEjyxiD6Iwi")
    private String accessId;

    String host = "https://" + bucketName + "." + endpoint; // host的格式为 bucketname.endpoint
    // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";
    String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    String dir = format + "/"; // 用户上传文件时指定的前缀。

    Map<String, String> respMap = null;

    @RequestMapping("/cos/policy")
    public R policy() {
        String secretId = "AKIDFo7B0ai0f5sPglJBUZnmXqEjyxiD6Iwi";
        String secretKey = "ex0nOaZJJ8qKYVJ6ey5yby50B7qIVSAe";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        String key="exampleobject";
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucketName, key);
// 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
// 这里设置签名在半个小时后过期
        long expireTime = 30;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expirationDate = new Date(expireEndTime);
        req.setExpiration(expirationDate);
        URL url = cosClient.generatePresignedUrl(req);
        System.out.println(url.toString());
        respMap = new LinkedHashMap<String, String>();
        respMap.put("accessid", accessId);
        respMap.put("signature", url.toString());
        respMap.put("dir", dir);
        respMap.put("host", host);
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        return R.ok().put("data",respMap);
    }
}
