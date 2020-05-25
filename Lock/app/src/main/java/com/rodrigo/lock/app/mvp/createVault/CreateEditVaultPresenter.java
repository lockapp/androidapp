package com.rodrigo.lock.app.mvp.createVault;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.source.Preferences;
import com.rodrigo.lock.app.data.source.VaultsRepository;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;
import com.rodrigo.lock.core.EncryptedFileSystemHandler;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class CreateEditVaultPresenter implements CreateEditVaultContract.Presenter {

    @NonNull
    private final CreateEditVaultContract.View mAddVaultView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @Nullable
    private String mVaultPath;

    @NonNull
    private CompositeSubscription mSubscriptions;


    public CreateEditVaultPresenter(@Nullable String vaultPath,
                                @NonNull CreateEditVaultContract.View addVaultView,
                                @NonNull BaseSchedulerProvider schedulerProvider) {
        mVaultPath = vaultPath;
        mAddVaultView = checkNotNull(addVaultView, "addTaskView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");

        mSubscriptions = new CompositeSubscription();
        mAddVaultView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (!TextUtils.isEmpty(mVaultPath) && FileUtils.esBobeda(new File(mVaultPath))) {
            populateVault();
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void saveVault(String fileName, String password, String password2) {
        if (TextUtils.isEmpty(fileName)) {
            mAddVaultView.showErrorEmptyName();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mAddVaultView.showErrorEmptyPassword();
            return;
        }
        if (TextUtils.isEmpty(password2)) {
            mAddVaultView.showErrorEmptyPassword2();
            return;
        }
        if (!TextUtils.equals(password, password2)) {
            mAddVaultView.showErrorPasswordNotMatch();
            return;
        }


        String fullPath = Preferences.getDefaultVaultDirectory() + File.separator + fileName + "." + FileUtils.LOCK_EXTENSION;
        if (new File(fullPath).exists()){
            mAddVaultView.showErrorVaultExists();
            return;
        }

        EncryptedFileSystemHandler.createEncryptedFile(fullPath, password);
        mAddVaultView.showTasksList();
    }


    @Override
    public void populateVault() {
        if (TextUtils.isEmpty(mVaultPath)) {
            throw new RuntimeException("populateVault() was called but task is new.");
        }
        Subscription subscription = VaultsRepository.getVault(mVaultPath)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<Vault>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAddVaultView.isActive()) {
                            mAddVaultView.showEmptyTaskError();
                        }
                    }

                    @Override
                    public void onNext(Vault vault) {
                        if (mAddVaultView.isActive()) {
                            mAddVaultView.setName(vault.getName());
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }
}
