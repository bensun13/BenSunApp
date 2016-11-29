package com.example.bsun.bensunapp;

import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.services.customsearch.Customsearch;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.model.Search;
import com.google.api.services.customsearch.model.Result;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.util.Log.d;

public class DisplayRandomPicture extends AppCompatActivity {

    private ImageView redditImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_random_picture);

        String cse_id = "005674502902063507867:ll79ly2mhgg";
        String api_key = "AIzaSyBoQIsngiyVmg92Rn8YxifrhIn0zK8eO-E";

        redditImageView = (ImageView) findViewById(R.id.reddit_image_view);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ShowRandomPicture.EXTRA_MESSAGE);
        String source =  intent.getStringExtra(ShowRandomPicture.EXTRA_SOURCE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_random_picture);
        layout.addView(textView);

        Log.d("Searching: ", source);

        if (source.equals("Google")) {
            Search search = null;
            try {
                search = new SearchAsyncTask(1).execute(message).get();
            } catch (InterruptedException e) {
                Log.e("Interrupted Error", "SearchAsyncTask failed", e);
            } catch (ExecutionException e) {
                Log.e("Execution Error", "SearchAsyncTask failed", e);
            }

        } else if (source.equals("Reddit")) {
            Log.d("Inside searching ", "Reddit");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.reddit.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            SubredditFetcher service = retrofit.create(SubredditFetcher.class);
            Call<SubredditWrapper> submissions = service.getSubreddit(message);


            submissions.enqueue(new Callback<SubredditWrapper>() {

                @Override
                public void onResponse(Call<SubredditWrapper> call, Response<SubredditWrapper> response) {
                    SubredditWrapper submissions = response.body();
                    if (submissions.data.children != null && !submissions.data.children.isEmpty()) {

                        Iterator<SubredditWrapper.Children> iterator = submissions.data.children.iterator();
                        while (iterator.hasNext()) {
                            SubredditWrapper.Children next = iterator.next();
                            if (next.data.preview != null && next.data.preview.images != null && !next.data.preview.images.isEmpty()) {
                                String url = next.data.preview.images.get(0).source.url;

                                if (url.indexOf(".jpg") < 0) {
                                    continue;
                                }

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
                                Log.d("Submission ID", next.data.preview.images.get(0).source.url);

                                break;
                            }

                        }

                    }

                }

                @Override
                public void onFailure(Call<SubredditWrapper> call, Throwable t) {
                    d("Failure", "Failed");
                }
            });
        } else {
            Log.d("Not searching", source);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Request Code: ", Integer.toString(requestCode));
        Log.d("Requested Permission: ", permissions.toString());
    }

}
