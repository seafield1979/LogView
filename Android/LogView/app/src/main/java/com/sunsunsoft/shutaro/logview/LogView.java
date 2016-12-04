package com.sunsunsoft.shutaro.logview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * Constants
     */
    public static final String TAG = "LogView";
    private final int FIRST_PERIOD = 1000;
    private final int INTERVAL_PERIOD = 1000;

    /**
     * Member variables
     */
    private LogBufferDB logBuf = LogBufferDB.getInstance();

    // クリック判定の仕組み
    private ViewTouch vt = new ViewTouch(this);

    private Context mContext;
    private Paint paint = new Paint();
    private Timer timer;

    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];

    private LogViewWindow mLogViewWin;

    // MessageWindow
    private ULogWindow mLogWin;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;

    private boolean logTypeSwitch = false;

    /**
     * Get/Set
     */
    public LogBufferDB getLogBuf() {
        return logBuf;
    }

    /**
     * Constructor
     */
    public LogView(Context context) {
        this(context, null);
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mContext = context;

        startTimer();
    }

    /**
     * Methods
     */

    /**
     * Public
     */
    public void updateView() {
        mLogViewWin.updateView();
        invalidate();
    }

    public void clear() {
        mLogViewWin.clear();
        invalidate();
    }

    /**
     * Private
     */
    /**
     * 画面に表示するWindowを生成する
     * @param width
     * @param height
     */
    private void initWindows(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // UMenuBar
        if (mMenuBar == null) {
            mMenuBar = UMenuBar.createInstance(this, this, width, height,
                    Color.BLACK);
            mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // LogViewWindow
        if (mLogViewWin == null) {
            mLogViewWin = LogViewWindow.createInstance(getContext(), this, logBuf,
                    0, 0, getWidth(), getHeight() - mMenuBar.getHeight());
            mWindows[WindowType.LogView.ordinal()] = mLogViewWin;
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

        mLogViewWin.update();

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
     * View更新用のタイマーを起動する
     */
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        // TimerオブジェクトのscheduleAtFixedRateにTimerTaskオブジェクトを渡す
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        }, FIRST_PERIOD, INTERVAL_PERIOD);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Callbacks
     */
    /**
     * UMenuItemCallbacks
     */
    public void menuItemClicked(MenuItemId id, int stateId)
    {
        switch (id) {
            case Play_Stop:
                if (stateId == 0) {
                    startTimer();
                } else {
                    stopTimer();
                }
                break;
            case AddLogPoint:
                getLogBuf().addPointLog(LogId.Log1, System.nanoTime());
                updateView();
                invalidate();
                break;
            case AddLogText:
                getLogBuf().addTextLog(LogId.Log2, System.nanoTime(), "hoge");
                updateView();
                invalidate();
                break;
            case AddLogArea: {
                LogAreaType areaType = LogAreaType.Start;
                if (!logTypeSwitch) {
                    areaType = LogAreaType.End;
                }
                logTypeSwitch = !logTypeSwitch;

                getLogBuf().addAreaLog(LogId.Log3, areaType, System.nanoTime());

                updateView();
                invalidate();
            }
                break;
            case ClearLogs:
                clear();
                invalidate();
                break;
            case ZoomIn:
                mLogViewWin.zoomIn();
                invalidate();
                break;
            case ZoomOut:
                mLogViewWin.zoomOut();
                invalidate();
                break;
        }
    }

    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {

    }
}
