package com.mba.chatapplication;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetFaceId extends AsyncTask<Uri,String,String> {
    String uri;
    public AsyncResponce delegate = null;

    @Override
    protected String doInBackground(Uri... strings) {
        String responseJson = null;
        Uri url = strings[0];



            uri = "https://eastus.api.cognitive.microsoft.com/face/v1.0/detect";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{ \"url\": \" "+ url +" \"}");
        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.get(uri)))
                .method("POST", body)
                .addHeader("Ocp-Apim-Subscription-Key", "YOUR_KEY")
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.code()==200)
            {
                responseJson= Objects.requireNonNull(response.body()).string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseJson;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            delegate.processFinish(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
