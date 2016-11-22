package com.example.bsun.bensunapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by bsun on 11/22/16.
 */

public interface SubredditFetcher {
    //gonewild.json
    @GET("/r/{subreddit}/.json")
    Call<SubredditWrapper> getSubreddit(@Path("subreddit") String subreddit);
}
