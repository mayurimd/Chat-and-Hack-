package com.groupd.hackchat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.


/*

 This class is responsible for signing up new user by uploading new credientails to database such as
 Profile Photo, Name, Email, and Password.

 */


public class SignUpActivity extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Uri picUri;

    ImageView mImageView;


    EditText NameEditText;
    EditText EmailEditText;
    EditText PasswordEditText;
    EditText ConfirmPasswordEditText;

    String Name;
    String Email;
    String Password;
    String ConfirmPassword;

    String ba1 = "";

    String []dataValues = new String[3];
    int counter = 0;

    boolean userFound = false;


    Button registerButton;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        dataValues[0] = "";
        dataValues[1] = "";
        dataValues[2] = "";
        counter = 0;

        userFound = false;

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setEnabled(false);

        NameEditText = (EditText) findViewById(R.id.NameID);
        EmailEditText = (EditText) findViewById(R.id.EmailID);
        PasswordEditText = (EditText) findViewById(R.id.PasswordID);
        ConfirmPasswordEditText = (EditText) findViewById(R.id.ConfirmPasswordID);

        registerButton = (Button) findViewById(R.id.registerButtonID);
        loginButton = (Button) findViewById(R.id.loginButtonID);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            picUri = data.getData();


            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 2);


        }

        if (requestCode == 2 && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Matrix matrix = new Matrix();
            //  matrix.postScale(-1, 1, imageBitmap.getWidth()/2, imageBitmap.getHeight()/2);
            // Bitmap bbb = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);

            mImageView.setImageBitmap(imageBitmap);
            mImageView.setEnabled(true);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            ba1 =  Base64.encodeToString(ba, Base64.DEFAULT);



        }




    }

    public void CaptureImage(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public void LoginClicked(View v)
    {

        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);

        Intent intent = new Intent(SignUpActivity.this , LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void RegisterButtonClicked(View v)
    {

        Name = NameEditText.getText().toString();
        Email = EmailEditText.getText().toString();
        Password = PasswordEditText.getText().toString();
        ConfirmPassword = ConfirmPasswordEditText.getText().toString();



        Email = Email.replace(" ","");


        if(Name.length() > 0 && isEmailValid(Email) && Password.length() > 0 && ConfirmPassword.length() > 0 && mImageView.isEnabled())
        {
            if (Password.equals(ConfirmPassword))
            {

                new CheckWhetherUserAlreadyExistsOnServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/Credentials/Credentials.txt");

            }
            else
            {
                Toast.makeText(this, "Passwords are not matched!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Fill All fields", Toast.LENGTH_SHORT).show();
        }

    }



    private class SignUpToServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return SignUpFunction(urls[0]);
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

                SaveME();

                Intent intent = new Intent(SignUpActivity.this, ValidateCameraPermissionActivity.class);
                startActivity(intent);
                finish();


            }
            else
            {

                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private String SignUpFunction(String myurl) throws IOException, UnsupportedEncodingException {

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
                    .appendQueryParameter("Email", Email)
                    .appendQueryParameter("Name" , Name)
                    .appendQueryParameter("Password" , Password)
                    .appendQueryParameter("ChatAndHackbase64", ba1)
                    .appendQueryParameter("ChatAndHackImageName", ("ProfilePhotos/" + Email + ".jpg"));

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






    private class CheckWhetherUserAlreadyExistsOnServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return CheckWhetherUserAlreadyExists(urls[0]);
            } catch (IOException e) {
                return "NotFound";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);

            dataValues[0] = "";
            dataValues[1] = "";
            dataValues[2] = "";

            counter = 0;

            userFound = false;



        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            if (result.equals("OK")) {

                if(userFound)
                {

                    loginButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "This email is already registered", Toast.LENGTH_SHORT).show();

                }
                else
                {

                    new SignUpToServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/Register.php");


                }

            }

            else if (result.equals("NotFound"))
            {

                new SignUpToServer().execute("http://www.voltbuy.com/ChatAndHack/UserData/Register.php");

            }
            else
            {

                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

            }



        }
    }

    private String CheckWhetherUserAlreadyExists(String myurl) throws IOException, UnsupportedEncodingException {
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

            userFound = false;


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



                if (dataValues[0].equals(Email))
                {
                    userFound = true;
                    break;
                }


                counter = 0;
                dataValues[0] = "";
                dataValues[1] = "";
                dataValues[2] = "";




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







    public void SaveME()
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChatAndHackME", Email);
        editor.apply();

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



}

