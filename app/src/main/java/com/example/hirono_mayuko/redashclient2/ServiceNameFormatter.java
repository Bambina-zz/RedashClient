package com.example.hirono_mayuko.redashclient2;

import android.content.Context;

/**
 * Created by hirono-mayuko on 2017/04/25.
 */

public class ServiceNameFormatter {
    public static String getServiceName(Context context, String name){
        int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
        if(id != 0){
            name = context.getResources().getString(id);
        }
        return name;
    }
}
