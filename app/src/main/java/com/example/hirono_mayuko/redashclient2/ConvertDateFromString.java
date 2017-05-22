package com.example.hirono_mayuko.redashclient2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hirono-mayuko on 2017/04/25.
 */

public class ConvertDateFromString {
    public static Date parse(String date, SimpleDateFormat format){
        Date formatDate = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            formatDate = format.parse(date);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return formatDate;
    }
}
