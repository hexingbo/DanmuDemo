package io.pcyan.danmudemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * 弹幕控制器
 */
public class DanmuControl {

    @SuppressWarnings("unused")
    private static final String TAG = "DanmuControl";

    //弹幕显示的时间(如果是list的话，会 * i)，记得加上mDanmakuView.getCurrentTime()
    private static final long ADD_DANMU_TIME = 2200;

    //弹幕背景色
    private static final int PINK_COLOR = 0xb2000000;// 楼主
    private static final int ORANGE_COLOR = 0xffff5a93;// 我
    private static final int BLACK_COLOR = 0xb2000000;// 普通

    //    private int BITMAP_WIDTH = 30;
//    private int BITMAP_HEIGHT = 30;
    private float DANMU_TEXT_SIZE = 14f;//弹幕字体的大小
    private int AVATAR_SIZE = 36;//头像的大小
//    private int   EMOJI_SIZE      = 14;//emoji的大小

    //这两个用来控制两行弹幕之间的间距
    private int DANMU_PADDING = 8;
    private int DANMU_PADDING_INNER = 8;
    private int DANMU_RADIUS = 25;//圆角半径

    private float DANMU_OFFSET = 6;

    //楼主ID
    @SuppressWarnings("FieldCanBeLocal")
    private final int mGoodUserId = 1;
    //用户id
    private int mMyUserId = 2;

    private Context mContext;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private long preDanmuTime;

    /**
     * 设置用户id,区别背景色
     */
    public void setUserId(int userId) {
        this.mMyUserId = userId;
    }

    public DanmuControl(Context context) {
        this.mContext = context;
        setSize();
        initDanmuConfig();
    }

    /**
     * 对数值进行转换，适配手机，必须在初始化之前，否则有些数据不会起作用
     */
    private void setSize() {
//        BITMAP_WIDTH = ScaleUtil.dip2px( BITMAP_HEIGHT);
//        BITMAP_HEIGHT = ScaleUtil.dip2px( BITMAP_HEIGHT);
//        EMOJI_SIZE = ScaleUtil.dip2px(ontext, EMOJI_SIZE);
        AVATAR_SIZE = ScaleUtil.dip2px(AVATAR_SIZE);
        DANMU_PADDING = ScaleUtil.dip2px(DANMU_PADDING);
        DANMU_PADDING_INNER = ScaleUtil.dip2px(DANMU_PADDING_INNER);
        DANMU_RADIUS = ScaleUtil.dip2px(DANMU_RADIUS);
        DANMU_TEXT_SIZE = ScaleUtil.sp2px(DANMU_TEXT_SIZE);
        DANMU_OFFSET = ScaleUtil.sp2px(DANMU_OFFSET);
    }

    /**
     * 初始化配置
     */
    private void initDanmuConfig() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 1); // 滚动弹幕最大显示x行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_MOVEABLE_XXX, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.0f)//越大速度越慢
//                .setScrollSpeedFactor(0.8f)//test
                .setScaleTextSize(1.2f)
                .setCacheStuffer(new BackgroundCacheStuffer(), mCacheStufferAdapter)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
    }

    /**
     * 绘制背景(自定义弹幕样式)
     */
    private class BackgroundCacheStuffer extends SpannedCacheStuffer {
        // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
        final Paint paint = new Paint();

        @Override
        public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
//            danmaku.padding = 20;  // 在背景绘制模式下增加padding
            super.measure(danmaku, paint, fromWorkerThread);
        }

        @Override
        public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
            paint.setAntiAlias(true);
            if (!danmaku.isGuest && danmaku.userId == mGoodUserId) {
                paint.setColor(PINK_COLOR);//粉红 楼主
            } else if (!danmaku.isGuest && danmaku.userId == mMyUserId
                    && danmaku.userId != 0) {
                paint.setColor(ORANGE_COLOR);//橙色 我
            } else {
                paint.setColor(BLACK_COLOR);//黑色 普通
            }
            if (danmaku.isGuest) {//如果是赞 就不要设置背景
                paint.setColor(Color.TRANSPARENT);
            }

            canvas.drawRoundRect(new RectF(left + DANMU_PADDING_INNER, top + DANMU_PADDING_INNER
                            , left + danmaku.paintWidth - DANMU_PADDING_INNER + DANMU_OFFSET,
                            top + danmaku.paintHeight - DANMU_PADDING_INNER),//+6 主要是底部被截得太厉害了，+6是增加padding的效果
                    DANMU_RADIUS, DANMU_RADIUS, paint);
        }

        @Override
        public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top, Paint paint) {
            // 禁用描边绘制
        }
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {

        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
            if (danmaku.text instanceof Spanned) {
                danmaku.text = "";
            }
        }
    };

    public void setDanmakuView(IDanmakuView danmakuView) {
        this.mDanmakuView = danmakuView;
        initDanmuView();
    }

    private void initDanmuView() {
        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });
        }
        if (mDanmakuView != null) {
            mDanmakuView.prepare(new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            }, mDanmakuContext);
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @SuppressWarnings("unused")
    public void hide() {
        if (mDanmakuView != null) {
            mDanmakuView.hide();
        }
    }

    @SuppressWarnings("unused")
    public void show() {
        if (mDanmakuView != null) {
            mDanmakuView.show();
        }
    }

    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void destroy() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }


    /*public void addDanmu(final Danmu danmu) {
        addDanmuGlide(danmu);
    }*/

    /**
     * 添加弹幕
     */
    public void addDanmu(final Danmu danmu) {
        final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.userId = danmu.userId;
        danmaku.isGuest = danmu.type.equals("Like");//isGuest此处用来判断是赞还是评论

        danmaku.padding = DANMU_PADDING;
        danmaku.priority = 1;  // 1:一定会显示, 一般用于本机发送的弹幕,但会导致行数的限制失效
        danmaku.isLive = true;

        /**
         * 弹幕间隔控制
         * 可见弹幕<3
         * 无正在发送的弹幕
         * 则直接发送，否则设置间隔
         */
        if (mDanmakuView.getCurrentVisibleDanmakus().size() < 3 && preDanmuTime < mDanmakuView.getCurrentTime()) {
            danmaku.time = mDanmakuView.getCurrentTime();
        } else {
            danmaku.time = preDanmuTime + ADD_DANMU_TIME;
        }
        //记录当前时间间隔，下一条需要间隔的弹幕以此为基础
        preDanmuTime = danmaku.time;

        danmaku.textSize = DANMU_TEXT_SIZE/* * (mDanmakuContext.getDisplayer().getDensity() - 0.6f)*/;
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低


        final SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>(100, 100) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                ImageSpan imageSpan = new VerticalImageSpan(mContext, resizeBitmap(resource, AVATAR_SIZE, AVATAR_SIZE));
                String avatar = "avatar";
                SpannableStringBuilder spannableString = new SpannableStringBuilder(avatar);
                spannableString.setSpan(imageSpan, 0, avatar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.append(" ");
                spannableString.append(danmu.content);
                spannableString.append(" ");

                danmaku.text = spannableString;
                mDanmakuView.addDanmaku(danmaku);
            }
        };

        Glide.with(mContext)
                .load(danmu.imgUrl)
                .asBitmap()
                .transform(new CropCircleTransformation(mContext))
                .into(simpleTarget);

    }

    /**
     * 图片缩放
     *
     * @param targetWidget 目标尺寸
     * @param targetHeight 目标尺寸
     */
    private static Bitmap resizeBitmap(Bitmap bitmap, float targetWidget, float targetHeight) {
        Matrix matrix = new Matrix();

        float scaleWidth = targetWidget / bitmap.getWidth();
        float scaleHeight = targetHeight / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * span图片垂直居中
     */
    public class VerticalImageSpan extends ImageSpan {

        @SuppressWarnings("unused")
        public VerticalImageSpan(Context arg0, int arg1) {
            super(arg0, arg1);
        }

        public VerticalImageSpan(Context context, Bitmap b) {
            super(context, b);
        }

        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();
            int transY;
            transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }
}
