package com.rodrigo.lock.app.data.Clases;



import com.google.android.gms.drive.DriveId;

import java.util.Date;


/**
 * Created by Rodrigo on 27/12/2017.
 */


public class GlucosioBackup {

    private String title;
    private DriveId driveId;
    private Date modifiedDate;
    private long backupSize;

    public GlucosioBackup(String title, DriveId driveId, Date modifiedDate, long backupSize) {
        this.title = title;
        this.driveId = driveId;
        this.modifiedDate = modifiedDate;
        this.backupSize = backupSize;
    }

    public DriveId getDriveId() {
        return driveId;
    }

    public void setDriveId(DriveId driveId) {
        this.driveId = driveId;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public long getBackupSize() {
        return backupSize;
    }

    public void setBackupSize(long backupSize) {
        this.backupSize = backupSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}