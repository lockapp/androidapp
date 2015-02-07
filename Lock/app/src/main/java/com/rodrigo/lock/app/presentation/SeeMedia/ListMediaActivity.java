package com.rodrigo.lock.app.presentation.SeeMedia;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Utils.HackyViewPager;
import com.rodrigo.lock.app.Core.controllers.crypto.DecryptControllerSeeMedia;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.DepthPageTransformer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ListMediaActivity extends MediaActivity {
    @InjectView(R.id.view_pager)  HackyViewPager mViewPager;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

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
        setContentView(R.layout.activity_see_image);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //posponerTransaccion();


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
        int actualp = getIntent().getExtras().getInt(Constants.MEDIA_ACTIVITY_OPENFILE,-1);
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
            retroceder();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    public void retroceder(){
        clearCacheFiles = false;
        deleteMediaController =false;
        Intent i = new Intent(this,GridMediaActivity.class );
        i.putExtra(Constants.CRYPTO_CONTROLLER, idCC);
        startActivity(i);
        finish();
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
            args.putInt(Constants.SEE_IMAGE_ID, position);
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
        getMenuInflater().inflate(R.menu.donate, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            retroceder();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

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
      /*  getWindow().getDecorView().setSystemUiVisibility(
                          View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                          | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                          | View.SYSTEM_UI_FLAG_IMMERSIVE
        );*/
        getSupportActionBar().hide();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showSystemUI() {
    /*  getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );*/
       getSupportActionBar().show();
    }





    public DecryptControllerSeeMedia getDecryptMediaController() {
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
