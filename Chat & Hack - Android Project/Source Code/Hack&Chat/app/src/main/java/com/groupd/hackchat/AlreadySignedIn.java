package com.groupd.hackchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.


/*

This class checks at the launch of the application if the user was previously logged in or not.
If user was logged in previously, so it takes the user to Chat List screen otherwise take user to
login screen.

 */


public class AlreadySignedIn extends Activity {

    String ME = null;

    Bitmap photo_ME = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoadME();

        if (ME != null)
        {


            new RetrieveProfilePhotoFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ProfilePhotos/" + ME + ".jpg");

        }
        else
        {


            Intent intent = new Intent (AlreadySignedIn.this , LoginActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void LoadME()
    {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ME = prefs.getString("ChatAndHackME", null);



    }






    private class RetrieveProfilePhotoFromServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return RetrieveProfilePhoto(urls[0]);
            } catch (IOException e) {


                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            photo_ME = null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



            if(result.equals("OK")) {


                if (photo_ME != null)
                {


                    Intent intent = new Intent (AlreadySignedIn.this , ChatListActivity.class);
                    intent.putExtra("photo_ME", photo_ME);
                    startActivity(intent);
                    finish();


                }

            }
            else if (result.equals("NetworkError"))
            {

                Toast.makeText(getApplicationContext(), "N E", Toast.LENGTH_SHORT).show();


            }
            else
            {

                Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
            }




        }
    }

    private String RetrieveProfilePhoto(String myurl) throws IOException, UnsupportedEncodingException {
        InputStream is = null;

        // Only display the first 500 characters of the retrieved
        // web page content.


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();



            is = conn.getInputStream();

            BufferedReader textReader = new BufferedReader(new InputStreamReader(is));


            BufferedInputStream bis = new BufferedInputStream(is, 8190);

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte)current);
            }
            byte[] imageData = baf.toByteArray();
            photo_ME = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);



            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                return "OK";
            }
            else
            {
                return "NetworkError";
            }


        } finally {


            if (is != null)
            {
                is.close();


            }

        }
    }




}
