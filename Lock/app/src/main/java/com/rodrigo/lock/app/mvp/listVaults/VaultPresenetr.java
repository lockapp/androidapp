package com.rodrigo.lock.app.mvp.listVaults;

import android.support.annotation.NonNull;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.source.VaultsRepository;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class VaultPresenetr implements VaultsContract.Presenter {

    //@NonNull
   // private final VaultsRepository mVaultsRepository;

    @NonNull
    private final VaultsContract.View mVaultsView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    //@NonNull
    //private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    @NonNull
    private CompositeSubscription mSubscriptions;

    public VaultPresenetr(
                          @NonNull VaultsContract.View VaultsView,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        //mVaultsRepository = checkNotNull(vaultsRepository, "vaultsRepository cannot be null");
        mVaultsView = checkNotNull(VaultsView, "mVaultsView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mVaultsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadVaults(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

//    @Override
//    public void result(int requestCode, int resultCode) {
//        // If a task was successfully added, show snackbar
//        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
//            mTasksView.showSuccessfullySavedMessage();
//        }
//    }

    @Override
    public void loadVaults() {
        // Simplification for sample: a network reload will be forced on first load.
        loadVaults( true);
        mFirstLoad = false;
    }



    private void loadVaults(final boolean showLoadingUI) {
        if (showLoadingUI) {
            mVaultsView.setLoadingIndicator(true);
        }

        mSubscriptions.clear();
        Subscription subscription = VaultsRepository.getVaults()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<List<Vault>>() {
                    @Override
                    public void onCompleted() {
                        mVaultsView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mVaultsView.showLoadingTasksError();
                    }

                    @Override
                    public void onNext(List<Vault> vaults) {
                        processVaults(vaults);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processVaults(@NonNull List<Vault> vaults) {
        if (vaults.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            mVaultsView.showNoVaults();
        } else {
            // Show the list of tasks
            mVaultsView.showTasks(vaults);
        }
    }



}
