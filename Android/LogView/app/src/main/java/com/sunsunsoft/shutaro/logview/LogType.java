package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/11/27.
 */

public enum LogType {
    Point,
    Text,
    Area,
    ;

    public static LogType toEnum(int value) {
        for (LogType id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return Point;
    }
}

enum LogAreaType {
    Start,
    End
    ;
    public static LogAreaType toEnum(int value) {
        for (LogAreaType id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return Start;
    }
}