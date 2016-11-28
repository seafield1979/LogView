package com.sunsunsoft.shutaro.logview;

import android.util.Log;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 単語カード
 * RealmObjectのサブクラスなのでそのままテーブルとして使用される
 */
public class Log2 extends RealmObject implements LogBase{
    public static final String TAG = "Log2";

    @PrimaryKey
    private int id;

    private long time;      // 1 = 1 nano sec
    private int type;       // LogType
    private int logId;      // LogId
    private int logType;    // LogType
    private int areaType;   // LogAreaType
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

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
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
        return "id:" + id + " type:" + logType +
                " logId:" + logId +
                " areaType:" + areaType + " " +
                "time:" + LogBuffer.longToDouble(time) +  " " + text;
    }

}
