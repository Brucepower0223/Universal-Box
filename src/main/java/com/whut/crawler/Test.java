package com.whut.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.file.FileUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/12/28.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Map<String, Object> resultmap = new HashMap<>();
//        String gjjJsonstr = FileUtils.readFileContent("G:\\fastjson.txt");
        String gjj="{\"process_msg\":\"接口调用成功\",\"process_code\":\"SCCS100\",\"location_cid\":\"1100\",\"code\":\"09c2a045-e998-4ba8-a0ef-5db2cb7df372\",\"data\":[{\"login_type\":\"2\",\"login_desc\":\"身份证号\",\"login_tip\":null,\"args\":[{\"arg_name\":\"loginId\",\"arg_type\":\"1\",\"arg_title\":\"登录账号\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入身份证号\",\"arg_validRule\":\"^([0-9]{17}[0-9,X]{1})|([0-9]{15})$\",\"arg_validMsg\":\"请输入15或\n" +
                "18位身份证,如包含“X”请输入大写\",\"arg_options\":null,\"arg_need\":null},{\"arg_name\":\"password\",\"arg_type\":\"4\",\"arg_title\":\"登录密码\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入登录密码\",\"arg_validRule\":null,\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null}]},{\"login_type\":\"8\",\"login_desc\":\"联名卡号\",\"login_tip\":null,\"args\":[{\"arg_name\":\"loginId\",\"arg_type\":\"1\",\"arg_title\":\"登录账号\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入联名卡>号\",\"arg_validRule\":\".*?\",\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null},{\"arg_name\":\"password\",\"arg_type\":\"4\",\"arg_title\":\"登录密码\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入登录密码\",\"arg_validRule\":null,\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null}]},{\"login_type\":\"7\",\"login_desc\":\"个人登记号\",\"login_tip\":null,\"args\":[{\"arg_name\":\"loginId\",\"arg_type\":\"1\",\"arg_title\":\"登录账号\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入个人登记号\",\"arg_validRule\":\".*?\",\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null},{\"arg_name\":\"password\",\"arg_type\":\"4\",\"arg_title\":\"登录密码\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入登录密码\",\"arg_validRule\":null,\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null}]},{\"login_type\":\"14\",\"login_desc\":\"军官证号\",\"login_tip\":null,\"args\":[{\"arg_name\":\"loginId\",\"arg_type\":\"1\",\"arg_title\":\"登录账号\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入军官证号\",\"arg_validRule\":\".*?\",\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null},{\"arg_name\":\"password\",\"arg_type\":\"4\",\"arg_title\":\"登录密码\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入登\n" +
                "录密码\",\"arg_validRule\":null,\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null}]},{\"login_type\":\"15\",\"login_desc\":\"护照号\",\"login_tip\":null,\"args\":[{\"arg_name\":\"loginId\",\"arg_type\":\"1\",\"arg_title\":\"登录账号\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入护照号\",\"arg_validRule\":\".*?\",\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null},{\"arg_name\":\"password\",\"arg_type\":\"4\",\"arg_title\":\"登录密码\",\"arg_imgUrl\":null,\"arg_imgUrlbase64\":null,\"arg_defaulVal\":null,\"arg_tip\":\"请输入登录密码\",\"arg_validRule\":null,\"arg_validMsg\":null,\"arg_options\":null,\"arg_need\":null}]}],\"tips\":[],\"supplement\":null}";
        JSONObject jobj = JSON.parseObject(gjj);
        String process_code = jobj.getString("process_code");
        String code = jobj.getString("code");
        resultmap.put("data", JSONObject.parseArray(jobj.getJSONArray("data").toJSONString().replace("arg_validRule", "arg_validrule").replace("arg_validMsg", "arg_validmsg").replace("arg_imgUrlbase64", "arg_imgurlbase64").replace(".*?","\\S")));
        resultmap.put("tips", jobj.getJSONArray("tips"));
    }
}
