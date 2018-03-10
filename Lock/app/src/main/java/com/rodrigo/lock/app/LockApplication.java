package com.rodrigo.lock.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.rodrigo.lock.app.account.Authenticator;
import com.rodrigo.lock.app.provider.DummyProvider;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class LockApplication  extends MultiDexApplication {

    // private static final String TAG = Tags.getTag(SpringApplication.class);
    private static Context context;
    private static Account appAccount=null;


    //TODO: configure AndroLog
    @Override
    public void onCreate() {
        super.onCreate();
        LockApplication.context = getApplicationContext();

        setupAppAccount();
        requestForcedSync();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);

        LockApplication.context = getApplicationContext();
        requestForcedSync();
    }


    public static Context getAppContext() {
        return LockApplication.context;
    }




    public static void requestForcedSync() {
        //Log.i(RegistroApplication.class.getName(), "----------------------------------r> entra en el request for sync");
        //ContentResolver.requestSync(createDummyAccount(this), SimicContentProvider.AUTHORITY, Bundle.EMPTY);
        if (appAccount!=null){
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(appAccount, DummyProvider.AUTHORITY, bundle);
        }
    }







    private void setupAppAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            appAccount = accounts[0];
            //appAccount.
        }else{
            // /Log.i(TAG, "UASpring account not present, creating...");
            appAccount = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
            accountManager.addAccountExplicitly(appAccount, null, null);

            ContentResolver.setSyncAutomatically(appAccount, DummyProvider.AUTHORITY, true);
            ContentResolver.setIsSyncable(appAccount, DummyProvider.AUTHORITY, 1); //de este no estoy tan seguro si hay que ponerlo
            //ContentResolver.addPeriodicSync(appAccount, DummyProvider.AUTHORITY, new Bundle(), Constants.Backup.frequency_in_seconds); //este hay que probarlo
        }

    }


}
