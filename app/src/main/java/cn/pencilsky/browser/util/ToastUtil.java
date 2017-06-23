package cn.pencilsky.browser.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chenlin on 23/06/2017.
 * 对Toast进行封装
 * 方便程序使用
 */
public class ToastUtil {
    public static void showLong(Context context, String info) {
        Toast.makeText(context.getApplicationContext(), info, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, String info) {
        Toast.makeText(context.getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, int resId) {
        Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, int resId) {
        Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }
}
