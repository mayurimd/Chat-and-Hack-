package com.groupd.hackchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.


/*

This is an adapter class holding the Chat conversation between users that is listed in the PrivateChatActivity class.

 */

public class ChatListActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener  {

    private ListView listView;
    private ChatListAdapter adapter;
    private ArrayList<String> ListName;
    private ArrayList<String> ListEmail;


    String ME = "";

    String YOU = "";

    String WhoStartedThisChat = "ME";

    String []dataValues = new String[2];
    int counter = 0;

    int count = 0;

    Bitmap photo_ME;

    ImageView photo_ME_View;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        LoadME();

        Intent intent = getIntent();
        photo_ME = (Bitmap) intent.getParcelableExtra("photo_ME");


        photo_ME_View = (ImageView) findViewById(R.id.photo_ME);

        if (photo_ME != null)
        {
            photo_ME_View.setImageBitmap(photo_ME);
        }


        dataValues[0] = "";
        dataValues[1] = "";
        counter = 0;

        count = 0;

        ListName = new ArrayList<>();
        ListEmail = new ArrayList<>();


        listView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        adapter = new ChatListAdapter(this, ListName, ListEmail);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listView.setEnabled(false);

                TextView ListEmailText = (TextView) view.findViewById(R.id.EmailID);

                YOU = ListEmailText.getText().toString();

                YOU = YOU.replace("(", "");
                YOU = YOU.replace(")", "");

                SaveYOU();

                new CheckWhetherChatWindowExistsOnServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + ME + "-" + YOU + ".txt");


            }
        });




        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */



        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        ListName.clear();
                                        ListEmail.clear();

                                        new FetchChatListFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatList/ChatList.txt");


                                    }
                                }
        );





    }

    @Override
    public void onRefresh() {


        ListName.clear();
        ListEmail.clear();


        new FetchChatListFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatList/ChatList.txt");



    }

    private class FetchChatListFromServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return FetchChatList(urls[0]);
            } catch (IOException e) {
                return "Sorry, We cannot retrieve credits data form the server at this moment.";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dataValues[0] = "";
            dataValues[1] = "";

            counter = 0;

            ListName.clear();
            ListEmail.clear();


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            swipeRefreshLayout.setRefreshing(false);

            if (result.equals("OK")) {


                listView.post(new Runnable() {
                    @Override
                    public void run() {

                        Collections.reverse(ListName);
                        Collections.reverse(ListEmail);
                        adapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(0);


                    }
                });


            }

            else
            {

                Toast.makeText(getApplicationContext(), "Network Connection Problem. Make sure that your internet is properly connected", Toast.LENGTH_SHORT).show();
            }




        }
    }

    private String FetchChatList(String myurl) throws IOException, UnsupportedEncodingException {
        InputStream is = null;


        // Only display the first 500 characters of the retrieved
        // web page content.


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();




            is = conn.getInputStream();

            BufferedReader textReader = new BufferedReader(new InputStreamReader(is));


            String readlineText;


            while ((readlineText = textReader.readLine()) != null) {



                for (int i = 0 ; i < readlineText.length() ; ++i)
                {
                    if (readlineText.charAt(i) == '|')
                    {
                        ++counter;

                        continue;
                    }

                    dataValues[counter] = (dataValues[counter] + readlineText.charAt(i));
                }


                if(dataValues[0].equals(ME))
                {
                    counter = 0;
                    dataValues[0] = "";
                    dataValues[1] = "";


                }
                else {

                    ListName.add(dataValues[1]);
                    ListEmail.add(dataValues[0]);

                    counter = 0;
                    dataValues[0] = "";
                    dataValues[1] = "";

                }





            }




            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                return "OK";

            }
            else
            {
                conn.disconnect();
                return "NetworkError";
            }


            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {


            if (is != null)
            {
                is.close();


            }

        }
    }




    private class CheckWhetherChatWindowExistsOnServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return CheckWhetherChatWindowExists(urls[0]);
            } catch (IOException e) {
                return "NotFound";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            if (result.equals("OK")) {

                listView.setEnabled(true);

                if (count == 0)
                {
                    WhoStartedThisChat = "ME";

                    SaveWhoStartedThisChat();

                    if (photo_ME != null)
                    {


                        Intent intent = new Intent (ChatListActivity.this , PrivateChatActivity.class);
                        intent.putExtra("photo_ME", photo_ME);
                        startActivity(intent);
                        finish();


                    }

                }
                if (count == 1)
                {
                    WhoStartedThisChat = "YOU";

                    SaveWhoStartedThisChat();

                    if (photo_ME != null)
                    {


                        Intent intent = new Intent (ChatListActivity.this , PrivateChatActivity.class);
                        intent.putExtra("photo_ME", photo_ME);
                        startActivity(intent);
                        finish();


                    }

                }
                if (count == 2)
                {
                    count = 0;

                    SaveWhoStartedThisChat();

                    if (photo_ME != null)
                    {


                        Intent intent = new Intent (ChatListActivity.this , PrivateChatActivity.class);
                        intent.putExtra("photo_ME", photo_ME);
                        startActivity(intent);
                        finish();


                    };

                    WhoStartedThisChat = "ME";
                }




            }

            else if (result.equals("NotFound"))
            {
                ++count;

                if (count == 1) {

                    new CheckWhetherChatWindowExistsOnServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + YOU + "-" + ME + ".txt");



                }
                else if (count == 2)
                {
                    new CreateChatWindowOnServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/CreateChatWindow.php");
                }


            }
            else
            {

                listView.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

            }



        }
    }

    private String CheckWhetherChatWindowExists(String myurl) throws IOException, UnsupportedEncodingException {
        InputStream is = null;


        // Only display the first 500 characters of the retrieved
        // web page content.


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();




            is = conn.getInputStream();

            BufferedReader textReader = new BufferedReader(new InputStreamReader(is));


            String readlineText;


            while ((readlineText = textReader.readLine()) != null) {




            }




            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                return "OK";

            }
            else
            {
                conn.disconnect();
                return "NetworkError";
            }




            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {


            if (is != null)
            {
                is.close();


            }

        }
    }





    private class CreateChatWindowOnServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return CreateChatWindow(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



            if (result.equals("OK")) {

                WhoStartedThisChat = "ME";

                SaveWhoStartedThisChat();

                if (photo_ME != null)
                {


                    Intent intent = new Intent (ChatListActivity.this , PrivateChatActivity.class);
                    intent.putExtra("photo_ME", photo_ME);
                    startActivity(intent);
                    finish();


                };



            }
            else
            {

                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private String CreateChatWindow(String myurl) throws IOException, UnsupportedEncodingException {

        OutputStream os = null;


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // Starts the query
            conn.connect();


            os = conn.getOutputStream();

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("ChatWindow", ME + "-" + YOU + ".txt");

            String query = builder.build().getEncodedQuery();


            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();

            // Convert the InputStream into a string
            // String contentAsString = readIt(is, len);


            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                return "OK";
            }
            else {
                return "NetworkError";
            }

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {

            if (os != null)
            {
                os.close();

            }

        }
    }




    public void LoadME()
    {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ME = prefs.getString("ChatAndHackME", null);
        

    }




    public void SaveYOU()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChatAndHackYOU", YOU);
        editor.apply();

    }

    public void SaveWhoStartedThisChat()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChatAndHackWhoStartedThisChat", WhoStartedThisChat);
        editor.apply();

    }


    public void LogOutME(View v)
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChatAndHackME", null);
        editor.apply();




        Intent intent = new Intent (ChatListActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();

    }


    public void QuitButtonClicked(View v)
    {

        finish();
    }


}
