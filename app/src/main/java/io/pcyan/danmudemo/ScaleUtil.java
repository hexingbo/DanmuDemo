package io.pcyan.danmudemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.DimenRes;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕的相关类
 */
public final class ScaleUtil {
    private static Point displaySize;

    private static Context getContext() {
        return MyApplication.getContext();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static Point getDisplaySize() {
        if (displaySize == null) {
            WindowManager windowManager =
                    (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            display.getSize(displaySize = new Point());
        }
        return displaySize;
    }

    private ScaleUtil() {}

    /**
     * 从资源获取尺寸
     */
    public static float getResDimen(@DimenRes int dimen){
        return getContext().getResources().getDimension(dimen);
    }

    /**
     * 获取屏幕宽度：PX
     * @return
     */
    public static int getDisplayWidth() {
        return getDisplaySize().x;
    }

    /**
     * 获取屏幕高度：PX
     * @return
     */
    public static int getDisplayHeight() {
        return getDisplaySize().y;
    }

    /**
     * DIP转PX
     * @param value
     * @return
     */
    public static int dip2px(float value) {
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * dipScale + 0.5f);
    }

    /**
     * DIP转PX
     */
    public static int dip2px(int dip, Fragment fragment) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, fragment.getActivity().getResources().getDisplayMetrics());
    }

    /**
     * PX转DIP
     * @param value
     * @return
     */
    public static int px2dip(int value) {
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value / dipScale + 0.5f);
    }

    /**
     * SP转PX
     * @param value
     * @return
     */
    public static int sp2px(float value) {
        float spScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (value * spScale + 0.5f);
    }

    /**
     * dp转换px
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * PX转SP
     * @param value
     * @return
     */
    public static int px2sp(int value) {
        float spScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / spScale + 0.5f);
    }

    /**
     * 计算屏幕宽的比例值 displayWidth / value
     * @param value
     * @return
     */
    public static int scaleWidth(int value) {
        if (value <= 0) value = 1;
        return getDisplayWidth() / value;
    }

    /**
     * 计算屏幕高的比例值 displayHeight / value
     * @param value
     * @return
     */
    public static int scaleHeight(int value) {
        if (value <= 0) value = 1;
        return getDisplayHeight() / value;
    }

    public static float getWidthPixels() {

        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static float getHeightPixels() {

        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 判断屏幕是横屏还是竖屏
     * @return boolean true 横屏 false 竖屏
     */
    public static boolean isScreenChange(Context context) {

        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
            return false;
        }
        return false;
    }
}
