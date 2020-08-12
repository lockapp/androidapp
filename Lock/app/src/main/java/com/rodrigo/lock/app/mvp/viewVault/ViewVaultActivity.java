package com.rodrigo.lock.app.mvp.viewVault;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import java.util.ArrayList;

public class ViewVaultActivity extends AppCompatActivity {
/*
    //boolean FLAG_IS_FINISH_TASK = true;
    private static Timer backgroundTimer;
    private static TimerTask backgroundTimerTask;
    private static boolean wasInBackground;
    private static final long MAX_BACKGROUND_TIME_MS = 2*60*1000;
*/
    public static final String EXTRA_PASSWORD= "PASSWORD";
    public static final String EXTRA_FULL_PATH= "FULL_PATH";
    public static final String EXTRA_FILES_TO_ADD= "EXTRA_FILES_TO_ADD";

    private ViewVaultPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_vault_activity);
        //ButterKnife.inject(this);
        // Set up the toolbar.

        // Get the requested task id
        String password = getIntent().getStringExtra(EXTRA_PASSWORD);
        String vault_path = getIntent().getStringExtra(EXTRA_FULL_PATH);
        ArrayList<String> filesToAdd=getIntent().getStringArrayListExtra(EXTRA_FILES_TO_ADD);

        ViewVaultFragment frament =  (ViewVaultFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (frament == null) {
            frament = ViewVaultFragment.newInstance(vault_path, password);
            //actionBar.setTitle(R.string.z_add_task);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),frament, R.id.contentFrame);

            //chequear esto si funciona aca
            new ViewVaultPresenter(
                    vault_path,
                    password,
                    filesToAdd,
                    frament,
                    Injection.provideSchedulerProvider());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
