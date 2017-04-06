package com.rodrigo.lock.app.mvp.listVaults;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

import java.util.List;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public interface VaultsContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTasks(List<Vault> vaults);

        //void showAddVault();

        //void showTaskDetailsUi(String taskId);

        void showLoadingTasksError();

        void showNoVaults();

        void showSuccessfullySavedMessage();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadVaults();


    }
}
