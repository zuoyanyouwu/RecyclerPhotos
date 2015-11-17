package com.valxu.recycler.photos;

import com.valxu.recycler.photos.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: xuke
 * Date: 2015-10-28
 */
public class PhotoData {

    static final String BASE = "http://i.imgur.com/";
    static final String EXT = ".jpg";

    private static final String[] URLS = {
            BASE + "CqmBjo5" + EXT, BASE + "zkaAooq" + EXT, BASE + "0gqnEaY" + EXT,
            BASE + "9gbQ7YR" + EXT, BASE + "aFhEEby" + EXT, BASE + "0E2tgV7" + EXT,
            BASE + "P5JLfjk" + EXT, BASE + "nz67a4F" + EXT, BASE + "dFH34N5" + EXT,
            BASE + "FI49ftb" + EXT, BASE + "DvpvklR" + EXT, BASE + "DNKnbG8" + EXT,
            BASE + "yAdbrLp" + EXT, BASE + "55w5Km7" + EXT, BASE + "NIwNTMR" + EXT,
            BASE + "DAl0KB8" + EXT, BASE + "xZLIYFV" + EXT, BASE + "HvTyeh3" + EXT,
            BASE + "Ig9oHCM" + EXT, BASE + "7GUv9qa" + EXT, BASE + "i5vXmXp" + EXT,
            BASE + "glyvuXg" + EXT, BASE + "u6JF6JZ" + EXT, BASE + "ExwR7ap" + EXT,
            BASE + "Q54zMKT" + EXT, BASE + "9t6hLbm" + EXT, BASE + "F8n3Ic6" + EXT,
            BASE + "P5ZRSvT" + EXT, BASE + "jbemFzr" + EXT, BASE + "8B7haIK" + EXT,
            BASE + "aSeTYQr" + EXT, BASE + "OKvWoTh" + EXT, BASE + "zD3gT4Z" + EXT,
            BASE + "z77CaIt" + EXT
    };

    private static List<Photo> createPhotosWithGroup(String groupName, int start, int end) {
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo(groupName, "", Photo.PHOTO_TYPE_TITLE));
        for (int i = start; i < end; i++) {
            photos.add(new Photo(groupName, URLS[i]));
        }

        return photos;
    }

    public static List<Photo> mPhotoGroups = new ArrayList<>();

    static {
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-25", 0, 5));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-26", 5, 11));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-27", 11, 22));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-28", 22, 34));
    }

    public static void resetPhotoGroups() {
        mPhotoGroups.clear();
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-25", 0, 5));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-26", 5, 11));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-27", 11, 22));
        mPhotoGroups.addAll(createPhotosWithGroup("2015-10-28", 22, 34));
    }

    private PhotoData() {
        // No instances.
    }

}
