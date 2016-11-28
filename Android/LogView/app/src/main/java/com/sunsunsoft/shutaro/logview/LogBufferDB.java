package com.sunsunsoft.shutaro.logview;

import java.util.List;

/**
 * データベースを使用したLogBuffer
 *
 * データベースは１つだけなのでシングルトンで実装する
 */

public class LogBufferDB extends LogBuffer {

    private static final int BUFFER_MAX = 100;

    /**
     * Static Variables
     */
    private static LogBufferDB singleton = new LogBufferDB(BUFFER_MAX);

    /**
     * Constructor
     */

    private LogBufferDB(int maxSize) {
        super(maxSize);

        topId = RealmManager.getLogViewDao().getNextId();
    }

    public static LogBufferDB getInstance(){
        return singleton;
    }

    public LogBase addLog(LogType type, LogAreaType areaType, LogId id, long time, String
            text)
    {
        Log2 log = new Log2();
        log.setId(topId);
        log.setType(type.ordinal());
        if (areaType != null) {
            log.setAreaType(areaType.ordinal());
        }
        log.setLogId(id.ordinal());
        log.setTime(time - startTime);
        if (text != null) {
            log.setText(text);
        }

        RealmManager.getLogViewDao().addOne(log);

        topId++;

        return log;
    }

    /**
     * 使用前に１回コールする。通常はRealmManagerから呼ばれるので自前で呼ぶ必要はない
     */
    public void init() {

    }

    /**
     * clear logs
     */
    public void clearLog() {
        RealmManager.getLogViewDao().clear();
        topId = 0;
        startTime = System.nanoTime();
    }

    /**
     * show all logs
     */
    public void showAllLog() {
        ULog.print(TAG, "+ showAllLog +");

        List<LogBase> logs = RealmManager.getLogViewDao().selectAll();
        for (LogBase log : logs) {
            log.dispLog();
        }

        if (ClearLogsAfterShow) {
            RealmManager.getLogViewDao().clear();
            topId = 0;
        }
        if (ResetStartTimeAfterShow) {
            startTime = System.nanoTime();
        }
    }

    public List<LogBase> getAllLogs() {
        List<LogBase> logs = RealmManager.getLogViewDao().selectAll();

        return logs;
    }
}
