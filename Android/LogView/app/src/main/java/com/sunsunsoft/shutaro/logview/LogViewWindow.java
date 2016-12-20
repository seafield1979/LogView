package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * レーンの幅の種類
 */
enum LaneWidth {
    Min(40),
    W2(50),
    W3(60),
    W4(70),
    W5(80),
    W6(100),
    W7(120),
    Max(150)
    ;

    private int width;

    private LaneWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    /**
     * 次の値に進める
     */
    public static LaneWidth expand(LaneWidth now) {
        return toEnum(now.ordinal() + 1);
    }
    public static LaneWidth shrink(LaneWidth now) {
        return toEnum(now.ordinal() - 1);
    }

    public static LaneWidth toEnum(int value) {
        if (value >= values().length) {
            return Max;
        } else if (value < 0) {
            return Min;
        } else {
            return values()[value];
        }
    }
}

/**
 * LogView本体のWindow
 */

public class LogViewWindow extends UScrollWindow {
    /**
     * Constants
     */
    public static final String TAG = "LogViewWindow";
    private static final int DRAW_PRIORITY = 100;
    private static final int TOP_Y = 0;
    private static final int BG_COLOR = Color.BLACK;
    private static final int LINE_COLOR = Color.WHITE;
    private static final int TIME_BAR_W = 200;

    private static final int LANE_TEXT_SIZE = 30;
    private static final int LANE_TOP_H = 70;

    // log icons
    private static final int LOG_W = 20;


    /**
     * Member Variables
     */

    private LogBuffer mLogBuf;
    private Context mContext;
    private View mParentView;
    private boolean isStop;
    private LaneWidth mLaneW = LaneWidth.W5;

    /*
     1ピクセルに相当する時間(nano sec)
     例: pixcelPerTime = 1 なら 1ピクセルで1ナノ秒(ns)(0.000000001秒)
         pixcelPerTime = 1000 なら 1ピクセルで1マイクロ秒(us)(0.000001秒)
         pixcelPerTime = 1000000 なら 1ピクセルで1ミリ秒(ms)(0.001秒)
         pixcelPerTime = 1000000000 なら 1ピクセルで1秒
      */
    private PixelPerTime pixelPerTime = PixelPerTime.Micro5000;

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

    /**
     * Get/Set
     */

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (stop) {
            currentTime = System.nanoTime();
        }
    }

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

//        UWindowCallbacks callbacks, int priority, float x, float y, int width, int
//        height, int color, int topBarH, int frameW, int frameH

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

    public void laneExpand() {
        mLaneW = LaneWidth.expand(mLaneW);
        updateView();
    }

    public void laneShrink() {
        mLaneW = LaneWidth.shrink(mLaneW);
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
        contentLen = (endTime - startTime) ;
        pageTime = (getHeight() - TOP_Y) * pixelPerTime.getDivValue();

        contentSize.height = contentLen;

        mScrollBarV.setPageLen(clientSize.height);
        long p2t = pixelPerTime.getDivValue();
        mScrollBarV.updateContent(contentLen / p2t);
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
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;

        PointF _offset = new PointF(TIME_BAR_W, TOP_Y);
        if (offset != null) {
            _offset.x += offset.x;
            _offset.y += offset.y;
        }

        long p2t = pixelPerTime.getDivValue();

        // BG
        UDraw.drawRectFill(canvas, paint, rect, BG_COLOR, 0, 0);

        if (startTime == 0) {
            return;
        }

        // Lanes
        drawLanes(canvas, paint, _offset);

        // TimeBar
        _offset.y += LANE_TOP_H;
        drawTimeBar(canvas, paint, _offset, p2t);

        // Logs
        drawLogs(canvas, paint, _offset, p2t);

        // Debug log
        if (UDebug.debugLogView) {
            drawDebug(canvas, paint);

        }
    }

    /**
     * タイムバーを表示
     * @param canvas
     * @param paint
     * @param p2t
     */
    private void drawTimeBar(Canvas canvas, Paint paint,
                             PointF offset, long p2t) {
        // タイムバー
        // BGのラインを描画
        float endY = (float)getHeight();

        // Left line
        UDraw.drawLine(canvas, paint, offset.x, offset.y, offset.x, endY, 2, LINE_COLOR);

        topPosTime = startTime + mScrollBarV.getTopPos() * p2t;

        // 最初のメモリの時間を計算する
        TimeUnit timeUnit = pixelPerTime.getTimeUnit();
        long memNext = (topPosTime + p2t * 100) / (p2t * 100);
        long memTime = memNext * p2t * 100;

        float topY = offset.y;
        float y;
        while(memTime < topPosTime + pageTime) {
            y = topY + (memTime - topPosTime) / p2t;
            UDraw.drawLine(canvas, paint, offset.x - 20, y , offset.x, y, 2, LINE_COLOR);

            // テキスト
            String value = String.format("%d", memTime / timeUnit.divValue());
            String text = "" + value + " " + timeUnit.unitStr();

            UDraw.drawTextOneLine(canvas, paint, text, UAlignment.Right_CenterY,
                    30, offset.x - 30, y,
                    Color.WHITE);

            memTime += p2t * 100;
        }
    }

    /**
     * レーンを表示
     * @param canvas
     * @param paint
     * @param offset
     */
    private void drawLanes(Canvas canvas, Paint paint, PointF offset) {
        // トップ領域
        UDraw.drawLine(canvas, paint, offset.x, offset.y + LANE_TOP_H, size.width, offset.y + LANE_TOP_H, 2, LINE_COLOR );

        // Lanes
        float x = offset.x;
        float y = offset.y;
        for (int i=0; i<5; i++) {
            UDraw.drawLine(canvas, paint, x + mLaneW.getWidth(), y,
                    x + mLaneW.getWidth(), y + LANE_TOP_H, 2, LINE_COLOR );

            UDraw.drawTextOneLine(canvas, paint, "" + (i+1), UAlignment.Center.Center,
                    LANE_TEXT_SIZE,
                    x + (mLaneW.getWidth() / 2), y + LANE_TOP_H / 2, LINE_COLOR);
            x += mLaneW.getWidth();
        }
    }

    /**
     * ログを表示
     * @param offset
     * @param p2t
     */
    private void drawLogs(Canvas canvas, Paint paint, PointF offset, long p2t)
    {
        // 表示領域に含まれるログを取得
        List<LogBase> logs = RealmManager.getLogViewDao()
                .selectByAreaTime(topPosTime, topPosTime + pageTime + 100);

//        for (LogBase log : logs) {
//            ULog.print(TAG, " logId:" + log._getLogId() +
//                            " logLane:" + log.getLaneId() +
//                            " logType:" + log._getType() +
//                            " logTime:" + log.getTime());
//        }

        // クリッピング前の状態を保存
        canvas.save();

        // クリッピングを設定
        canvas.clipRect(offset.x, offset.y, getWidth(), getHeight());

        float topY = offset.y;
        float x;
        int laneW = mLaneW.getWidth();

        for (LogBase log : logs) {
            // 表示座標を求める
            x = offset.x + log.getLaneId() * laneW;
            switch (log._getType()) {
                case Point:
                    offset.y = topY + (log.getTime() - topPosTime) / p2t;
                    x += laneW / 2;
                    UDraw.drawCircle(canvas, paint, new PointF(x, offset.y),
                            LOG_W, 5, log._getLogId().getColor());
                    break;
                case Text:
                    offset.y = topY + (log.getTime() - topPosTime) / p2t;
                    x += laneW / 2;
                    UDraw.drawCircleFill(canvas, paint, new PointF(x, offset.y),
                            LOG_W, log._getLogId().getColor());
                    UDraw.drawTextOneLine(canvas, paint, log.getText(), UAlignment.None,
                            LOG_W, x + 35, offset.y + 5, Color.WHITE);
                    break;
                case Area:
                    int y1 = (int)(topY + (log.getTime() - topPosTime) / p2t);
                    int y2 = (int)(topY + (log.getTime2() - topPosTime) / p2t);
                    UDraw.drawRectFill(canvas, paint,
                            new Rect((int)x, y1, (int)x + laneW, y2),
                            log._getLogId().getColor(), 2, Color.WHITE);

                    break;
            }
        }

        // クリッピングを解除
        canvas.restore();
    }

    /**
     * 固定のデバッグログを表示
     */
    private void drawDebug(Canvas canvas, Paint paint) {
        int TEXT_SIZE = 30;
        float x = 200;
        float y = 30;
        UDraw.drawTextOneLine(canvas, paint, "topPosTime:" + LogTime.longToDouble(topPosTime),
                UAlignment.None, 30, x, y, Color.WHITE);
        y += TEXT_SIZE + 15;
        UDraw.drawTextOneLine(canvas, paint, "currentTime:" + LogTime.longToDouble
                        (getCurrentTime() - mLogBuf.getStartTime()),
                UAlignment.None, 30, x, y, Color.WHITE);
        y += TEXT_SIZE + 15;

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
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;

        if (super.touchEvent(vt, offset)) {
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