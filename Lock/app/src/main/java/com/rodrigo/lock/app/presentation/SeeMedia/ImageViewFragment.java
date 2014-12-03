package com.rodrigo.lock.app.presentation.SeeMedia;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Rodrigo on 08/08/2014.
 */
public class ImageViewFragment extends Fragment {
    @InjectView(R.id.iv_photo) PhotoView photoView;
    @InjectView(R.id.progress) View progreso;

    private ListMediaActivity padre;
    private DecryptControllerSeeMedia controller;
    private int imageid;
    private  PhotoViewAttacher mAttacher = null;

    public ImageViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_imagesegureview, container, false);
        ButterKnife.inject(this, V);
        ViewCompat.setTransitionName(photoView, getString(R.string.image_grid));

        imageid=getArguments().getInt("imageID");
        padre = ((ListMediaActivity) getActivity());

        if (  (0 <= imageid)  && (imageid <padre.getCantimages())){
            SetBitmapTask task  = new SetBitmapTask();
            task.execute(imageid);
        }

        return V;
    }



    private class SetBitmapTask extends AsyncTask<Integer, Void, Bitmap> {

        public SetBitmapTask( ){
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            try{
                return padre.getDecryptMediaController().getImage(imageid);
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            try {
                if (result != null){
                    progreso.setVisibility(View.GONE);
                    photoView.setImageBitmap(result);

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mAttacher  = new PhotoViewAttacher(photoView);
                        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
                    }
                }

            }catch (Exception e){

            }

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // photoView.destroyDrawingCache();
       // photoView.setImageBitmap(null);
        if (mAttacher!= null)
            mAttacher.cleanup();
    }



    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {
        @Override
        public void onPhotoTap(View view, float x, float y) {
            ListMediaActivity padre = (ListMediaActivity) getActivity();
            padre.regularBar();
        }
    }



}
