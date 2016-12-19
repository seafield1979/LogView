package com.sunsunsoft.shutaro.logview;

import android.util.Log;

/**
 * Created by shutaro on 2016/11/27.
 *
 * LogBufferList用のログ
 */

public class Log1 implements LogBase {

    /**
     * Consts
     */
    public static final String TAG = "Log1";

    /**
     * Member Variables
     */
    private long time;      // 1 = 1 nano sec
    private long time2;
    private String text;
    private LogType type;

    private LogId logId;
    private LogType logType;
    private int laneId;

    /**
     * Get/Set
     */
    public LogType _getType() {
        return type;
    }
    public LogId _getLogId() {
        return logId;
    }
    public String getText() {
        return text;
    }

    /**
     * Constructor
     */
    /**
     * 点
     */
    public Log1 createPoint(LogId logId, int laneId, long time) {
        Log1 instance = new Log1( LogType.Point, logId, laneId, null, time, 0);
        return instance;
    }

    /**
     * テキスト
     */
    public Log1 createText(LogId logId, int laneId, long time, String text) {
        Log1 instance = new Log1( LogType.Point, logId, laneId, text, time, 0);
        return instance;
    }

    /**
     * 範囲 (2つのログで１セット）
     */
    public Log1 createArea(LogId logId, int laneId, long time, long time2) {
        Log1 instance = new Log1( LogType.Area, logId, laneId, null, time, time2);
        return instance;
    }

    public Log1(LogType logType, LogId logId, int laneId, String
            text, long time, long time2)
    {
        this.logType = logType;
        this.logId = logId;
        this.laneId = laneId;
        this.text = text;
        this.logId = logId;
        this.time = time;
        this.time2 = time2;
    }

    /**
     * Methods
     */

    public void dispLog() {
        Log.d(TAG, this.toString());
    }

    public String toString() {
        return " type:" + logType +
                " logId:" + logId +
                " laneId:" + laneId +
                " time:" + LogBuffer.longToDouble(time) +  " " + text;
    }

    public long getTime()
    {
        return time;
    }
    public long getTime2() { return time2; }

    public int getLaneId() { return laneId; }
}
