package com.lock.rodrigo.lock.Encrypt;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.devspark.appmsg.AppMsg;
import com.lock.rodrigo.lock.Core.Clases.Accion;
import com.lock.rodrigo.lock.Core.Clases.InputType;
import com.lock.rodrigo.lock.Core.Controladores.CabezalController;
import com.lock.rodrigo.lock.Core.Controladores.FileController;
import com.lock.rodrigo.lock.Core.Manejadores.ManejadorFile;
import com.lock.rodrigo.lock.DecryptActivity;
import com.lock.rodrigo.lock.R;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;


public class ReceiveAndEncryptActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {

    private Handler mHandler = new Handler();
    private boolean mShowingBack = false;
    //ViewAnimator viewAnimator1;

    FileController controler;
    CabezalController cabezal=null;
    //boolean isImage;


    public FileController getControler() {
        return controler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controler = ManejadorFile.createControler(getApplicationContext());//new FileController();
        encontrAraccion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        //ButterKnife.inject(this);


        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new EncryptFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

    }

    public void ImagenNoValida(String error){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.error_noblock))
                .setMessage(error)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void encontrAraccion() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleImage(intent); // Handle single image being sent
            } else if (type.startsWith("video/")) {
                handleVideo(intent);
            } else {
                handleFile(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent);
            }
        } else {
            handleFile(intent);
        }

    }


    //resuelve si manda a desecryptar o se queda aca y lo encrypta
    void resolverAccion(InputType vinoComo) {
        try {
            controler.resolverAccion(vinoComo);

            if ((controler.getAccion() == Accion.Encyptar) || (controler.getAccion() == Accion.EncryptarConImagen)) {
                //se queda aca
                cabezal = new CabezalController();

            } else {
                Intent i = new Intent(this, DecryptActivity.class);
                i.putExtra("controlerId", controler.getId());
                startActivity(i);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void handleImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String dir = getImagesRealPathFromURI(this, imageUri);
            if (TextUtils.isEmpty(dir)){
                ImagenNoValida(getResources().getString(R.string.error_nofind));
            }else{
                File f = new File(dir);
                controler.addFile(f);
                resolverAccion(InputType.Image);
            }

        }
    }


    void handleVideo(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String dir = getMediaFilePathFromUri(imageUri, this);
            if (TextUtils.isEmpty(dir)){
                ImagenNoValida(getResources().getString(R.string.error_nofind));
            }else{
                File f = new File(dir);
                controler.addFile(f);
                resolverAccion(InputType.Video);
            }

        }
    }


    void handleFile(Intent intent) {
        Uri returnUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if(returnUri == null)
            returnUri = (Uri) intent.getData();

        if (returnUri != null) {
            String path = returnUri.toString().substring(returnUri.toString().indexOf(":///")+ 4);

            if (TextUtils.isEmpty(path)){
                ImagenNoValida(getResources().getString(R.string.error_nofind));

            }else {
                File myFile = new File(path);

                if (!myFile.exists())
                    myFile = new File( path.replaceAll("%20", " "));

                if (!myFile.exists()){
                    ImagenNoValida(getResources().getString(R.string.error_nofind));
                }else{
                    //File f = new File(dir);
                    controler.addFile(myFile);
                    resolverAccion(InputType.File);
                }

            }
        } else{
            ImagenNoValida(getResources().getString(R.string.error_nofind));
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
            for (Uri u : imageUris) {
                if (u != null) {
                    String dir = getImagesRealPathFromURI(this, u);
                    if (TextUtils.isEmpty(dir)){
                        ImagenNoValida(getResources().getString(R.string.error_nofind));
                        return;
                    } else{
                        File f = new File(dir);
                        controler.addFile(f);
                    }

                }
            }
            resolverAccion(InputType.Images);


        }
    }

    public String getImagesRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private String getMediaFilePathFromUri(Uri selectedVideoUri, Context context) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor =  context.getContentResolver().query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

////////////////////////////////////////////////////////////

    private void flipCard() {
//        NowLayout.setHabilitado(false);

        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.
        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.container, new EncryptConfigFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();





        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }


    public void girar() {
        flipCard();
        // AnimationFactory.flipTransition(viewAnimator1, AnimationFactory.FlipDirection.LEFT_RIGHT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Add either a "photo" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_addtogallery, Menu.NONE,
                mShowingBack
                        ? getResources().getString(R.string.app_name)
                        : getResources().getString(R.string.action_settings)
        );
        item.setIcon(mShowingBack
                ? R.drawable.ic_action_back
                : R.drawable.ic_action_settings);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        setTitle(mShowingBack ?  getResources().getString(R.string.action_settings):  getResources().getString(R.string.app_name));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, CarouselActivity.class));
                return true;
*/
            case R.id.action_addtogallery:
                flipCard();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


//////
    public CabezalController getCabezal() {
        return cabezal;
    }

    public void mostrarError(String error){
        AppMsg appMsg = AppMsg.makeText(this, error,  AppMsg.STYLE_ALERT);
        appMsg.setDuration(8000);
        appMsg.show();
    }
}
