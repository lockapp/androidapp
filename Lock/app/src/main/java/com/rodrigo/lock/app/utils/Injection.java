package com.rodrigo.lock.app.utils;

import com.rodrigo.lock.app.data.source.ContentVaultRepository;
import com.rodrigo.lock.app.util.schedulers.BaseSchedulerProvider;
import com.rodrigo.lock.app.util.schedulers.SchedulerProvider;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class Injection {

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }

}
