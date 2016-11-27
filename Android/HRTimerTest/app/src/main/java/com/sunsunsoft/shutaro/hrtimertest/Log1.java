package com.sunsunsoft.shutaro.hrtimertest;

/**
 * Created by shutaro on 2016/11/27.
 */

public class Log1 extends LogBase {

    public Log1(int id, long time, String text) {
        super(id, time, text);
    }

    public String toString() {
        return "id:" + id + " time:" + getFloatTime() + " " + text;
    }
}
