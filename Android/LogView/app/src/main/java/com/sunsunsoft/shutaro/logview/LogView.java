package com.sunsunsoft.shutaro.logview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * LogView本体
 */

public class LogView extends View implements OnTouchListener, ViewTouchCallbacks, UMenuItemCallbacks
{
    enum WindowType {
        LogView,
        MenuBar,
        Log
    }

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private Context mContext;
    private Paint paint = new Paint();

    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];

    private LogViewWindow mLogViewWin;

    // MessageWindow
    private ULogWindow mLogWin;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;


    // get/set
    public LogView(Context context) {
        this(context, null);
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mContext = context;
    }


    /**
     * 画面に表示するWindowを生成する
     * @param width
     * @param height
     */
    private void initWindows(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // LogViewWindow
        if (mLogViewWin == null) {
            mLogViewWin = LogViewWindow.createInstance(getContext(), this,
                    0, 0, getWidth(), getHeight());
        }

        // UMenuBar
        if (mMenuBar == null) {
            mMenuBar = UMenuBar.createInstance(this, this, width, height,
                    Color.BLACK);
            mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(getContext(), this, LogWindowType.AutoDisappear,
                    0, 0, width / 2, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initWindows(getWidth(), getHeight());
        }
        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // Windowの処理
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win == null) continue;
            if (win.doAction()) {
                invalidate();
            }
        }

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (UDrawManager.getInstance().draw(canvas, paint)){
            invalidate();
        }
    }

    /**
     * タッチイベント処理
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;

        vt.checkTouchType(e);
        // 描画オブジェクトのタッチ処理はすべてUDrawManagerにまかせる
        if (UDrawManager.getInstance().touchEvent(vt)) {
            invalidate();
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                break;
            default:
        }

        // コールバック
        return ret;
    }


    /**
     * Callbacks
     */
    /**
     * UMenuItemCallbacks
     */
    public void menuItemClicked(MenuItemId id)
    {
        switch (id) {
            case Play:
                break;
            case ZoomIn:
                break;
            case ZoomOut:
                break;
        }
    }

    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {

    }
}
