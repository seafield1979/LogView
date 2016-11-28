package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import java.util.List;

/**
 * LogView本体のWindow
 */

public class LogViewWindow extends UWindow {
    /**
     * Constants
     */
    public static final String TAG = "LogViewWindow";
    private static final int DRAW_PRIORITY = 100;

    private static final int BG_COLOR = Color.BLACK;

    // long型のTimeを各単位の秒に変換するための定数
    // 以下の定数でlong型のTimeを割り算すると指定の単位の時間が取得できる
    public static final long PIXCEL_PER_NANO_SEC = 1;
    public static final long PIXCEL_PER_MICRO_SEC = 1000;
    public static final long PIXCEL_PER_MILL_SEC = 1000000;
    public static final long PIXCEL_PER_SEC = 1000000000;

    private static final long DEFAULT_PIXCEL_PER_TIME = PIXCEL_PER_MILL_SEC;

    // 再描画のインターバル ms
    private static final int REDRAW_INTERVAL = 1000;

    /**
     * Member Variables
     */

    private LogBuffer mLogBuf;
    private Context mContext;
    private View mParentView;

    /*
     1ピクセルに相当する時間(nano sec)
     例: pixcelPerTime = 1 なら 1ピクセルで1ナノ秒(ns)(0.000000001秒)
         pixcelPerTime = 1000 なら 1ピクセルで1マイクロ秒(us)(0.000001秒)
         pixcelPerTime = 1000000 なら 1ピクセルで1ミリ秒(ms)(0.001秒)
         pixcelPerTime = 1000000000 なら 1ピクセルで1秒
      */
    private PixelPerTime pixelPerTime = PixelPerTime.Micro1000;

    // ログエリアの最初の時間（一番最初のログの時間）
    private long startTime;

    // ログエリアの最後の時間（一番最後のログの時間）
    private long endTime;

    // コンテンツエリアのサイズ(ピクセル数)
    private long contentLen;

    // 表示エリアの先頭位置の時間
    private long topPosTime;

    // 表示エリアの終端位置の時間
//    private long endPosTime;

    // 表示領域1ページ分の時間
    private long pageTime;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */

    public LogViewWindow(Context context, View parentView, LogBuffer logBuf,
                         float x, float y,
                         int width, int height, int color) {
        super(null, DRAW_PRIORITY, x, y, width, height, color);

        mParentView = parentView;
        mContext = context;
        mLogBuf = logBuf;
        refreshLogs();
    }

    /**
     * インスタンスを生成する
     *
     * @param context
     * @param parentView
     * @param width
     * @param height
     * @return
     */
    public static LogViewWindow createInstance(Context context, View parentView,
                                               LogBuffer logBuf,
                                               float x, float y, int width, int height) {
        LogViewWindow instance = new LogViewWindow(context, parentView, logBuf,
                x, y, width, height, Color.argb(128, 0, 0, 0));

        return instance;
    }

    /**
     * Methods
     */

    /**
     * Public
     */
    public void clear() {
        mLogBuf.clearLog();
        refreshLogs();
    }

    /**
     * LogViewの表示を更新する
     */
    private void refreshLogs() {
        // ログの表示エリアを計算する

        // 最初と最後のログに囲まれた時間内のログを表示する
        startTime = topPosTime = RealmManager.getLogViewDao().selectMinLogTime();
        endTime =  RealmManager.getLogViewDao().selectMaxLogTime();

        if (startTime == 0) return;

        // ScrollViewにサイズを設定する
        contentLen = endTime - startTime;
        pageTime = getHeight() * pixelPerTime.getDivValue();
        contentSize.height = contentLen;

        mScrollBarV.setPageLen(pageTime);
        mScrollBarV.updateContent(contentSize);

        if (true) {
            ULog.print(TAG, "startTime:" + LogBuffer.longToDouble(startTime));
            ULog.print(TAG, "endTime:" + LogBuffer.longToDouble(endTime));
            ULog.print(TAG, "contentLen:" + contentLen);
            ULog.print(TAG, "pageTime:" + pageTime);
        }
    }

    /**
     * Private
     */

    /**
     * UDrawable
     */
    public void drawContent(Canvas canvas, Paint paint) {
        if (!isShow) return;

        // BG
        UDraw.drawRectFill(canvas, paint, rect, BG_COLOR);

        if (startTime == 0) {
            return;
        }

        // 表示領域に含まれるログを取得
        List<LogBase> logs = RealmManager.getLogViewDao()
                .selectByAreaTime(topPosTime, topPosTime + pageTime);

        // BGのラインを描画
        float x = 150, y, topY = 50, endY = (float)getHeight();

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);

        canvas.drawLine( x, topY, x, endY, paint);

        topPosTime = startTime + (long)mScrollBarV.getTopPos();

        // タイムバー
        // 最初のメモリの時間を計算する
        long p2t = pixelPerTime.getDivValue();
        TimeUnit timeUnit = pixelPerTime.getTimeUnit();
        long memNext = (topPosTime + p2t * 100) / (p2t * 100);
        long memTime = memNext * p2t * 100;

        while(memTime < topPosTime + pageTime) {
            y = topY + (memTime - topPosTime) / p2t;
            canvas.drawLine(x -20, y , x, y, paint);

            // テキスト
            String value = String.format("%d", memTime / timeUnit.divValue());
            String text = "" + value + " " + timeUnit.unitStr();

            UDraw.drawTextOneLine(canvas, paint, text, UDraw.UAlignment.None, 30, x-150, y,
                    Color
                    .WHITE);

            memTime += p2t * 100;
        }


        // ログを表示
        for (LogBase log : logs) {
            // 表示座標を求める
            y = topY + (log.getTime() - topPosTime) / p2t;

            UDraw.drawCircleFill(canvas, paint, new PointF(x + 200, y), 30, Color.RED);
        }

    }


    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    public boolean doAction() {
        // 自動移動
        if (isMoving) {
            if (autoMoving()) {
                return true;
            }
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        if (super.touchEvent(vt)) {
            return true;
        }

        // 範囲外なら除外
        if (!(rect.contains((int)vt.getX(), (int)vt.getY()))) {
            return false;
        }

        switch (vt.type) {
            case Click:
                break;
            case Moving:
                break;
        }

        return true;
    }

    /**
     * UScrollbar
     */
    public void UScrollBarScrolled(UScrollBar scrollbar) {
        topPosTime = startTime + (long)scrollbar.getTopPos();
    }
}