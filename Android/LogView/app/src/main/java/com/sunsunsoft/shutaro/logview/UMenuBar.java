package com.sunsunsoft.shutaro.logview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

// メニューバーのトップ項目
enum TopMenu {
    Play,
    Log,
    Zoom,
    Move,
    Trash,
    Settings,
}

// メニューをタッチした時に返されるID
enum MenuItemId {
    Play_Stop,

    // add log
    LogTop,
    AddLogPoint,
    AddLogText,
    AddLogArea,
    ClearLogs,

    // zoom
    ZoomTop,
    ZoomIn,
    ZoomOut,

    // move
    MoveTop,
    Next,
    Prev,

    // trash
    Trash,

    // settings
    Settings,
}

/**
 * メニューバー
 * メニューに表示する項目を管理する
 */
public class UMenuBar extends UWindow {
    /**
     * Constants
     */
    public static final int DRAW_PRIORITY = 90;
    public static final int MENU_BAR_H = 150;
    private static final int MARGIN_L = 30;
    private static final int MARGIN_LR = 50;
    private static final int MARGIN_TOP = 15;
    public static final int TOP_MENU_MAX = TopMenu.values().length;

    /**
     * Member variables
     */
    private View mParentView;
    private UMenuItemCallbacks mMenuItemCallbacks;
    UMenuItem[] topItems = new UMenuItem[TOP_MENU_MAX];
    UMenuItem[] items = new UMenuItem[MenuItemId.values().length];
    private DrawList mDrawList;
    private boolean isAnimating;

    /**
     * Get/Set
     */
    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    private UMenuBar(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        super(null, DRAW_PRIORITY, 0, parentH - MENU_BAR_H, parentW, MENU_BAR_H, bgColor);
        mParentView = parentView;
        mMenuItemCallbacks = callbackClass;
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
    public static UMenuBar createInstance(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        UMenuBar instance = new UMenuBar(parentView, callbackClass, parentW, parentH, bgColor);
        instance.initMenuBar();
        return instance;
    }

    /**
     * メニューバーを初期化
     */
    private void initMenuBar() {
        UMenuItem parent;

        // Play & Stop
        parent = addTopMenuItem(TopMenu.Play, MenuItemId.Play_Stop, R.drawable.play);

        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.stop);
        parent.addState(bmp);

        // Log
        parent = addTopMenuItem(TopMenu.Log, MenuItemId.LogTop, R.drawable.add);
        addMenuItem(parent, MenuItemId.AddLogPoint, R.drawable.number_1);
        addMenuItem(parent, MenuItemId.AddLogText, R.drawable.number_2);
        addMenuItem(parent, MenuItemId.AddLogArea, R.drawable.number_3);
        addMenuItem(parent, MenuItemId.ClearLogs, R.drawable.number_4);

        // Zoom
        parent = addTopMenuItem(TopMenu.Zoom, MenuItemId.ZoomTop, R.drawable.zoom);
        addMenuItem(parent, MenuItemId.ZoomIn, R.drawable.zoom_in);
        addMenuItem(parent, MenuItemId.ZoomOut, R.drawable.zoom_out);

        // next/prev
        parent = addTopMenuItem(TopMenu.Move, MenuItemId.MoveTop, R.drawable.sort_arrows);
        addMenuItem(parent, MenuItemId.Next, R.drawable.skip_down);
        addMenuItem(parent, MenuItemId.Prev, R.drawable.skip_up);

        // trash
        addTopMenuItem(TopMenu.Trash, MenuItemId.Trash, R.drawable.trash);

        // settings
        addTopMenuItem(TopMenu.Settings, MenuItemId.Settings, R.drawable.settings_1);

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }

    private void updateBGSize() {
        size.width = MARGIN_L + TOP_MENU_MAX * (UMenuItem.ITEM_W + MARGIN_LR);
    }

    /**
     * メニューのトップ項目を追加する
     * @param topId
     * @param menuId
     * @param bmpId
     */
    private UMenuItem addTopMenuItem(TopMenu topId, MenuItemId menuId, int bmpId) {
        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), bmpId);
        UMenuItem item = new UMenuItem(this, menuId, bmp);
        item.setCallbacks(mMenuItemCallbacks);
        item.setShow(true);

        topItems[topId.ordinal()] = item;
        items[menuId.ordinal()] = item;

        // 座標設定
        item.setPos(MARGIN_LR + (UMenuItem.ITEM_W + MARGIN_LR) * topId.ordinal(), MARGIN_TOP);
        return item;
    }

    /**
     * 子メニューを追加する
     * @param parent
     * @param menuId
     * @param bmpId
     * @return
     */
    private UMenuItem addMenuItem(UMenuItem parent, MenuItemId menuId, int bmpId) {
        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), bmpId);
        UMenuItem item = new UMenuItem(this, menuId, bmp);
        item.setCallbacks(mMenuItemCallbacks);
        item.setParentItem(parent);
        // 子要素は初期状態では非表示。オープン時に表示される
        item.setShow(false);

        parent.addItem(item);

        items[menuId.ordinal()] = item;
        return item;
    }

    /**
     * メニューのアクション
     * メニューアイテムを含めて何かしらの処理を行う
     *
     * @return true:処理中 / false:完了
     */
    @Override
    public boolean doAction() {
        if (!isShow) return false;

        boolean allFinished = true;
        for (UMenuItem item : topItems) {
            if (item.doAction()) {
                allFinished = false;
            }
        }

        return !allFinished;
    }

    /**
     * タッチ処理を行う
     * 現状はクリック以外は受け付けない
     * メニューバー以下の項目(メニューの子要素も含めて全て)のクリック判定
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        boolean done = false;
        float touchX = vt.touchX() - pos.x;
        float touchY = vt.touchY() - pos.y;

        // 渡されるクリック座標をメニューバーの座標系に変換
        for (UMenuItem item : topItems) {
            if (item == null) continue;

            if (item.checkTouch(vt, touchX, touchY)) {
                done = true;
                if (item.isOpened()) {
                    // 他に開かれたメニューを閉じる
                    closeAllMenu(item);
                }
                break;
            }
            if (done) break;
        }

        // メニューバーの領域をクリックしていたら、メニュー以外がクリックされるのを防ぐためにtrueを返す
        if (!done) {
            if (0 <= touchX && touchX <= size.width &&
                    0 <= touchY && touchY <= size.height)
            {
                return true;
            }
        }
        return done;
    }


    /**
     * メニューを閉じる
     * @param excludedItem
     */
    private void closeAllMenu(UMenuItem excludedItem) {
        for (UMenuItem item : topItems) {
            if (item == excludedItem) continue;
            item.closeMenu();
        }
    }

    /**
     * メニュー項目の座標をスクリーン座標で取得する
     */
    public PointF getItemPos(MenuItemId itemId) {
        UMenuItem item = items[itemId.ordinal()];
        if (item == null) {
            return new PointF();
        }
        PointF itemPos = item.getPos();
        return new PointF(toScreenX(itemPos.x), toScreenY(itemPos.y));
    }

    /*
        Drawableインターフェースメソッド
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint ) {
        if (!isShow) return;

        // bg
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        paint.setColor(0xff000000);

        canvas.drawRect(pos.x,
                pos.y,
                pos.x + size.width,
                pos.y + size.height,
                paint);

        // トップのアイテムから描画
        for (UMenuItem item : topItems) {
            if (item != null && item.isShow) {
                item.draw(canvas, paint, pos);
            }
        }
        return;
    }

    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    public boolean animate() {
        if (!isAnimating) return false;
        boolean allFinished = true;

        for (UMenuItem item : topItems) {
            if (item.animate()) {
                allFinished = false;
            }
        }
        if (allFinished) {
            isAnimating = false;
        }
        return !allFinished;
    }

    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }

}
