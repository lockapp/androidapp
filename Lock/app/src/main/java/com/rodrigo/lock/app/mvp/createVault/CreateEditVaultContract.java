package com.rodrigo.lock.app.mvp.createVault;

import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public interface CreateEditVaultContract {

    interface View extends BaseView<Presenter> {
        void showEmptyTaskError();

        void showErrorEmptyName();

        void showErrorEmptyPassword();

        void showErrorEmptyPassword2();

        void showErrorPasswordNotMatch();

        void showErrorVaultExists();

        void showTasksList();

        void setName(String title);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveVault(String fileName, String password, String password2);

        void populateVault();
    }
}
