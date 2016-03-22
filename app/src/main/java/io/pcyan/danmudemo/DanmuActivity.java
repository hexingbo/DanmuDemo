package io.pcyan.danmudemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.controller.IDanmakuView;

/**
 * 弹幕
 */
public class DanmuActivity extends Activity {

    private static final String TAG = "DanmuActivity";

    private DanmuControl mDanmuControl;
    @Bind(R.id.btn_add_danmu)
    Button btnAddDanmu;

    @Bind(R.id.danmakuView)
    IDanmakuView mDanmakuView;


    /*******TEST********/
    private static int count = 0;

    private int userId = 30224;

    String [] avatars = {
            "http://upload.cguoguo.com/upload/2016-02-29/56d45c8f21dac.jpg",
            "http://upload.cguoguo.com/upload/2015-12-22/5678f5f3e64dd.jpg",
            "http://upload.cguoguo.com/upload/def.jpg"
    };
    /*******TEST********/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dan_mu);
        ButterKnife.bind(this);

        initView();
        initListener();
    }

    private void initView() {
        mDanmuControl = new DanmuControl(this);
        //设置弹幕视图
        mDanmuControl.setDanmakuView(mDanmakuView);
        //设置用户id，区别背景色
        mDanmuControl.setUserId(userId);
    }

    private void initListener() {

    }

    @OnClick(R.id.btn_add_danmu)
    void addDanmu(){
        Log.d(TAG, "addDanmu: ");
        setData();
    }

    private void setData() {
        /*******TEST********/
        count++;
        List<Danmu> danmus = new ArrayList<>();
        Danmu danmu1 = new Danmu(0, userId, "Comment",avatars[0], count+" 我：这是一条弹幕啦啦啦");
        Danmu danmu2 = new Danmu(0, 1, "Comment",avatars[1], count+" 楼主：这又是一条弹幕啦啦啦啦啦这又是一条弹幕啦啦啦啦啦这又是一条弹幕啦啦啦啦啦这又是一条弹幕啦啦啦啦啦这又是一条弹幕啦啦啦啦啦这又是一条弹幕啦啦啦啦啦");
        Danmu danmu3 = new Danmu(0, 3, "Comment",avatars[2], count+" 普通：这还是一条弹幕啦啦啦啦啦啦啦这还是一条弹幕啦啦啦啦啦啦啦");

        danmus.add(danmu1);
        danmus.add(danmu2);
        danmus.add(danmu3);
        Collections.shuffle(danmus);
        danmus.remove(0);
        danmus.remove(0);
        /*******TEST********/


        // 添加弹幕
        mDanmuControl.addDanmu(danmus.get(0));
    }

    ///////////////////////////////////////////////////////////////////////////
    // 必须调用，跟随生命周期
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        mDanmuControl.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmuControl.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmuControl.destroy();
    }
}
