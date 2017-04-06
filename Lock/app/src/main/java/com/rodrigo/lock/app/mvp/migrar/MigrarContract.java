package com.rodrigo.lock.app.mvp.migrar;

import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public interface MigrarContract {

    interface View extends BaseView<Presenter> {

        void showErrorEmptyName();

        void showErrorVaultExists();

        void showErrorEmptyPassword();

        void showErrorIncorrectPassword();

        void showErrorNoSeAhEcontradoVault();

        public void showTasksList();

        boolean isActive();

        public void terminar();
    }

    interface Presenter extends BasePresenter {

        void migrarVault(String name, String password);

        void populateVault();
    }
}
