package com.rujara.health.redlife.classes;

import android.graphics.drawable.Drawable;

/**
 * Created by deep.patel on 9/23/15.
 */
public class RequestObject {
    private String mText1;
    private String mText2;
    private Drawable bloodGroupIcon;

    public RequestObject(String text1, String text2, Drawable bloodGroupIcon) {
        mText1 = text1;
        mText2 = text2;
        this.bloodGroupIcon = bloodGroupIcon;
    }

    public Drawable getBloodGroupIcon() {
        return bloodGroupIcon;
    }

    public void setBloodGroupIcon(Drawable bloodGroupIcon) {
        this.bloodGroupIcon = bloodGroupIcon;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

}
