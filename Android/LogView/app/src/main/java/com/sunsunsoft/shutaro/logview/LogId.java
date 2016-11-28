package com.sunsunsoft.shutaro.logview;

import android.graphics.Color;

/**
 * Created by shutaro on 2016/11/27.
 */

public enum LogId {
    Log1,
    Log2,
    Log3,
    Log4,
    Log5,
    Log6
    ;

    private static final int[] colorTable = {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.GRAY,
            Color.LTGRAY
    };

    public int getColor() {
        return colorTable[ordinal()];
    }

    public static LogId toEnum(int value) {
        for (LogId id : values()) {
            if (id.ordinal() == value) {
                return id;
            }
        }
        return Log1;
    }
}
