package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/27.
 *
 * ログ１件分の情報を保持するクラス
 * 特殊な情報を持たせたい場合はこのクラスのサブクラスを使用する
 */
abstract public class LogBase {

    /**
     * Enums
     */

    /**
     * Consts
     */

    // Nano Sec からSecに変換するための割り算の分母
    protected static final int DIVISOR = 1000000000;


    /**
     * Static Variables
     */

    /**
     * Member Variables
     */
    int id;
    long time;      // 1 = 1 nano sec
    String text;
    LogType type;

    /**
     * Get/Set
     */
    public LogType getType() {
        return type;
    }


    /**
     * Constructor
     */
    /**
     * ログを追加
     * @param time  1=1ナノ秒 の時間
     * @param text
     */
    public LogBase(int id, long time, String text) {
        this.id = id;
        this.time = time;
        this.text = text;
    }

    /**
     * Methods
     */

    abstract public void dispLog();
    abstract public String toString();

    protected double getFloatTime() {
        return (double)time / (double)DIVISOR;
    }
}
