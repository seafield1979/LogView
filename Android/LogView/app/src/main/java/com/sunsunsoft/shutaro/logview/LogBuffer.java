package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/27.
 */

import android.util.Log;
import java.util.LinkedList;

/**
 * Created by shutaro on 2016/11/27.
 * ログ用のスタック
 * スタックに積んでおいて後でまとめて出力する
 */


public class LogBuffer {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "LogStack";

    // switchs
    private static final boolean ClearLogsAfterShow = true;
    private static final boolean ResetStartTimeAfterShow = true;

    // 指定可能なバッファのサイズの最大値
    private static final int LOG_MAX = 100000;

    /**
     * Static Variables
     */
    static int topId;

    /**
     * Member Variables
     */
    LinkedList<LogBase> mLogs = new LinkedList<>();
    private long startTime;
    private int maxSize;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public LogBuffer(int maxSize) {
        if (maxSize > LOG_MAX) {
            maxSize = LOG_MAX;
        }
        this.maxSize = maxSize;
        startTime = System.nanoTime();
    }

    /**
     * Methods
     */
    public LogBase addPointLog(LogId id, long time) {
        return this.addLog(LogType.Point, null, id, time, null);
    }

    public LogBase addTextLog(LogId id, long time, String text) {
        return this.addLog(LogType.Point, null, id, time, text);
    }

    public LogBase addAreaLog(LogId id, LogAreaType areaType, long time) {
        return this.addLog(LogType.Point, areaType, id, time, null);
    }

    public LogBase addLog(LogType type, LogAreaType areaType, LogId id, long time, String text) {
        if (mLogs.size() >= maxSize) {
            // バッファが最大値に達していたら古いものから削除
            mLogs.removeFirst();
        }
        Log1 log = new Log1(topId, id, type, areaType, time - startTime, text);
        mLogs.add(log);

        topId++;

        return log;
    }

    /**
     * clear logs
     */
    public void clearLog() {
        mLogs.clear();
    }

    /**
     * show all logs
     */
    public void showAllLog() {
        Log.d(TAG, "+ showAllLog +");

        for (LogBase log : mLogs) {
            Log.d(TAG, log.toString());
        }
        if (ClearLogsAfterShow) {
            mLogs.clear();
            topId = 0;
        }
        if (ResetStartTimeAfterShow) {
            startTime = System.nanoTime();
        }
    }
}
