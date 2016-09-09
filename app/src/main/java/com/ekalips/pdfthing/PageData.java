package com.ekalips.pdfthing;

import android.net.Uri;

import java.net.URI;

/**
 * Created by ekalips on 9/9/16.
 */

class PageData {
    private Uri photoUri; private String comment;

    public PageData(Uri photoUri, String comment) {
        this.photoUri = photoUri;
        this.comment = comment;
    }

    public PageData() {
        photoUri = null;
        comment = "";
    }

    Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
