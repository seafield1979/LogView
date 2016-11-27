package com.sunsunsoft.shutaro.logview;

import android.util.Log;

/**
 * Created by shutaro on 2016/11/27.
 */

public class Log1 implements LogBase {

    /**
     * Consts
     */
    public static final String TAG = "Log1";

    /**
     * Member Variables
     */
    int id;
    long time;      // 1 = 1 nano sec
    String text;
    LogType type;

    LogId logId;
    LogType logType;
    LogAreaType areaType;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    /**
     * 点
     */
    public Log1 createPoint(int id, LogId logId, long time) {
        Log1 instance = new Log1(id, logId, LogType.Point, null, time, null);
        return instance;
    }

    /**
     * テキスト
     */
    public Log1 createText(int id, LogId logId, long time, String text) {
        Log1 instance = new Log1(id, logId, LogType.Point, null, time, text);
        return instance;
    }

    /**
     * 範囲 (2つのログで１セット）
     */
    public Log1 createArea(int id, LogId logId, LogAreaType areaType, long time) {
        Log1 instance = new Log1(id, logId, LogType.Area, areaType, time, null);
        return instance;
    }

    public Log1(int id, LogId logId, LogType logType, LogAreaType areaType, long time, String
            text)
    {
        this.id = id;
        this.logId = logId;
        this.text = text;
        this.logType = logType;
        this.logId = logId;
        this.areaType = areaType;
    }

    /**
     * Methods
     */

    public void dispLog() {
        Log.d(TAG, this.toString());
    }

    public String toString() {
        return "id:" + id + " type:" + logType +
                " logId:" + logId +
                " areaType:" + areaType + " " +
                "time:" + getFloatTime() +  " " + text;
    }

    public double getFloatTime()
    {
        return (double)time / (double)DIVISOR;
    }
}
