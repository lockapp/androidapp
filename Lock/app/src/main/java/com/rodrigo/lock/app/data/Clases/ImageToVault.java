package com.rodrigo.lock.app.data.Clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.rodrigo.lock.app.utils.MediaUtils;
import com.rodrigo.lock.core.datatype.DataNewFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Created by Rodrigo on 06/12/2016.
 */

public class ImageToVault extends DataNewFile {

    public ImageToVault(File in) {
        super(in);
    }




    @Override
    public InputStream getPreview() {
        Bitmap tumb =MediaUtils.getImagePreview(in.getAbsolutePath());
        return MediaUtils.bitMapToInputStream(tumb);
    }


}
