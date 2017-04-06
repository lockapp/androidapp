package com.rodrigo.lock.app.mvp.editVault;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditVault extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public  static final String VAULT_PATH = "VAULT_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_vault_activity);
        ButterKnife.inject(this);

        // Set up the toolbar.
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        String path =  getIntent().getStringExtra(VAULT_PATH);
        actionBar.setTitle(R.string.z_edit);


        EditVaultFragment addEditTaskFragment =
                (EditVaultFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (addEditTaskFragment == null) {
            addEditTaskFragment = EditVaultFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.contentFrame);
        }

        // Create the presenter
        new EditVaultPresenter(
                path,
                addEditTaskFragment,
                Injection.provideSchedulerProvider());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
