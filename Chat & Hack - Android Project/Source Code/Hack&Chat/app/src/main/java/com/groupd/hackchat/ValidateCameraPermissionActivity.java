package com.groupd.hackchat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.

/*

 Whenever this application is started, this class validates if the Camera Permission
 is granted or not. If permission is not granted, so it asks user to grant permission
 so they he/she may proceed further.

 */


public class ValidateCameraPermissionActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.validate_camera_permission);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);

        }
        else
        {
            Intent intent = new Intent(ValidateCameraPermissionActivity.this , AlreadySignedIn.class);
            startActivity(intent);
            finish();
        }

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Intent intent = new Intent(ValidateCameraPermissionActivity.this , AlreadySignedIn.class);
                    startActivity(intent);
                    finish();


                } else {

                    Toast.makeText(this, "This application cannot be used without Camera Permission. The Camera Permission is required for capturing your profile photo.", Toast.LENGTH_LONG).show();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {

                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);

                    }
                    else
                    {
                        Intent intent = new Intent(ValidateCameraPermissionActivity.this , AlreadySignedIn.class);
                        startActivity(intent);
                        finish();
                    }



                }
                break;
        }





    }


}
