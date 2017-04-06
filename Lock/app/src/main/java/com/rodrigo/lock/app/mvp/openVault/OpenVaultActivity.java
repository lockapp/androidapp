package com.rodrigo.lock.app.mvp.openVault;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import java.util.ArrayList;

public class OpenVaultActivity extends AppCompatActivity {

    public static final String EXTRA_VAULT_PATH= "VAULT_PATH";
    public static final String EXTRA_FILES_TO_ADD= "EXTRA_FILES_TO_ADD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_vault_activity);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(getResources().getString(R.string.abrir_boveda));

        // Get the requested task id
        String vaultPath = getIntent().getStringExtra(EXTRA_VAULT_PATH);
        ArrayList<String> filesToAdd =  getIntent().getStringArrayListExtra(EXTRA_FILES_TO_ADD);

        OpenVaultFragment taskDetailFragment = (OpenVaultFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (taskDetailFragment == null) {
            taskDetailFragment = OpenVaultFragment.newInstance(vaultPath);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskDetailFragment, R.id.contentFrame);
        }

        // Create the presenter
        new OpenVaultPresenter(
                vaultPath,
                filesToAdd,
                taskDetailFragment,
                Injection.provideSchedulerProvider());
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
