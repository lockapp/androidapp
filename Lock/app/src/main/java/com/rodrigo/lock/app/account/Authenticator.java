package com.rodrigo.lock.app.account;

/**
 * Created by Rodrigo on 27/01/2018.
 */


import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * Created by Rodrigo on 22/02/2016.
 */
/**
 * This class is an implementation of AbstractAccountAuthenticator for
 * authenticating accounts.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_TYPE = "com.rodrigo.lock.app.account";
    public static final String ACCOUNT_NAME = "Lock - Google Drive Sync.";

    public Authenticator(Context context) {
        super(context);
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle loginOptions) {
        throw new UnsupportedOperationException();
    }

}