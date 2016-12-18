package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/12/18.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

/**
 * メニューバー
 * メニューに表示する項目を管理する
 */
public class MenuBarLogView extends UMenuBar {

    /**
     * Enums
     */
    // メニューのID、画像ID、Topかどうかのフラグ
    enum MenuItemId {
        Play_Stop(R.drawable.play, true),

        LogTop(R.drawable.add, true),
        AddLogPoint(R.drawable.number_1, false),
        AddLogText(R.drawable.number_2, false),
        AddLogArea(R.drawable.number_3, false),
        ClearLogs(R.drawable.trash, false),

        ZoomTop(R.drawable.zoom, true),
        ZoomIn(R.drawable.zoom_in, false),
        ZoomOut(R.drawable.zoom_out, false),

        MoveTop(R.drawable.sort_arrows, true),
        Next(R.drawable.skip_down, false),
        Prev(R.drawable.skip_up, false),

        Settings(R.drawable.settings_1, true),
        ;

        private boolean isTop;
        private int imageId;

        MenuItemId(int imageId, boolean isTop) {
            this.imageId = imageId;
            this.isTop = isTop;
        }

        public int getImageId() {
            return imageId;
        }
        public boolean isTop() {
            return isTop;
        }

        public static MenuItemId toEnum(int value) {
            if (value >= values().length) return Play_Stop;
            return values()[value];
        }
    }

    /**
     * Constructor
     */
    public MenuBarLogView(UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor) {
        super(callbackClass, parentW, parentH, bgColor);
    }

    /**
     * メニューバーを生成する
     * @param parentView
     * @param callbackClass
     * @param parentW     親Viewのwidth
     * @param parentH    親Viewのheight
     * @param bgColor
     * @return
     */

    public static MenuBarLogView createInstance(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        MenuBarLogView instance = new MenuBarLogView( callbackClass, parentW, parentH, bgColor);
        instance.initMenuBar();
        return instance;
    }


    protected void initMenuBar() {
        UMenuItem itemTop = null;

        // add menu items
        for (MenuItemId itemId : MenuItemId.values()) {
            if (itemId.isTop()) {
                // Parent
                itemTop = addTopMenuItem(itemId.ordinal(), itemId.getImageId());
            } else {
                // Child
                addMenuItem(itemTop, itemId.ordinal(), itemId.getImageId());
            }
        }

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
