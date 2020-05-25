package com.rodrigo.lock.app.mvp.editVault;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditVault extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public  static final String VAULT_PATH = "VAULT_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_vault_activity);
        ButterKnife.bind(this);

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
