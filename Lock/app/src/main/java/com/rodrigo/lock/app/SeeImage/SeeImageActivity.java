package com.rodrigo.lock.app.SeeImage;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


import com.rodrigo.lock.app.Core.crypto.DecryptControllerSeeImage;
import com.rodrigo.lock.app.Core.Controladores.FileController;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.Utils.HackyViewPager;
import com.rodrigo.lock.app.LockActivity;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.UI.DepthPageTransformer;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.util.ArrayList;
import java.util.List;

public class SeeImageActivity extends LockActivity {
    private static final String ISLOCKED_ARG = "isLocked";
    @InjectView(R.id.view_pager)  HackyViewPager mViewPager;
    private boolean HIDE_BAR = false;

    private DecryptControllerSeeImage controllerImage;
    private FileController fc;
    private int cantimages = 1;
    ImagePagerAdapter adapter;
    DesencriptarAsincrona task = null;


    public DecryptControllerSeeImage getControllerImage() {
        return controllerImage;
    }

    // @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_see_image);
        ButterKnife.inject(this);

        int idC = getIntent().getExtras().getInt("controlerId");
        fc =ManejadorFile.getControlador(idC);
        this.controllerImage = (DecryptControllerSeeImage)fc.getCryptoController();

        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        adapter =  new ImagePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

        showSystemUI();


        if (!this.controllerImage.isComplete()){
            setSupportProgressBarIndeterminateVisibility(true);
            task = new DesencriptarAsincrona();
            new Thread(task).start();
        }else{
            setSupportProgressBarIndeterminateVisibility(false);
        }
        setTitle();
    }


    private void setTitle(){
        if(cantimages == 1){
            setTitle(getResources().getString(R.string.file));
        }else{
            setTitle(String.format(getResources().getString(R.string.files), cantimages));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task!= null){
            task.setSalir(true);
            task = null;
        }
        if (isFinishing()) {
            controllerImage.delteCache();
            if (! esperaPorService) ManejadorFile.quitarControldor(fc.getId());
        }

    }



    public class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
           super(fm);
           cantimages = controllerImage.getCantImages();
        }

        @Override
        public Fragment getItem(int position) {
            ImageViewFragment f=  new ImageViewFragment();
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








    boolean esperaPorService=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addtogallery) {
            try {
                esperaPorService = true;
                fc.setCryptoController(null);
                fc.convertirenService(this);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        }else if (id ==R.id.action_share){
            Intent sendIntent = shareExludingApp();
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }
    }



    public  Intent shareExludingApp ()  {
        String packageNameToExclude =this.getPackageName();
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = createShareIntent();
        List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(createShareIntent(),0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                Intent targetedShare = createShareIntent();

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


    private    Intent createShareIntent ()  {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/zip");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fc.getInFiles().getFirst()));
        return share ;
    }



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




    private class DesencriptarAsincrona implements Runnable {
        String error = null;
        boolean salir = false;

        public void setSalir(boolean salir) {
            this.salir = salir;
        }

        @Override
        public void run() {
            try {
                while (!controllerImage.isComplete() && !salir) {
                    controllerImage.loadImage();
                    SeeImageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cantimages = controllerImage.getCantImages();
                            adapter.notifyDataSetChanged();
                            setTitle();
                        }
                    });
                }

            } catch (Exception e) {
                error = "Error: " + e.getMessage();
            }


            SeeImageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error != null) {
                        mostrarError(error);
                    } else if (!salir){
                        //Toast.makeText(SeeImageActivity.this, getResources().getString(R.string.open_all), Toast.LENGTH_LONG).show();
                        setSupportProgressBarIndeterminateVisibility(false);
                    }
                }
            });
        }

    }




}
