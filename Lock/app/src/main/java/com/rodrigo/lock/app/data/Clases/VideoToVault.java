package com.rodrigo.lock.app.data.Clases;

import android.graphics.Bitmap;
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

public class VideoToVault extends DataNewFile {

    public VideoToVault(File in) {
        super(in);
    }
    public InputStream getPreview() {
        Bitmap tumb = MediaUtils.getVideoPreview(in.getAbsolutePath());
        return MediaUtils.bitMapToInputStream(tumb);
    }

}
