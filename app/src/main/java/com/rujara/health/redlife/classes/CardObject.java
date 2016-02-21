package com.rujara.health.redlife.classes;

import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by deep.patel on 9/23/15.
 */
public class CardObject {
    private String id;
    private String mText1;
    private String mText2;
    private String mText3;
    private Drawable cardIcon;
    private Drawable status;
    private Map<String, String> data = new HashMap<String, String>();

    public CardObject(String id, String text1, String text2, Drawable cardIcon) {
        this.id = id;
        mText1 = text1;
        mText2 = text2;
        this.cardIcon = cardIcon;
    }

    public CardObject(String id, String text1, String text2, String text3, Drawable cardIcon) {
        this.id = id;
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
        this.cardIcon = cardIcon;
    }

    public Drawable getCardIcon() {
        return cardIcon;
    }

    public void setCardIcon(Drawable cardIcon) {
        this.cardIcon = cardIcon;
    }

    public Drawable getStatusIcon() {
        return status;
    }

    public void setStatusIcon(Drawable status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getmText3() {
        return mText3;
    }

    public void setmText3(String mText3) {
        this.mText3 = mText3;
    }

    public void addToData(String key, String value) {
        if (data == null)
            data = new HashMap<String, String>();
        data.put(key, value);
    }

    public String getFromData(String key) {
        if (data != null)
            return data.get(key);
        return null;
    }


}
