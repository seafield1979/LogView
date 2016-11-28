package com.sunsunsoft.shutaro.logview;

/**
 * Long型の時間を保持するクラス
 */

public class LogTime {

    public static final long NANO_TO_SEC = 1000000000;

    private long time;


    /**
     * Constructor
     */
    public LogTime(long time) {
        this.time = time;
    }

    /**
     * Get/Set
     */
    public long longValue() {
        return time;
    }

    public Double doubleValue() {
        return (double)time / (double)NANO_TO_SEC;
    }
}
