package com.common.utils.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Bruce
 * @date 2017/12/15
 */
public class Test {

    public static void main(String[] args) {
        String account="6214850108267417";
        String pattern = "^[a-zA-Z0-9-_+!@#$%^&*()]+$";

        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(account);
        System.out.println(matcher.find());

    }

}
