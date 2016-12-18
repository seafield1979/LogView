package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
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
    private static final int TOP_Y = 50;
    private static final int BG_COLOR = Color.BLACK;

    // log icons
    private static final int LOG_W = 20;


    /**
     * Member Variables
     */

    private LogBuffer mLogBuf;
    private Context mContext;
    private View mParentView;
    private boolean isStop;

    /*
     1ピクセルに相当する時間(nano sec)
     例: pixcelPerTime = 1 なら 1ピクセルで1ナノ秒(ns)(0.000000001秒)
         pixcelPerTime = 1000 なら 1ピクセルで1マイクロ秒(us)(0.000001秒)
         pixcelPerTime = 1000000 なら 1ピクセルで1ミリ秒(ms)(0.001秒)
         pixcelPerTime = 1000000000 なら 1ピクセルで1秒
      */
    private PixelPerTime pixelPerTime = PixelPerTime.Milli50;

    // ログエリアの最初の時間（一番最初のログの時間）
    private long startTime;

    // ログエリアの最後の時間（一番最後のログの時間）
    private long endTime;

    // コンテンツエリアのサイズ(ピクセル数)
    private long contentLen;

    // 表示エリアの先頭位置の時間
    private long topPosTime;

    // 表示領域1ページ分の時間
    private long pageTime;

    // 停止中のログエリアの終端の時間（停止時のカレントタイム）
    private long currentTime;

    // trueなら表示が最新位置に張り付く
    private boolean isFixed;

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (stop) {
            currentTime = System.nanoTime();
        }
    }

    /**
     * Get/Set
     */


    private long getCurrentTime() {
        if (isStop) {
            return currentTime;
        } else {
            return System.nanoTime();
        }
    }

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
        updateView();
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
        updateView();
    }

    public void zoomIn() {
        pixelPerTime.zoomIn();
        updateView();
    }

    public void zoomOut() {
        pixelPerTime.zoomOut();
        updateView();
    }

    public void update() {
        updateView();
    }

    /**
     * LogViewの表示を更新する
     */
    public void updateView() {
        // ログの表示エリアを計算する

        // 最初と最後のログに囲まれた時間内のログを表示する
        startTime = RealmManager.getLogViewDao().selectMinLogTime();
        if (true) {
            endTime = getCurrentTime() - mLogBuf.getStartTime();
        } else {
            endTime = RealmManager.getLogViewDao().selectMaxLogTime();
        }

        // 最後のログのあとに少し余白をも持たせる
        endTime += pixelPerTime.getDivValue() * 100;

        if (startTime == 0) return;

        // ScrollViewにサイズを設定する
        contentLen = endTime - startTime;
        pageTime = (getHeight() - TOP_Y) * pixelPerTime.getDivValue();
        contentSize.height = contentLen;

        mScrollBarV.setPageLen(pageTime);
        mScrollBarV.updateContent(contentSize);
        if (isFixed) {
            topPosTime = mScrollBarV.setBarPos(ScrollBarPos.Bottom) + startTime;
        }

        if (false) {
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
    /**
     * Windowのコンテンツ領域を描画
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint) {
        if (!isShow) return;

        // BG
        UDraw.drawRectFill(canvas, paint, rect, BG_COLOR, 0, 0);

        if (startTime == 0) {
            return;
        }

        // 表示領域に含まれるログを取得
        List<LogBase> logs = RealmManager.getLogViewDao()
                .selectByAreaTime(topPosTime, topPosTime + pageTime + 100);

        // BGのラインを描画
        float x = 150, y = 0, topY = TOP_Y, endY = (float)getHeight();

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);

        canvas.drawLine( x, topY, x, endY, paint);

        topPosTime = startTime + (long)mScrollBarV.getTopPos();

        ULog.print(TAG, "topPosTime:" + LogTime.longToDouble(topPosTime));
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

            UDraw.drawTextOneLine(canvas, paint, text, UAlignment.None,
                    30, x - 150, y + 5,
                    Color.WHITE);

            memTime += p2t * 100;
        }

        // ログを表示
        drawLogs(canvas, paint, logs, x, y, topY, p2t);

        // 固定表示
        if (UDebug.debugLogView) {
            int TEXT_SIZE = 30;
            x = 200;
            y = 30;
            UDraw.drawTextOneLine(canvas, paint, "topPosTime:" + LogTime.longToDouble(topPosTime),
                    UAlignment.None, 30, x, y, Color.WHITE);
            y += TEXT_SIZE + 15;
            UDraw.drawTextOneLine(canvas, paint, "currentTime:" + LogTime.longToDouble
                    (getCurrentTime() - mLogBuf.getStartTime()),
                    UAlignment.None, 30, x, y, Color.WHITE);
            y += TEXT_SIZE + 15;

        }
    }

    private void drawLogs(Canvas canvas, Paint paint,
                          List<LogBase> logs, float x, float y, float topY, long p2t)
    {
        for (LogBase log : logs) {
            // 表示座標を求める
            y = topY + (log.getTime() - topPosTime) / p2t;

            switch (log._getType()) {
                case Point:
                    UDraw.drawCircleFill(canvas, paint, new PointF(x + 200, y),
                            LOG_W, log._getLogId().getColor());
                    break;
                case Text:
                    UDraw.drawCircleFill(canvas, paint, new PointF(x + 200, y),
                            LOG_W, log._getLogId().getColor());
                    UDraw.drawTextOneLine(canvas, paint, log.getText(), UAlignment.None,
                            LOG_W, x + 200 + 35, y + 5, Color.WHITE);
                    break;
                case Area:
//                    UDraw.drawRectFill(canvas, paint, new PointF(x + 200, y),
//                            30, log._getLogId().getColor());
                    break;
            }
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