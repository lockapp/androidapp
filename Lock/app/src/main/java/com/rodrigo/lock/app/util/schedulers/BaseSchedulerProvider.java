package com.rodrigo.lock.app.util.schedulers;

import androidx.annotation.NonNull;

import rx.Scheduler;

/**
 * Created by Rodrigo on 19/11/2016.
 */
public interface BaseSchedulerProvider {

    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler ui();
}
