package com.mba.chatapplication;

import org.json.JSONException;

public  interface AsyncResponce {
    void processFinish(String output) throws JSONException;

}
