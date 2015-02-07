package com.rodrigo.lock.app.presentation.SeeMedia;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Rodrigo on 28/09/2014.
 */
public class VideoViweFragment extends Fragment {
    @InjectView(R.id.imageView)    ImageView thumbnail;
    File f;
    int imageid;
    private ListMediaActivity padre;

    public VideoViweFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_see_video_thumbnail, container, false);
        ButterKnife.inject(this, V);
        //ViewCompat.setTransitionName(thumbnail, getString(R.string.image_grid));

        padre=(ListMediaActivity) getActivity();
        imageid=getArguments().getInt(Constants.SEE_IMAGE_ID);

        try {
            if (  (0 <= imageid)  && (imageid <padre.getCantimages())){
                f = ((ListMediaActivity) getActivity()).getDecryptMediaController().getFile(imageid);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                thumbnail.setImageBitmap(thumb);
            }
        }catch (Exception e){
        }

        return V;
    }




    @OnClick(R.id.play)
    public void vervideo(){
        Intent intent = new Intent(this.getActivity(), PlayVideoActivity.class);
        intent.putExtra(Constants.SEE_VIDEO_PATH, f.getAbsolutePath());
        intent.putExtra(Constants.SEE_IMAGE_ID, imageid);
        intent.putExtra(Constants.CRYPTO_CONTROLLER, padre.getIdCC());
        ((ListMediaActivity) getActivity()).setClearCacheFiles(false);
        ((ListMediaActivity) getActivity()).setDeleteMediaController(false);

        padre.startActivity(intent);
        padre.finish();
    }


    @OnClick(R.id.imageView)
    public void regularbar(){
        ((ListMediaActivity) getActivity()).regularBar();
    }





}
