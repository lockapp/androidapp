package com.rodrigo.lock.app.presentation.Encrypt;

import android.content.Intent;

import com.rodrigo.lock.app.Constants;

/**
 * Created by Rodrigo on 30/09/2014.
 */
public class ReceiveOther  extends ReceiveAndEncryptActivity{


    @Override
    public void encontrAraccion() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
                handleFile(intent);

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                handleFile(intent);

        }

    }



}
