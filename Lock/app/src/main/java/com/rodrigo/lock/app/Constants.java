package com.rodrigo.lock.app;

import java.io.File;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class Constants {
    public static class Preferences{
        public static final String PREFERENCE_PRIMERA_VEZ_EN_APP= "PREFERENCE_PRIMERA_VEZ_EN_APP";
        public static final String PREFERENCE_VAULT_DIRECTORY= "PREFERENCE_VAULT_DIRECTORY";
        public static final String PREFERENCE_EXTRACT_DIRECTORY= "PREFERENCE_EXTRACT_DIRECTORY";
        public static final String PREFERENCE_IS_GRID_VIEW_IN_VAULT= "PREFERENCE_IS_GRID_VIEW_IN_VAULT";
    }
    public static class Storage{
        public static final String PACKAGE= "com.rodrigo.lock.app";

        public static final String DEFAULT_DIRECTORY= LockApplication.getAppContext().getCacheDir().getAbsolutePath();
        public static final String DEFAULT_OPEN_VAULT_DIRECTORY = DEFAULT_DIRECTORY + File.separator + "tem_openvault";
        public static final String DEFAULT_EXTRACT_VAULT_DIRECTORY = DEFAULT_DIRECTORY + File.separator + "tem_extract";
    }


    public static final String CRYPTO_CONTROLLER ="CRYPTO_CONTROLLER";

}
