package com.rodrigo.lock.app.mvp.addToVault;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.migracion.MigrarUtilsDeprecated;
import com.rodrigo.lock.app.mvp.listVaults.VaultPresenetr;
import com.rodrigo.lock.app.mvp.listVaults.VaultsFragment;
import com.rodrigo.lock.app.mvp.migrar.MigrarActivity;
import com.rodrigo.lock.app.mvp.migrar.MigrarFragment;
import com.rodrigo.lock.app.mvp.openVault.OpenVaultActivity;
import com.rodrigo.lock.app.utils.ActivityUtils;
import com.rodrigo.lock.app.utils.Injection;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddToVaultActivity extends AppCompatActivity implements AddTovaultContract.irLuegoDeRecibir {


    protected VaultPresenetr mTasksPresenter;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;


    private ArrayList<String> archivos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ResolverAccionUtils.resolverAccion(getIntent(), this);

        //copiado el oncreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_vault_activity);

        ButterKnife.bind(this);
        // Set up the toolbar.
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.z_ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer.
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        VaultsFragment tasksFragment = (VaultsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = VaultsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        // Create the presenter
        mTasksPresenter = new VaultPresenetr(
                tasksFragment,
                Injection.provideSchedulerProvider());
        //end oncreate

        tasksFragment.setMItemListener(clickListener);
        ab.setTitle(R.string.selecione_la_bobeda_a_aniadir);
    }

    private VaultsFragment.TaskItemListener clickListener = new VaultsFragment.TaskItemListener() {
        @Override
        public void onTaskClick(Vault clickedVault) {
            Intent intent = new Intent(getApplicationContext(), OpenVaultActivity.class);
            intent.putExtra(OpenVaultActivity.EXTRA_VAULT_PATH, clickedVault.getFullPath());
            intent.putStringArrayListExtra(OpenVaultActivity.EXTRA_FILES_TO_ADD, archivos);
            startActivity(intent);
            finish();
        }
    };


    @Override
    public void irADecrypt(String filePath) {
        Intent intent = new Intent(this, OpenVaultActivity.class);
        intent.putExtra(OpenVaultActivity.EXTRA_VAULT_PATH, filePath);
        startActivity(intent);
        finish();
    }

    @Override
    public void irAEncrypt(ArrayList<String> filesPath) {
        this.archivos = filesPath;
    }

    @Override
    public void irAMigrar(MigrarUtilsDeprecated obje) {
        Intent intent = new Intent(this, MigrarActivity.class);
        intent.putExtra(MigrarFragment.DATA_MIGRACION, obje);
        startActivity(intent);
        finish();
    }

    @Override
    public void mostrarArchivoNoEncontrado(String path) {
        Toast.makeText(this, getResources().getText(R.string.archivo_no_encontrado) + path, Toast.LENGTH_SHORT).show();
        finish();
    }
}
