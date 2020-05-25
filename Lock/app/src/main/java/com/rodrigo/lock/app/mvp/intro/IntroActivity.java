package com.rodrigo.lock.app.mvp.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.mvp.listVaults.VaultsActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Preferences.getPreference(Constants.Preferences.PREFERENCE_PRIMERA_VEZ_EN_APP, true)
                && isStoragePermissionGranted()){
           startNextActivity();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        ButterKnife.bind(this);




    }

    @OnClick(R.id.fab_done)
    public void clickEnNext(){
        if (isStoragePermissionGranted()){
            startNextActivity();
        }else{
            askPermission();
        }
    }

    public void startNextActivity(){
        Preferences.savePreference(Constants.Preferences.PREFERENCE_PRIMERA_VEZ_EN_APP, false);
        Intent i = new Intent(this, VaultsActivity.class);
        startActivity(i);
        finish();
    }


    public void askPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(IntroActivity.class.getName(),"Permission is granted");
                return true;
            } else {
                Log.v(IntroActivity.class.getName(),"Permission is revoked");
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(IntroActivity.class.getName(),"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(IntroActivity.class.getName(),"Permission: "+permissions[0]+ "was "+grantResults[0]);
            startNextActivity();
        }
    }




}
