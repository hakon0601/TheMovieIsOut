package com.example.hakon.movieavailablenotifier;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RetrieveFeedTask extends AsyncTask<Object, Object, JSONObject> {

    private Exception exception;

    protected JSONObject doInBackground(Object... urls) {
        try {
            String urlString = "http://127.0.0.1:5000/movies-coming-soon/";
            HttpURLConnection urlConnection = null;

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            urlConnection.setDoOutput(true);

            urlConnection.connect();

            BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

            char[] buffer = new char[1024];

            String jsonString = new String();

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();

            jsonString = sb.toString();

            System.out.println("JSON: " + jsonString);
            Log.i("json", jsonString);

            return new JSONObject(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject json) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}