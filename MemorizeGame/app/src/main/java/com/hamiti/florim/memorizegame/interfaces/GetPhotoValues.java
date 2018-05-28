package com.hamiti.florim.memorizegame.interfaces;

import com.hamiti.florim.memorizegame.utils.FlickPhotosDetailsResponse;
import com.hamiti.florim.memorizegame.utils.FlickPhotosDetailsResponseFirst;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Florim on 5/27/2018.
 */

public interface GetPhotoValues {
    public static final String BASE_URL = "https://api.flickr.com/services/rest/";

    @GET
    Call<FlickPhotosDetailsResponseFirst> getFlickPhotosDetails(
            @Url String url
    );
}
