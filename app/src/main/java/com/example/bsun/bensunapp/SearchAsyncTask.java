package com.example.bsun.bensunapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequest;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Context;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by bsun on 11/25/16.
 */

public class SearchAsyncTask extends AsyncTask<String, Void, Search> {
    private long mStart;

    public SearchAsyncTask(long start) {
        this.mStart = start;
    }

    @Override
    protected Search doInBackground(String... params) {
        Search result = null;
        if(params != null && params.length > 0) {
            NetHttpTransport transport = new NetHttpTransport();
            Search search = null;

            if(transport != null) {
                String query = params[0];
                Customsearch.Builder builder = new Customsearch.Builder(transport, new JacksonFactory(), null);
                builder.setApplicationName("BenSunApp");
                builder.setCustomsearchRequestInitializer(new CustomsearchRequestInitializer() {
                    @Override
                    protected void initializeCustomsearchRequest(CustomsearchRequest<?> request) throws IOException {
                        request.setKey("AIzaSyBoQIsngiyVmg92Rn8YxifrhIn0zK8eO-E");
                        request.set("cx", "005674502902063507867:ll79ly2mhgg");
                        request.setFields("items/link");
                        request.set("searchType", "image");
                        request.set("num", new Long("10"));
                        request.set("start", new Long("1"));
                    }
                });
                try {
                    result = builder.build().cse().list(query).execute();
                } catch (IOException e) {
                    Log.e("SearchTask", "Custom search failed", e);
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Search result) {
        super.onPostExecute(result);
        if (result.size() > 0) {
            List<Result> listResult = (List) result.getItems();

            Random rand = new Random();
            int  n = rand.nextInt(result.size()) + 1;

            ImageView redditImageView = (ImageView) findViewById(R.id.reddit_image_view);

            Result chosen = listResult.get(n);
            String url = chosen.getLink();
            Glide.with(DisplayRandomPicture.this)
                    .load(url)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), resource, "Title", null);
                            Uri bmpUri = Uri.parse(path);

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/jpeg");

                            startActivity(Intent.createChooser(shareIntent, "2024690072"));

                            return false;
                        }
                    })
                    .into(redditImageView);
        }

        Log.d("This is running afterwards", "Tadaaaaaa");
    }

}
