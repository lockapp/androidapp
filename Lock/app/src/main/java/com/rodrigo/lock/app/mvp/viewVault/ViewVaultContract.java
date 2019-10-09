package com.rodrigo.lock.app.mvp.viewVault;

import android.net.Uri;
import android.widget.ImageView;

import com.rodrigo.lock.app.data.Clases.VaultContent;
import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public interface ViewVaultContract {

    interface View extends BaseView<Presenter> {

        void noSePudoAbrirBobeda();

        void setLoadingView();

        void setContentView();

        boolean isActive();

        void finishActivity();

        void setContenToShow(List<VaultContent> contenToShow);

        void openFile(String extension,  Uri fileUri);

        void cambioExtrayendoEnItem (VaultContent content);

        void addFilesToVault(ArrayList<String> archivos);

        void errorToDelete();
    }

    interface Presenter extends BasePresenter {

        void loadPreview(final WeakReference<ImageView> imageViewWeakReference, VaultContent vaultContent);

        void openContent (VaultContent vaultContent);

        //void extractContent (final VaultContent vaultContent);

        void delete (final VaultContent vaultContent);

        String getPassword();

        String getVaultPath();

        void clrearCache ();

        public void deleteVault();

    }
}
