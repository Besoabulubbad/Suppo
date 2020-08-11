package com.mba.chatapplication;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Identical extends AsyncTask<ArrayList<String>,String,Boolean> {
    public AsyncResponce3 delegate = null;

    @Override
    protected Boolean doInBackground(ArrayList<String>... arrayLists) {
     String fid1 =  arrayLists[0].get(0);
        String fid2 =   arrayLists[0].get(1);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{ \"faceId1\": \""+fid1+"\",\r\n    \"faceId2\":\""+fid2+"\"\r\n}");
        Request request = new Request.Builder()
                .url("https://eastus.api.cognitive.microsoft.com/face/v1.0/verify")
                .method("POST", body)
                .addHeader("Ocp-Apim-Subscription-Key", "YOUR KEY")
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.code()==200)
            {
               String a= Objects.requireNonNull(response.body()).string();

                JSONObject object= new JSONObject(a);
              return  object.getBoolean("isIdentical");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        delegate.processFinish(s);
    }
}
