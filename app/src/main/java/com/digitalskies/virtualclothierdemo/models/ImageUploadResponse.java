package com.digitalskies.virtualclothierdemo.models;

public class ImageUploadResponse {

    private boolean imageUploaded;

    private String downloadUrl;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isImageUploaded() {
        return imageUploaded;
    }

    public void setImageUploaded(boolean imageUploaded) {
        this.imageUploaded = imageUploaded;
    }


}
