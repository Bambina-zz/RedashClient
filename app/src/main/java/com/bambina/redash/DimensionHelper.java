package com.bambina.redash;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by hirono-mayuko on 2017/05/26.
 */

public class DimensionHelper {
    public static float convertDpToPx(Context context, float dp){
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
