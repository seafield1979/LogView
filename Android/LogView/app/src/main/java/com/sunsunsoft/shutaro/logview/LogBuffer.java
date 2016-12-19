package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/27.
 */

/**
 * Created by shutaro on 2016/11/27.
 * ログ用のスタック
 * スタックに積んでおいて後でまとめて出力する
 */


abstract public class LogBuffer {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "LogStack";

    // switchs
    protected static final boolean ClearLogsAfterShow = false;
    protected static final boolean ResetStartTimeAfterShow = false;

    // 指定可能なバッファのサイズの最大値
    protected static final int LOG_MAX = 100000;
    public static final int LANE_MAX = 20;

    /**
     * Static Variables
     */
    static int topId;

    /**
     * Member Variables
     */
    protected long startTime;
    protected int maxSize;

    /**
     * Get/Set
     */
    public long getStartTime(){
        return startTime;
    }

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
    public LogBase addPointLog(LogId id, int laneId, long time) {
        return this.addLog(LogType.Point, id, laneId, time, 0, null);
    }

    public LogBase addTextLog(LogId id, int laneId, long time, String text) {
        return this.addLog(LogType.Text, id, laneId, time, 0, text);
    }

    public LogBase addAreaLog(LogId id, int laneId, long startTime, long endTime) {
        return this.addLog(LogType.Area, id, laneId, startTime, endTime, null);
    }

    abstract public LogBase addLog(LogType type, LogId id, int laneId,
                                   long startTime, long endTime, String text);

    /**
     * clear logs
     */
    abstract public void clearLog();

    /**
     * show all logs
     */
    abstract public void showAllLog();

    /**
     * long型の時間(nano sec)をDouble型の時間(sec)に変換する
     */
    public static double longToDouble(long time) {
        return (double)time / (double)LogBase.DIVISOR;
    }

}
