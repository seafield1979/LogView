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
    protected static final boolean ClearLogsAfterShow = true;
    protected static final boolean ResetStartTimeAfterShow = true;

    // 指定可能なバッファのサイズの最大値
    protected static final int LOG_MAX = 100000;

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

    abstract public LogBase addLog(LogType type, LogAreaType areaType, LogId id, long time, String
            text);

    /**
     * clear logs
     */
    abstract public void clearLog();

    /**
     * show all logs
     */
    abstract public void showAllLog();
}
