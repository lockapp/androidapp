package com.rodrigo.lock.app.mvp.openVault;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.source.VaultsRepository;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rodrigo on 08/12/2016.
 */

public class OpenVaultPresenter implements OpenVaultContract.Presenter {

    private String vaultPath;
    private ArrayList<String> filesToAdd;
    @NonNull
    private final OpenVaultContract.View mView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public OpenVaultPresenter(String vaultPath,
                              ArrayList<String> filesToAdd,
                              @NonNull OpenVaultContract.View mView,
                              @NonNull BaseSchedulerProvider mSchedulerProvider) {
        this.vaultPath = vaultPath;
        this.filesToAdd=filesToAdd;
        this.mView = mView;
        this.mSchedulerProvider = mSchedulerProvider;


        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        openVault();
        //load vault
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void openVault(String password) {
        if (TextUtils.isEmpty(password)){
            mView.showErrorEmptyPassword();
        }else{
            mView.openVault(password, this.vaultPath, this.filesToAdd);
        }
    }

    @Override
    public String getVaultPaht() {
        return vaultPath;
    }


    private void openVault() {
        if (TextUtils.isEmpty(vaultPath)) {
            //mView.showMissingTask();
            return;
        }

        mView.setLoadingIndicator(true);

        Subscription subscription = VaultsRepository
                .getVault(vaultPath)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<Vault>() {
                    @Override
                    public void onCompleted() {
                        mView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Vault vault) {
                        showVault(vault);
                    }
                });
        mSubscriptions.add(subscription);
    }


    private void showVault(@NonNull Vault vault) {
        mView.setVaultName(vault.getName());
    }

}
