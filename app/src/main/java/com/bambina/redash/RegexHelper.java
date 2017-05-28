package com.bambina.redash;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hirono-mayuko on 2017/05/17.
 */

public class RegexHelper {
    public static String extractDomainName(String str){
        str = removeNonVisibleChars(str);
        Pattern p = Pattern.compile("^http[s]?://([a-z0-9.]*)/?$");
        Matcher m = p.matcher(str);
        if(m.find()){
            return m.group(1);
        } else {
            return "";
        }
    }

    public static String extractUri(String str){
        str = removeNonVisibleChars(str);
        Pattern p = Pattern.compile("^(http[s]?://[a-z0-9.]*)/?$");
        Matcher m = p.matcher(str);
        if(m.find()){
            return m.group(1);
        } else {
            return "";
        }
    }

    public static String extractNumbers(String str){
        str = removeNonVisibleChars(str);
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        if(m.find()){
            return m.group(0);
        } else {
            return "";
        }
    }

    public static String removeNonVisibleChars(String str){
        if(str.length() == 0) return "";

        return str.replaceAll("\\s+","");
    }
}
