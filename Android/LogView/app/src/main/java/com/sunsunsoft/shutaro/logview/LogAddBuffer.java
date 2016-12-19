package com.sunsunsoft.shutaro.logview;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/19.
 *
 * ログを追加するためのバッファ
 * DBを使用するLogBuffer等はログ追加処理自体がそれなりに重いため、
 * ログを追加する場合はこのバッファに積んでおいて後でまとめて本体のバッファーに追加する
 *
 * また、エリアログ等いくつかのログがセットになっているものはこのバッファを使ってまとめる
 */


/**
 * 一時的にバッファに追加するログ
 */
class AddLog {
    public LogType type;
    public LogId logId;
    public int laneId;
    public long time;

    // Text用
    public String text;

    // Area用
    public long time2;

    /**
     * Constructor
     */
    public AddLog(LogType type, LogId logId, int laneId, String text, long time, long time2) {
        this.type = type;
        this.logId = logId;
        this.laneId = laneId;
        this.text = text;
        this.time = time;
        this.time2 = time2;
    }
}

/**
 * エリアログの開始情報
 * エリアログの終了と結びついてAddLogのログになる
 */
class AddAreaLog {
    public LogId logId;
    public int laneId;
    public long time;

    /**
     * Constructor
     */
    public AddAreaLog(LogId logId, int laneId, long time) {
        this.logId = logId;
        this.laneId = laneId;
        this.time = time;
    }
}










public class LogAddBuffer {
    /**
     * Constants
     */
    public static final String TAG = "LogAddBuffer";

    /**
     * Member variables
     */
    private LinkedList<AddLog> mLogs = new LinkedList<>();

    private LinkedList<AddAreaLog> mAreaLogs = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor(Singleton)
     */
    private static LogAddBuffer singleton = new LogAddBuffer();

    private LogAddBuffer() {
    }
    public static LogAddBuffer getInstance(){
        return singleton;
    }


    /**
     * Methods
     */
    /**
     * ログを追加する。エリアタイプのログは２つで１セットにして追加する
     * @param type
     * @param logId
     * @param laneId
     * @param text
     * @param time
     */
    public void addLog(LogType type, LogId logId, int laneId,
                       String text, long time)
    {
        // AreaLogならすでにStartタイプのログがあるかをチェック
        if (type == LogType.Area) {
            // エリアログのリストに同じlogId, laneIdのログがあったら結合する
            boolean found = false;
            for (AddAreaLog areaLog : mAreaLogs) {
                if (areaLog.logId == logId && areaLog.laneId == laneId) {
                    // エリアログを追加
                    AddLog log = new AddLog(LogType.Area, logId, laneId, text, areaLog.time,
                            time);
                    mLogs.add(log);

                    ULog.print(TAG, "add log :" + areaLog.time + " " + time);
                    found = true;
                    // 古いStartタイプを削除
                    mAreaLogs.remove(areaLog);
                    break;
                }
            }
            if (!found) {
                // セットになるAreaログが見つからなかったのでStartタイプを追加
                AddAreaLog areaLog = new AddAreaLog(logId, laneId, time);
                mAreaLogs.add(areaLog);
            }
        } else {
            AddLog log = new AddLog(type, logId, laneId, text, time, 0);
            mLogs.add(log);
        }
    }

    /**
     * AddバッファーのログをLogBuffer本体に追加する
     */
    public void addToLogBuffer(LogBuffer logBuf) {
        for (AddLog log : mLogs) {
            logBuf.addLog(log.type, log.logId, log.laneId, log.time, log.time2, log.text);
        }
        mLogs.clear();
    }

}
