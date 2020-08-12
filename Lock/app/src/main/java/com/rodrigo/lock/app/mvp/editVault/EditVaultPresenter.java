package com.rodrigo.lock.app.mvp.editVault;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.converters.Converter;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;
import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;

import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class EditVaultPresenter implements EditVaultContract.Presenter {

    @NonNull
    private final EditVaultContract.View mAddVaultView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    private File vaultFile;

    @NonNull
    private CompositeSubscription mSubscriptions;


    public EditVaultPresenter(@Nullable String vaultPath,
                              @NonNull EditVaultContract.View addVaultView,
                              @NonNull BaseSchedulerProvider schedulerProvider) {
        vaultFile = new File(vaultPath);
        mAddVaultView = checkNotNull(addVaultView, "addTaskView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");

        mSubscriptions = new CompositeSubscription();
        mAddVaultView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (vaultFile!= null  && FileUtils.esBobeda(vaultFile)) {
            populateVault();
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }


    @Override
    public void populateVault() {
        Vault vault = Converter.convertVault(vaultFile);
        mAddVaultView.setName(vault.getName());
    }



    public void editar (String fileName/*, String actualPassword, String newPassword1, String newPassword2*/){

        if (TextUtils.isEmpty(fileName)) {
            mAddVaultView.showErrorEmptyName();
            return;
        }
        /*
        if (TextUtils.isEmpty(actualPassword)) {
            mAddVaultView.showErrorEmptyActualPassword();
            return;
        }
        if (TextUtils.isEmpty(newPassword1)) {
            mAddVaultView.showErrorEmptyPassword();
            return;
        }
        if (TextUtils.isEmpty(newPassword2)) {
            mAddVaultView.showErrorEmptyPassword2();
            return;
        }
        if (!TextUtils.equals(newPassword1, newPassword2)) {
            mAddVaultView.showErrorPasswordNotMatch();
            return;
        }*/


        String fullPath = Preferences.getDefaultVaultDirectory() + File.separator + fileName + "." + FileUtils.LOCK_EXTENSION;
        File newFile =new File(fullPath);
        if (newFile.exists() && !vaultFile.getAbsolutePath().equals(newFile.getAbsolutePath())){
            mAddVaultView.showErrorVaultExists();
            return;
        }else{
            MediaUtils.rename(vaultFile, newFile);
        }

        //EncryptedFileSystem fs = EncryptedFileSystemHandler.openEncryptedFile(vaultFile.getAbsolutePath(), actualPassword);
        //fs.
        mAddVaultView.showTasksList();

//        Subscription subscription = VaultsRepository.getVault(mVaultPath)
//                .subscribeOn(mSchedulerProvider.computation())
//                .observeOn(mSchedulerProvider.ui())
//                .subscribe(new Observer<Vault>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mAddVaultView.isActive()) {
//                            mAddVaultView.showEmptyTaskError();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(Vault vault) {
//                        if (mAddVaultView.isActive()) {
//                            mAddVaultView.setName(vault.getName());
//                        }
//                    }
//                });
//        mSubscriptions.add(subscription);
    }
}
