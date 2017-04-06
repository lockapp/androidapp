package com.rodrigo.lock.app.mvp.createVault;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.mvp.UI.PasswordEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class CreateEditVaultFragment extends Fragment implements CreateEditVaultContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private CreateEditVaultContract.Presenter mPresenter;

    @InjectView(R.id.nombre)    EditText name;
    @InjectView(R.id.password1)    PasswordEditText password1;
    @InjectView(R.id.password2)  PasswordEditText password2;

    @InjectView(R.id.fab_edit_task_done)  FloatingActionButton fab;

    public static CreateEditVaultFragment newInstance() {
        return new CreateEditVaultFragment();
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

    @Override
    public void setPresenter(@NonNull CreateEditVaultContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        FloatingActionButton fab =
//                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.create_edit_vault_frag, container, false);
        ButterKnife.inject(this, root);

        //fab.setImageResource(R.drawable.z_ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveVault(name.getText().toString(),password1.getText().toString(), password2.getText().toString());
            }
        });

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(name, getString(R.string.empty_vault_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showErrorEmptyName() {
        name.setError(getResources().getString(R.string.empty_name));
        name.requestFocus();
    }

    @Override
    public void showErrorEmptyPassword() {
        password1.setError(getResources().getString(R.string.empty_password));
        password1.requestFocus();
    }

    @Override
    public void showErrorEmptyPassword2() {
        password2.setError(getResources().getString(R.string.re_password));
        password2.requestFocus();
    }

    @Override
    public void showErrorPasswordNotMatch() {
        password1.setError(getResources().getString(R.string.nomatch_password));
        password2.setError(getResources().getString(R.string.nomatch_password));
        password1.requestFocus();
    }

    @Override
    public void showErrorVaultExists() {
        name.setError(getResources().getString(R.string.exists_vault_whith_name));
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setName(String title) {

    }


    @Override
    public boolean isActive() {
        return isAdded();
    }
}
