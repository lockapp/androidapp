package com.rodrigo.lock.app.mvp.addToVault;

import com.rodrigo.lock.app.migracion.MigrarUtilsDeprecated;
import com.rodrigo.lock.app.mvp.BasePresenter;
import com.rodrigo.lock.app.mvp.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public interface AddTovaultContract {

    interface irLuegoDeRecibir  {
        void irADecrypt(String filePath);
        void irAEncrypt(ArrayList<String> filesPath);
        void irAMigrar( MigrarUtilsDeprecated mu);
        void mostrarArchivoNoEncontrado( String path);
    }
}
