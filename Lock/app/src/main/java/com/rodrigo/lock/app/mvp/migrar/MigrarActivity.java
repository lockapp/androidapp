package com.rodrigo.lock.app.mvp.migrar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.migracion.MigrarUtilsDeprecated;

import com.rodrigo.lock.app.utils.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MigrarActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.migrar_activity);
        ButterKnife.bind(this);
        // Set up the toolbar.
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        MigrarFragment addEditTaskFragment =
                (MigrarFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        MigrarUtilsDeprecated obje = (MigrarUtilsDeprecated) getIntent().getSerializableExtra(MigrarFragment.DATA_MIGRACION);;
        if (addEditTaskFragment == null) {
            addEditTaskFragment = MigrarFragment.newInstance();
            //actionBar.setTitle(R.string.z_add_task);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.contentFrame);
        }
        new MigrarPresenter(
                obje,
                addEditTaskFragment,
                this.getApplicationContext());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }












}
