package com.sunsunsoft.shutaro.logview;

import android.util.Log;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * LogBufferDB用のログ
 * RealmObjectのサブクラスなのでそのままテーブルとして使用される
 */
public class Log2 extends RealmObject implements LogBase{
    public static final String TAG = "Log2";

    @PrimaryKey
    private int id;

    private long time;      // 1 = 1 nano sec
    private long time2;     // 1 = 1 nano sec
    private int type;       // LogType
    private int logId;      // LogId
    private int laneId;     // LaneId
    private String text;

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime2() {
        return time2;
    }

    public void setTime2(long time2) {
        this.time2 = time2;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getLaneId() {
        return laneId;
    }

    public void setLaneId(int laneId) {
        this.laneId = laneId;
    }

    public LogType _getType() {
        return LogType.toEnum(type);
    }
    public LogId _getLogId() {
        return LogId.toEnum(logId);
    }


    /**
     * ログをコンソールに出力する
     */
    public void dispLog() {
        Log.d(TAG, this.toString());
    }

    /**
     * ログをStringに変換する
     */
    public String toString() {
        return "id:" + id + " type:" + type +
                " logId:" + logId +
                " laneId:" + laneId + " " +
                "time:" + LogBuffer.longToDouble(time) +  " " + text;
    }
}
