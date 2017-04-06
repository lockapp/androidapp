package com.rodrigo.lock.app.mvp.createVault;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreateEditVaultActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)   Toolbar toolbar;

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

        CreateEditVaultFragment addEditTaskFragment =
                (CreateEditVaultFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String taskId = null;
        if (addEditTaskFragment == null) {
            addEditTaskFragment = CreateEditVaultFragment.newInstance();

            if (getIntent().hasExtra(CreateEditVaultFragment.ARGUMENT_EDIT_TASK_ID)) {
                taskId = getIntent().getStringExtra(CreateEditVaultFragment.ARGUMENT_EDIT_TASK_ID);
                actionBar.setTitle(R.string.z_edit_task);
            } else {
                actionBar.setTitle(R.string.z_add_task);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.contentFrame);
        }

        // Create the presenter
        new CreateEditVaultPresenter(
                taskId,
                addEditTaskFragment,
                Injection.provideSchedulerProvider());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
