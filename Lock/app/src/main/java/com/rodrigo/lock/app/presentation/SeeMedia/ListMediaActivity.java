package com.rodrigo.lock.app.presentation.SeeMedia;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.Core.Utils.HackyViewPager;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.DepthPageTransformer;
import com.rodrigo.lock.app.services.ExtractService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import java.util.ArrayList;
import java.util.List;

public class ListMediaActivity extends MediaActivity {
    @InjectView(R.id.view_pager)  HackyViewPager mViewPager;

    private MediaPagerAdapter adapter;
    private static final String ISLOCKED_ARG = "isLocked";
    private boolean HIDE_BAR = false;
    private int cantimages;

    public int getCantimages() {
        return cantimages;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
       // setSupportProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_see_image);
        ButterKnife.inject(this);



        //se crea el adapter
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        adapter =  new MediaPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

        //se muestra la barra y se setea el clic para que se esconda muestre
        showSystemUI();

        //se pone la pagina actual
        int actualp = getIntent().getExtras().getInt("acutalpage",-1);
        if (actualp!=-1)
            mViewPager.setCurrentItem(actualp);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //se crea la tarea que desencripta
        setTitle(mediaCryptoController.isComplete());
    }



    private void setTitle(boolean fin){
        String titulo;
        if(cantimages == 1){
            titulo =(getResources().getString(R.string.file));
        }else{
            titulo =(String.format(getResources().getString(R.string.files), cantimages));
        }
        if (!fin){
            titulo = titulo + " ...";
        }
        setTitle(titulo);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            clearCacheFiles = false;
            deleteMediaController =false;
            Intent i = new Intent(this,GridMediaActivity.class );
            i.putExtra("controlerId", idCC);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }





    public class MediaPagerAdapter extends FragmentStatePagerAdapter {

        public MediaPagerAdapter(FragmentManager fm) {
           super(fm);
           cantimages = mediaCryptoController.getCantImages();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            if (mediaCryptoController.getFileType(position)== FileType.Imagen ){
                f=  new ImageViewFragment();
            } else{
                f=  new VideoViweFragment();
            }

            Bundle args = new Bundle();
            args.putInt("imageID", position);
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return cantimages;
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_image, menu);
        return true;
    }



    @OnClick(R.id.mLoginFormView)
    public void regularBar(){
        HIDE_BAR = !HIDE_BAR;
        if (HIDE_BAR) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }



    // This snippet hides the system bars.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                          View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                          | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                          | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showSystemUI() {
      getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }





    public DecryptControllerSeeMedia getMediaController() {
        return mediaCryptoController;
    }


    @Override
    public synchronized void notificarCantImages(int cantImages) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cantimages = mediaCryptoController.getCantImages();
                adapter.notifyDataSetChanged();
                setTitle(false);
            }
        });
    }

    @Override
    public synchronized void fin() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // setSupportProgressBarIndeterminateVisibility(false);
                setTitle(true);
            }
        });
    }



}
