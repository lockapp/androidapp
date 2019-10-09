package com.rodrigo.lock.app.mvp.openVault;

import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

import java.util.ArrayList;

/**
 * Created by Rodrigo on 08/12/2016.
 */

public interface OpenVaultContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showErrorEmptyPassword();

        void showErrorPasswordIncorrect();

        void openVault(String password, String path, ArrayList<String> filesToAdd);

        boolean isActive();

        void setVaultName(String name);
    }

    interface Presenter extends BasePresenter {
        void openVault(String password);

        String getVaultPaht();

    }
}
