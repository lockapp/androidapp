package com.rodrigo.lock.app.bus;

/**
 * Created by Rodrigo on 20/12/2016.
 */

public class Event {
    private String vaultPath;
    private EventType eventType;

    public String getVaultPath() {
        return vaultPath;
    }

    public void setVaultPath(String vaultPath) {
        this.vaultPath = vaultPath;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
