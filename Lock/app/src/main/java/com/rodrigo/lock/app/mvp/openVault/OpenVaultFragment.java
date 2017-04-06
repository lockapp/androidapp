package com.rodrigo.lock.app.mvp.openVault;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.mvp.UI.PasswordEditText;
import com.rodrigo.lock.app.mvp.editVault.EditVault;
import com.rodrigo.lock.app.mvp.viewVault.ViewVaultActivity;
import com.rodrigo.lock.app.utils.ActivityUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 08/12/2016.
 */

public class OpenVaultFragment extends Fragment implements OpenVaultContract.View {

    public static final String ARGUMENT_VAULT_PATH = "VAULT_PATH";

    public static final int START_ACTIVITY_FOR_OPEN_VAULT = 1;
    //public static final int ERROR_DETAIL = 1;

    private OpenVaultContract.Presenter mPresenter;


    @InjectView(R.id.nombreVault)    TextView nameVault;
    @InjectView(R.id.password1)    PasswordEditText password1;

    public static OpenVaultFragment newInstance(String path) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_VAULT_PATH, path);
        OpenVaultFragment fragment = new OpenVaultFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.open_vault_frag, container, false);
        ButterKnife.inject(this, root);
        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(@NonNull OpenVaultContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.z_view_vault, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent i = new Intent(getContext(), EditVault.class);
                i.putExtra(EditVault.VAULT_PATH, mPresenter.getVaultPaht());
                getContext().startActivity(i);
                getActivity().invalidateOptionsMenu();
                getActivity().finish();
                return true;
            //case R.id.delete:
                //Preferences.saveGridViewInVault(true);
                //loadLayautManager();
                //getActivity().invalidateOptionsMenu();
                //return true;
        }
        return false;
    }






    @Override
    public void showErrorEmptyPassword() {
        password1.setError(getResources().getString(R.string.empty_password));
        password1.requestFocus();
    }

    @Override
    public void showErrorPasswordIncorrect() {
        password1.setError(getResources().getString(R.string.error_open_vault));
        password1.requestFocus();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == START_ACTIVITY_FOR_OPEN_VAULT) {
            // LO QUE RETORNA ES EL ERROR sino sale como si no s hubieraa llamado.
            if (resultCode != Activity.RESULT_OK) {
                getActivity().finish();
                return;
            }else{
                showErrorPasswordIncorrect();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setVaultName(String name) {
        nameVault.setText(String.format(getResources().getString(R.string.vault_1), name));
    }


    public void openVault(String password, String path,  ArrayList<String> filesToAdd){
        Intent intent = new Intent(getContext(), ViewVaultActivity.class);
        intent.putExtra(ViewVaultActivity.EXTRA_PASSWORD, password);
        intent.putExtra(ViewVaultActivity.EXTRA_FULL_PATH, path);
        intent.putStringArrayListExtra(ViewVaultActivity.EXTRA_FILES_TO_ADD, filesToAdd);
        startActivityForResult(intent, START_ACTIVITY_FOR_OPEN_VAULT);
    }

    @OnClick(R.id.fab_open_vault)
    public void clickOpenVault(){
        mPresenter.openVault(password1.getText().toString());
    }


    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            nameVault.setText(getString(R.string.loading));
        }
    }



}
