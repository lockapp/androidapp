package com.rodrigo.lock.app.account;

/**
 * Created by Rodrigo on 27/01/2018.
 */


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//import android.accounts.Account;
/**
 * Created by Rodrigo on 22/02/2016.
 */

public class GenericAccountService extends Service {
    //    private static final String TAG = "GenericAccountService";
//    private static final String ACCOUNT_TYPE = "com.example.android.network.sync.basicsyncadapter";
//    public static final String ACCOUNT_NAME = "sync";
    private Authenticator mAuthenticator;

    /**
     * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
     *
     * @return Handle to application's account (not guaranteed to resolve unless CreateSyncAccount()
     *         has been called)
     */
//    public static Account GetAccount() {
//        // Note: Normally the account name is set to the user's identity (username or email
//        // address). However, since we aren't actually using any user accounts, it makes more sense
//        // to use a generic string in this case.
//        //
//        // This string should *not* be localized. If the user switches locale, we would not be
//        // able to locate the old account, and may erroneously register multiple accounts.
//        final String accountName = ACCOUNT_NAME;
//        return new Account(accountName, ACCOUNT_TYPE);
//    }

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }



}