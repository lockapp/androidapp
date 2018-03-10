package com.rodrigo.lock.app.data.source;

import android.text.TextUtils;

import com.rodrigo.lock.app.data.Clases.Vault;
import com.rodrigo.lock.app.data.converters.Converter;
import com.rodrigo.lock.core.utils.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class VaultsRepository {

    public static List<File> getVaultsFiles(){
        List<File> res = new LinkedList();

        File baseFile = new File(Preferences.getDefaultVaultDirectory());
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }

        for (File f : baseFile.listFiles()){
            if ( FileUtils.esBobeda(f)){
                res.add(f);
            }
        }

        return res;
    }





    public static Observable<List<Vault>> getVaults() {
        return Observable.from(getVaultsFiles())
                .map((new Func1<File, Vault>() {
                    @Override
                    public Vault call(File srcObj) {
                        return Converter.convertVault(srcObj);
                    }
                }))
                .toSortedList(new Func2<Vault, Vault, Integer>() {
                    @Override
                    public Integer call(Vault vault, Vault vault2) {
                        return vault.getName().compareTo(vault2.getName());
                    }
                });

    }


    public static Observable<Vault> getVault(String fullPath) {
        if (TextUtils.isEmpty(fullPath)) {
            return Observable.empty();
        }
        File file = new File(fullPath);
        if (!file.exists() || !FileUtils.esBobeda(file)) {
            return Observable.empty();
        }

        return Observable.just(file)
                .map((new Func1<File, Vault>() {
                    @Override
                    public Vault call(File srcObj) {
                        return Converter.convertVault(srcObj);
                    }
                }));
    }


}
