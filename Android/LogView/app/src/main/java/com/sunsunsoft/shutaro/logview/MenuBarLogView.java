package com.sunsunsoft.shutaro.logview;

/**
 * Created by shutaro on 2016/12/18.
 *
 * LogView画面に表示するメニューバー
 */

import android.view.View;

/**
 * メニューバー
 * メニューに表示する項目を管理する
 */
public class MenuBarLogView extends UMenuBar {

    /**
     * Enums
     */
    enum MenuItemType {
        Top,
        Child,
        State
    }
    // メニューのID、画像ID、Topかどうかのフラグ
    enum MenuItemId {
        Play_Stop(R.drawable.play, MenuItemType.Top),
        Stop(R.drawable.stop, MenuItemType.State),

        LogTop(R.drawable.add, MenuItemType.Top),
        AddLogPoint(R.drawable.number_1, MenuItemType.Child),
        AddLogText(R.drawable.number_2, MenuItemType.Child),
        AddLogArea(R.drawable.number_3, MenuItemType.Child),
        ClearLogs(R.drawable.trash, MenuItemType.Child),

        ZoomTop(R.drawable.zoom, MenuItemType.Top),
        ZoomIn(R.drawable.zoom_in, MenuItemType.Child),
        ZoomOut(R.drawable.zoom_out, MenuItemType.Child),
        ZoomLane(R.drawable.resize_expand, MenuItemType.Child),
        ZoomOutLane(R.drawable.resize_shrink, MenuItemType.Child),

        MoveTop(R.drawable.sort_arrows, MenuItemType.Top),
        Next(R.drawable.skip_down, MenuItemType.Child),
        Prev(R.drawable.skip_up, MenuItemType.Child),

        Settings(R.drawable.settings_1, MenuItemType.Top),
        ;

        private MenuItemType type;
        private int imageId;

        MenuItemId(int imageId, MenuItemType type) {
            this.imageId = imageId;
            this.type = type;
        }

        public int getImageId() {
            return imageId;
        }
        public MenuItemType getType() { return type; }

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
        UMenuItem item = null;
        UMenuItem itemTop = null;

        // add menu items
        for (MenuItemId itemId : MenuItemId.values()) {
            switch(itemId.getType()) {
                case Top:
                    item = itemTop = addTopMenuItem(itemId.ordinal(), itemId.getImageId());
                    break;
                case Child:
                    item = addMenuItem(itemTop, itemId.ordinal(), itemId.getImageId());
                    break;
                case State:
                    item.addState(UResourceManager.getBitmapById(itemId.getImageId()));
                    break;
            }
        }

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
