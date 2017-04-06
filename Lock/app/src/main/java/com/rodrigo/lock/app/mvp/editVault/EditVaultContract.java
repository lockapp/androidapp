package com.rodrigo.lock.app.mvp.editVault;

import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public interface EditVaultContract {

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

        void showErrorEmptyActualPassword();
    }

    interface Presenter extends BasePresenter {

        void editar (String fileName/*, String actualPassword, String newPassword1, String newPassword2*/);

        void populateVault();
    }
}
