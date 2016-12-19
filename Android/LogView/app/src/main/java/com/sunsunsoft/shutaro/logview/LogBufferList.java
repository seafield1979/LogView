package com.sunsunsoft.shutaro.logview;

import android.util.Log;

import java.util.LinkedList;

/**
 * LinkedListを使用したLogBuffer
 */

public class LogBufferList extends LogBuffer {

    /**
     * Member Variables
     */
    LinkedList<LogBase> mLogs = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public LogBufferList(int maxSize) {
        super(maxSize);
    }

    /**
     * Methods
     */

    public LogBase addLog(LogType type, LogId id, int laneId, long time, long
            time2,
                          String text) {
        if (mLogs.size() >= maxSize) {
            // バッファが最大値に達していたら古いものから削除
            mLogs.removeFirst();
        }
        Log1 log = new Log1(type, id, topId, text, time - startTime, 0);
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
