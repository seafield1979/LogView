package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/27.
 *
 * ログ１件分の情報を保持するクラス
 * 特殊な情報を持たせたい場合はこのクラスのサブクラスを使用する
 */
interface LogBase {

    /**
     * Enums
     */

    /**
     * Consts
     */

    // Nano Sec からSecに変換するための割り算の分母
    int DIVISOR = 1000000000;


    /**
     * Static Variables
     */


    /**
     * Get/Set
     */

    /**
     * Constructor
     */

    /**
     * Methods
     */

    /**
     * ログをコンソールに出力する
     */
    void dispLog();

    /**
     * ログをStringに変換する
     */
    String toString();

    /**
     * 浮動小数点型の時間を取得する
     * @return
     */
    double getFloatTime();
}
