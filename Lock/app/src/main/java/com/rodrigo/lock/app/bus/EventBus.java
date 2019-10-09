package com.rodrigo.lock.app.bus;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Rodrigo on 20/12/2016.
 */

public class EventBus {

    private static EventBus instance;

    private PublishSubject<Event> subject = PublishSubject.create();

    public static  EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Pass any event down to event listeners.
     */
    public void addEvent(Event object) {
        subject.onNext(object);
    }

    /**
     * Subscribe to this Observable. On event, do something
     * e.g. replace a fragment
     */
    public Observable<Event> getEvents() {
        return subject;
    }
}