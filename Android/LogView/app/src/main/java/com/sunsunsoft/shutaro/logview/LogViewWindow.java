package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * LogView本体のWindow
 */

public class LogViewWindow extends UWindow {
    /**
     * Constants
     */
    public static final int DRAW_PRIORITY = 100;

    public static final int BG_COLOR = Color.BLACK;

    public LogViewWindow(float x, float y, int width, int height, int color) {
        super(null, DRAW_PRIORITY, x, y, width, height, color);
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
                                               float x, float y, int width, int height) {
        LogViewWindow instance = new LogViewWindow(x, y, width, height, Color.argb(128, 0, 0, 0));

        return instance;
    }


    /**
     * UDrawable
     */
    public void drawContent(Canvas canvas, Paint paint) {
        if (!isShow) return;

        // BG
        UDraw.drawRectFill(canvas, paint, rect, BG_COLOR);
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
}