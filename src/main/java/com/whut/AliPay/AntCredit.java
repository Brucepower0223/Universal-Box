package com.whut.AliPay;

//import com.alibaba.fastjson.JSONObject;
//import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
//import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
//import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthqueryRequest;
//import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
//import com.antgroup.zmxy.openplatform.api.response.ZhimaAuthInfoAuthqueryResponse;
//import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;


/**
 * 接入蚂蚁金服芝麻信用评分的接口
 *
 * @author Bruce
 */
public class AntCredit {

//    //芝麻开放平台地址
//    private String gatewayUrl = "https://zmopenapi.zmxy.com.cn/openapi.do";
//    //商户应用 Id
//    private String appId = "2017101909388445";
//    //商户 RSA 私钥
//    private String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCKF+zbeEP33/S4LOabgEeFl33J35mFJdpBx+Qy3NpXOVSIggeA0IDzhpdyER65XZUmQuth05sodVHSkpy7pGraa0EW58XOh7BDkjCFh6PChqhPjmicFJrXa7cGm6OAcSArO+/iUgNqInt6hJJzMDyp2cTGumbaEkOiAY+57wzyXweDw2lG6msWo6Ffc5cy/o/bUU29J+8Sxnkd9JHMVgl8NGzq/NdSZRtRh1lPCJEgmPMzQDRvjSnWZhqziWNft00jbGLoFGUoOC5eQCLYVl1IA3/Y8LbmXhUYy+ova9xjljpazoyzoTop0MhirBU1J1jRvzNEAh3bSER4dwG4D2C9AgMBAAECggEAEngCsJOUG4mzVBKFQRTV+/L32tDx2lHmr9eJGJhbB0FeoecqE7tPE8LTbGkHv+jZMsXOb0X6wV13dP8wwRPLSqz/ho228dlJ5K+fl6w9ZZ3W0tUL/pvu3ZLFZrfYFSyMDdw1SAKJ/F7iPs+OOqOf6WN7osx5Rv8cVoV48HgdNij3DfXs7HujaoPaSwBGsk2vdt0PZkex0Wth4Kw4yvvtBu+686/6xEbmIaeINtk+DVrNhwYCf6hWCJaOS/Y8bAeRfM1d+73i8UjqqKzS9uX6cP3PntOHaKUKjq6bAzqVWrb3Mg5VBDqj/G9DdgliDafXfBKNhvhPdmAxKhNBNYeywQKBgQDKQvI5VUqk8AKnxsc2jLXRykizaj8qlfJSesEbqQP/V42TWKYuQkeYFUcT2VZ8rtSJCu8mkMAmvYk+j2MKOwnKYdZjBDeZZhcwOwQIwVF8AVPXQJuuhHDqFpe9zG5fYkm6OdJDkZ4OO0eirqmqHGTQ6Lv7kcXHUFLzMH56NWnDrQKBgQCuyIOHb7HTstCeCVZuEWAV70FBwhGRI2QLpgf83TtQ8fSSu3bN2zslWQAqZyMdN+IQNGv/P05KqBsw8YS0fsgpZqOsu/+mMWl1Bbi1tpuyBRvh0oIfITg5anBoRBnU0nD7liYbq5Z044vszIxF6jxv540WLjH8crn7bwmi6bEzUQKBgQCTe/V8cQEY/BozlPK2iCwcadBRY2vj9arZEcG2FaxgiMsnYkDrAlGKSBmoE5bJHALGWz6/dFVo6lQlVhD16M7e0DCvyudv582I0b/DUpPERIOKNgZPXyumxgqnz2W8D8OLE5aKxqfl4Lv/mDUHYeTxoINSkZ0SFTQnOHlBS6lvkQKBgQCgM09DQyBF/p7yS3K53hko4av5fsylMEAR4TNvFMO6dJsMJuFE282BdOsO8oufy0ncOoBGaxrymNSbbrOI0K/cKqSYvZQcGywiw4TCoUZhzAb7W/xCVit3jNTM7KaUh/Jg1wXE1OhJ6Qqml/F2X7TXiO+rRdoxHmLTT7oQZYwykQKBgGP/x3D59Jt/xQXMXfcMSptDko3b0b8pFrl0dc9+PbcjmBSbe/HFiBwFhRGSbRfnIETDPMAu1uU507Rw19WDICYNWylj1hvlkQHSalsk8/YeaOIi+3QtHpDiZQjVPFQjJdSSRBA+NxD00u+l+g40706D9nEBBvPv6KSJ4TdesTu1";
//    //芝麻 RSA 公钥
//    private String zhimaPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAihfs23hD99/0uCzmm4BHhZd9yd+ZhSXaQcfkMtzaVzlUiIIHgNCA84aXchEeuV2VJkLrYdObKHVR0pKcu6Rq2mtBFufFzoewQ5IwhYejwoaoT45onBSa12u3BpujgHEgKzvv4lIDaiJ7eoSSczA8qdnExrpm2hJDogGPue8M8l8Hg8NpRuprFqOhX3OXMv6P21FNvSfvEsZ5HfSRzFYJfDRs6vzXUmUbUYdZTwiRIJjzM0A0b40p1mYas4ljX7dNI2xi6BRlKDguXkAi2FZdSAN/2PC25l4VGMvqL2vcY5Y6Ws6Ms6E6KdDIYqwVNSdY0b8zRAId20hEeHcBuA9gvQIDAQAB";
//
//    public void testZhimaCreditScoreGet() {
//        ZhimaCreditScoreGetRequest req = new ZhimaCreditScoreGetRequest();
//        req.setChannel("apppc");
//        req.setPlatform("zmop");
//        req.setTransactionId("201512100936588040000000465158");// 必要参数
//        req.setProductCode("w1010100100000000001");// 必要参数
//        req.setOpenId("268810000007909449496");// 必要参数
//        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
//        try {
//            ZhimaCreditScoreGetResponse response = (ZhimaCreditScoreGetResponse) client.execute(req);
//            System.out.println(response.isSuccess());
//            System.out.println(response.getErrorCode());
//            System.out.println(response.getErrorMessage());
//        } catch (ZhimaApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 发起授权请求
//     */
//    public void testZhimaAuthInfoAuthquery() {
//        ZhimaAuthInfoAuthqueryRequest req = new ZhimaAuthInfoAuthqueryRequest();
//        req.setIdentityType("2");// 必要参数
//        req.setIdentityParam("{\"certNo\":\"421083199309100036\",\"certType\":\"IDENTITY_CARD\",\"name\":\"方劲\"}");// 必要参数
//        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
//                zhimaPublicKey);
//        try {
//            ZhimaAuthInfoAuthqueryResponse response = client.execute(req);
//            System.out.println(JSONObject.toJSON(response));
//        } catch (ZhimaApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        AntCredit antCredit = new AntCredit();
//        antCredit.testZhimaAuthInfoAuthquery();
////        antCredit.testZhimaCreditScoreGet();
//    }


}
