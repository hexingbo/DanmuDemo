/**
 * 作者：Administrator
 * 日期: 2016/3/14 11:09
 * 描述:
 */
package io.pcyan.danmudemo;

import android.app.Application;
import android.content.Context;

/**
 * 作者：yanpeicai
 * 日期: 2016/3/14 11:09
 * 描述: 
 */
public class MyApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

}
