package com.example.walden.jbox2dtest;

import android.content.Context;

/**
 * Created by wangjt on 2017/7/25.
 * 密度转化
 */

public class Util {

    public static final int tag1 = 110;
    public static final int tag2 = 112;

    public static int dp2px(Context context, int dp) {
        return (int) context.getResources().getDisplayMetrics().densityDpi * dp;
    }

    public static int px2dp(Context context, int px) {
        return px / context.getResources().getDisplayMetrics().densityDpi;
    }
}
