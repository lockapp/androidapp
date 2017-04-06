package com.rodrigo.lock.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Rodrigo on 20/11/2016.
 */

public class ActivityUtils {
    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }



    public static Intent shareExludingApp (Context c, Uri uri)  {
        String packageNameToExclude =c.getPackageName();
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = createShareIntent(uri);
        List<ResolveInfo> resInfo = c.getPackageManager().queryIntentActivities(createShareIntent(uri),0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = createShareIntent(uri);

                if (!info.activityInfo.packageName.equalsIgnoreCase(packageNameToExclude)) {
                    targetedShare.setPackage(info.activityInfo.packageName);
                    targetedShareIntents.add(targetedShare);
                }
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),
                    "Select app to share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    targetedShareIntents.toArray(new Parcelable[] {}));
            return chooserIntent;
        }
        return null;
    }

    private   static   Intent createShareIntent (Uri uri)  {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/zip");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        return share ;
    }

}
