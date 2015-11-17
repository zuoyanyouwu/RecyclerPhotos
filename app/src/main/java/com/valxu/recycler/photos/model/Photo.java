package com.valxu.recycler.photos.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Author: xuke
 * Date: 2015-10-28
 */
public class Photo implements Serializable{

    private static final long serialVersionUID = -5523776862484204338L;

    public static final int PHOTO_TYPE_TITLE = 0;
    public static final int PHOTO_TYPE_ITEM = 1;

    public String date;
    public String url;
    public int type;

    public Photo(String date, String url) {
        this.date = date;
        this.url = url;
        this.type = PHOTO_TYPE_ITEM;
    }

    public Photo(String date, String url, int type) {
        this.date = date;
        this.url = url;
        this.type = type;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Photo) {
            Photo photo = (Photo) o;
            if (!TextUtils.isEmpty(this.url) && !TextUtils.isEmpty(photo.url)
                    && this.url.equals(photo.url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "data = " + date + ", url = " + url + ", type = " + type;
    }
}
