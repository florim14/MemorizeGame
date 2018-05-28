package com.hamiti.florim.memorizegame.utils;

import com.google.gson.annotations.SerializedName;
import com.hamiti.florim.memorizegame.models.FlickrPhotosDetails;

import java.util.List;

/**
 * Created by Florim on 5/27/2018.
 */

public class FlickPhotosDetailsResponse {
    @SerializedName("photo")
    private List<FlickrPhotosDetails> photo;


    public List<FlickrPhotosDetails> getPhoto() {
        return photo;
    }

    public void setPhoto(List<FlickrPhotosDetails> photo) {
        this.photo = photo;
    }

    public List<FlickrPhotosDetails> getPhotos() {
        return photo;
    }

    public void flickPhotosDetails(List<FlickrPhotosDetails> photo) {
        this.photo = photo;
    }
}