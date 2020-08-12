package com.rodrigo.lock.app.mvp.migrar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.migracion.MigracionService;
import com.rodrigo.lock.app.migracion.MigrarUtilsDeprecated;
import com.rodrigo.lock.app.migracion.MigrationExecutor;
import com.rodrigo.lock.core.EncryptedFileSystem;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;

import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class MigrarPresenter implements MigrarContract.Presenter {

    @NonNull
    private final MigrarContract.View mMigracionView;
    @NonNull
    private final Context context;
    @NonNull
    private MigrarUtilsDeprecated mMigrarUtilsDeprecated;
    @NonNull
    private CompositeSubscription mSubscriptions;


    public MigrarPresenter(@NonNull MigrarUtilsDeprecated migrarUtilsDeprecated,
                           @NonNull MigrarContract.View addVaultView,
                           @NonNull  Context context) {
        mMigrarUtilsDeprecated =checkNotNull( migrarUtilsDeprecated, "migrarUtilsDeprecated cannot be null!");
        mMigracionView = checkNotNull(addVaultView, "addTaskView cannot be null!");
        this.context = checkNotNull(context, "context cannot be null!");

        mSubscriptions = new CompositeSubscription();
        mMigracionView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        populateVault();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void migrarVault(String fileName, String password) {
        if (TextUtils.isEmpty(fileName)) {
            mMigracionView.showErrorEmptyName();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mMigracionView.showErrorEmptyPassword();
            return;
        }


        String fullPath = Preferences.getDefaultVaultDirectory() + File.separator + fileName + "." + FileUtils.LOCK_EXTENSION;
        if (new File(fullPath).exists()) {
            mMigracionView.showErrorVaultExists();
            return;
        }


        MigrationExecutor me = new MigrationExecutor(password, new File(mMigrarUtilsDeprecated.getFullpath()), mMigrarUtilsDeprecated.getOffset());
        try {
            me.checkAndInit();
        }catch (Exception e){
            mMigracionView.showErrorIncorrectPassword();
            return;
        }
        EncryptedFileSystem out = EncryptedFileSystemHandler.createEncryptedFile(fullPath, password);
        me.setOut(out);

        int id = MigracionService.addPendientes(me);


        Intent i = new Intent(context, MigracionService.class);
        i.putExtra(Constants.CRYPTO_CONTROLLER, id);
        context.startService(i);

        mMigracionView.terminar();
        //mMigracionView.showTasksList();
    }


    @Override
    public void populateVault() {


    }
    private class DesencriptarAsincronaService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
//                controller.chequear();
//                cc = controller.getDecryptController(DecryptActivity.this.getApplicationContext());
//                cc.checkAndInit();
                return null;
            } catch (Exception e) {
                return (e.getMessage() == null) ? "Error" : e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String error) {
//            showProgress(false);
//            if (error == null) {
//                convertirEnService();
//            } else {
//                mostrarError(error);
//            }
        }

    }


}
