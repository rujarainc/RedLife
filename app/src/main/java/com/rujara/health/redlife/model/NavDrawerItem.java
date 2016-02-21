package com.rujara.health.redlife.model;

import android.graphics.drawable.Drawable;

/**
 * Created by deep.patel on 9/18/15.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private Drawable icon;

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public Drawable getIcon() {
        return icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
