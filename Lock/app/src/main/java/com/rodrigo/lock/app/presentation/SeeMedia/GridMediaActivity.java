package com.rodrigo.lock.app.presentation.SeeMedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.HeaderGridView;
import com.rodrigo.lock.app.presentation.UI.scrollActionbar.AlphaForegroundColorSpan;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GridMediaActivity extends MediaActivity {

    @InjectView(R.id.gridview)   HeaderGridView gridView;
    @InjectView(R.id.textheader1)   TextView titulo1;
    @InjectView(R.id.textheader2)   TextView titulo2;
    @InjectView(R.id.tapaheader)   FrameLayout tapaheader;
    @InjectView(R.id.logo)   View logo;
    @InjectView(R.id.progreso)   View progreso;

    MediaAdapter adapter;
    LinkedList<Archivo> archivos;
    int cantimages;


    @InjectView(R.id.header)  View header;
    @InjectView(R.id.toolbar) android.support.v7.widget.Toolbar toolbar;


    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private int mActionBarTitleColor;
    private SpannableString mSpannableString;
    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private View mPlaceHolderView;

    private int mActionBarHeight;
    private TypedValue mTypedValue = new TypedValue();

    private final Object lock = new Object();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_media);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_mediabar);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();

        mPlaceHolderView = getLayoutInflater().inflate(R.layout.fake_header, gridView, false);
        gridView.addHeaderView(mPlaceHolderView);

        archivos = mediaCryptoController.getAbiertos();
        adapter = new MediaAdapter(this);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(getApplicationContext(), "posicion = " + (position-3), Toast.LENGTH_LONG).show();
                viewMedia(parent, v, position, id);
            }
        });


        mActionBarTitleColor = getResources().getColor(R.color.white);
        mSpannableString = new SpannableString(getResources().getString(R.string.secure_view));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);


        setupGridView();

        synchronized (lock) {
            updateText(mediaCryptoController.isComplete());
        }

        tapaheader.setAlpha(0);

    }


    private void setupGridView() {

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                //sticky actionbar
                header.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                //header_logo --> actionbar icon
                float ratio = clamp(header.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                //actionbar title alpha
                //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                //---------------------------------
                //better way thanks to @cyrilmottier
                float alpha = clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F);
                setTitleAlpha(alpha);
                tapaheader.setAlpha(alpha);
               /* titulo1.setAlpha(1.0F-alpha);
                titulo2.setAlpha(1.0F-alpha);*/


            }
        });
    }



    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(mSpannableString);
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }


    public int getScrollY() {
        View c = gridView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = gridView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }



    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }



    public void viewMedia(AdapterView<?> parent, View v, int position, long id){
        clearCacheFiles = false;
        deleteMediaController =false;
        int image = position - 3;

        Intent i = new Intent(this,ListMediaActivity.class );
        i.putExtra(Constants.CRYPTO_CONTROLLER, idCC);
        i.putExtra(Constants.MEDIA_ACTIVITY_OPENFILE,image);
        startActivity(i);

/*
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,  v.findViewById(R.id.picture),  getString(R.string.image_grid));
        ActivityCompat.startActivity(this, i, options.toBundle());
*/
        //finish();


    }


    private  void updateText(boolean fin){
        String titulo;
        if(cantimages == 1){
            titulo =(getResources().getString(R.string.file));
        }else{
            titulo =(String.format(getResources().getString(R.string.files), cantimages));
        }
        if (fin){
            progreso.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
        }else{
            titulo = titulo + " ...";
            progreso.setVisibility(View.VISIBLE);
            logo.setVisibility(View.GONE);
        }
        titulo2.setText(titulo);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mediaCryptoController.esExtraible()){
            getMenuInflater().inflate(R.menu.see_image, menu);
        }else{
            getMenuInflater().inflate(R.menu.see_image_no_extract, menu);
        }

        return true;
    }



    private class MediaAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MediaAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            cantimages =archivos.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return cantimages;
        }




        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            ImageView imageplay;
            Archivo a = archivos.get(i);

            if(v == null) {
                v = inflater.inflate(R.layout.grid_image, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.play, v.findViewById(R.id.play));
            } else{
                if(a.getFile().getAbsolutePath().equals(v.getTag())){
                    // If so, return it directly.
                    return v;
                }
            }

            picture = (ImageView)v.getTag(R.id.picture);
            imageplay = (ImageView)v.getTag(R.id.play);
           // ViewCompat.setTransitionName(picture, getString(R.string.image_grid) );

            picture.setImageBitmap(null);
            if (a.getTipo() == FileType.Video){
                imageplay.setVisibility(View.VISIBLE);

                VideoGetter task = new VideoGetter(picture) ;
                task.execute(a.getFile());
                picture.setTag(task);

            }else{
                imageplay.setVisibility(View.GONE);

                ImageGetter task = new ImageGetter(picture) ;
                task.execute(i);
                picture.setTag(task);
            }

            v.setTag(a.getFile().getAbsolutePath() );
            return v;
        }

    }


    public class ImageGetter extends AsyncTask<Integer, Void, Bitmap> {
        private WeakReference<ImageView> mImageViewRef;

        public ImageGetter(ImageView v) {
            mImageViewRef = new WeakReference<ImageView>(v);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if ( null == mImageViewRef.get() )
                return null;
            else
                return mediaCryptoController.getSmallImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //super.onPostExecute(result);
            ImageView iv = mImageViewRef.get();
            if (iv!= null && result!= null)
                iv.setImageBitmap(result);
        }
    }


    public class VideoGetter extends AsyncTask<File, Void, Bitmap> {
        private WeakReference<ImageView> mImageViewRef;

        public VideoGetter(ImageView v) {
            mImageViewRef = new WeakReference<ImageView>(v);

        }

        @Override
        protected Bitmap doInBackground(File... params) {
            if (null == mImageViewRef.get()){
                return null;
            }else {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(params[0].getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                return thumb;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //super.onPostExecute(result);
            ImageView iv = mImageViewRef.get();
            if (iv!= null && result!= null)
                iv.setImageBitmap(result);

        }
    }




    @Override
    public void notificarCantImages(int cantImages) {
        synchronized (lock) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cantimages = mediaCryptoController.getCantImages();
                    adapter.notifyDataSetChanged();
                    updateText(false);
                }
            });
        }
    }

    @Override
    public void fin() {
        synchronized (lock) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateText(true);
                    //hide progress bar
                }
            });
        }

    }


}
