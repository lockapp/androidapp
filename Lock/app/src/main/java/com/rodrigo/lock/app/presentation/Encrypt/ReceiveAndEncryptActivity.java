package com.rodrigo.lock.app.presentation.Encrypt;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.rodrigo.lock.app.Core.Clases.Accion;
import com.rodrigo.lock.app.Core.Clases.Archivo;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.Core.Clases.FileType;
import com.rodrigo.lock.app.Core.Interfaces.IPreferences;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorCrypto;
import com.rodrigo.lock.app.Core.Manejadores.ManejadorFile;
import com.rodrigo.lock.app.Core.Utils.MediaUtils;
import com.rodrigo.lock.app.Core.controllers.FileController;
import com.rodrigo.lock.app.Core.controllers.PreferencesController;
import com.rodrigo.lock.app.Core.controllers.crypto.CryptoController;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.DecryptActivity;
import com.rodrigo.lock.app.presentation.UI.TextureVideoView;
import com.rodrigo.lock.app.services.ExtractService;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class ReceiveAndEncryptActivity extends ActionBarActivity  implements ObservableScrollViewCallbacks, DatePickerDialog.OnDateSetListener {
/*
    @InjectView(R.id.textureView)
    com.rodrigo.lock.app.presentation.UI.TextureVideoView videoVew;
*/
    @InjectView(R.id.bg)
    ImageView bg;

    @InjectView(R.id.bgfondo)
    ImageView bgfondo;

    @InjectView(R.id.fondoconimgen)
    FrameLayout fondoconimgen;

    Handler mHandler= new Handler();


    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    @InjectView(R.id.toolbar) Toolbar  mToolbar;
   // @InjectView(R.id.image) View mImageView;
    @InjectView(R.id.overlay) View mOverlayView;
    @InjectView(R.id.scroll) ObservableScrollView mScrollView;
    @InjectView(R.id.title) TextView mTitleView;
    int mActionBarSize;
    int mFlexibleSpaceShowFabOffset;
    int mFlexibleSpaceImageHeight;
    int mFabMargin;
    int mToolbarColor;
    boolean mFabIsShown;


    FileController controler;
    FileHeader cabezal;

    /** config **/
    @InjectView(R.id.fecha)   TextView fecha;
    @InjectView(R.id.cifrar)
    Switch cifrar;
    @InjectView(R.id.vencimiento)   Switch vencimiento;
    @InjectView(R.id.layout_vencimiento)
    LinearLayout layout_vencimiento;
    @InjectView(R.id.soloaca)   Switch soloaca;
    @InjectView(R.id.prhoibirextraer)   Switch prhoibirextraer;
    @InjectView(R.id.dejarcopiasinbloquear)   Switch dejarcopiasinbloquear;

    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.password2)
    EditText password2;
    @InjectView(R.id.bg_passwords)
    LinearLayout bg_passwords;

    DatePickerDialog datePickerDialog = null;
    public static final String DATEPICKER_TAG = "datepicker";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controler = ManejadorFile.createControler(getApplicationContext());
        encontrAraccion();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecrrypt_temp);

        if (controler.getAccion() == Accion.EncryptarConImagen || controler.getAccion() == Accion.Encyptar) {
            ButterKnife.inject(this);
            setSupportActionBar(mToolbar);

            initOpcions(savedInstanceState);


            mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
            mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
            mActionBarSize = getActionBarSize();
            mToolbarColor = getResources().getColor(R.color.bg_primario);

            if (!TOOLBAR_IS_STICKY) {
                mToolbar.setBackgroundColor(Color.TRANSPARENT);
            }

            mScrollView.setScrollViewCallbacks(this);
            mTitleView.setText(getTitle());
            setTitle(null);
            mTitleView.setText(controler.getName());


            ViewTreeObserver vto = mScrollView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);
                }
            });
            //onScrollChanged(2, true,false);

        }

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




    //resuelve si manda a desecryptar o se queda aca y lo encrypta
    void resolverAccion() {
        try {
            controler.resolverAccion();

            if ((controler.getAccion() == Accion.Encyptar) || (controler.getAccion() == Accion.EncryptarConImagen)) {
                //se queda aca
                cabezal = new FileHeader();

            } else {
                Intent i = new Intent(this, DecryptActivity.class);
                i.putExtra("controlerId", controler.getId());
                startActivity(i);
                //finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                    Archivo a = new Archivo(myFile);
                    controler.addFile(a);
                    resolverAccion();
                }

            }
        } else{
            ImagenNoValida(getResources().getString(R.string.error_nofind));
        }
    }



    public void encontrAraccion() {
    }


    public void mostrarError(String error){
        AppMsg appMsg = AppMsg.makeText(this, error,  AppMsg.STYLE_ALERT);
        appMsg.setDuration(8000);
        appMsg.show();
    }


    /*****************************************************************************/
    /**  SCROLL **/
    /*****************************************************************************/

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }




    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //Log.d("--> en scrool", ""+scrollY);
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
        ViewHelper.setTranslationY(fondoconimgen, Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));

        // Scale title text
        float scale = 1 + Math.max(0, Math.min(MAX_TEXT_SCALE_DELTA, (flexibleRange - scrollY) / flexibleRange));
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                setBackgroundAlpha(mToolbar, 1, mToolbarColor);
            } else {
                setBackgroundAlpha(mToolbar, 0, mToolbarColor);
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    /*****************************************************************************/
    /**  encriptar **/
    /*****************************************************************************/
    @OnClick(R.id.bloquear)
    void encrypt() {
        View focusView = null;

        Boolean cancel = false;

        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if (cabezal.isCifrar()) {

            if (TextUtils.isEmpty(pass1)) {
                cancel = true;
                focusView = password1;
                password1.setError(getResources().getString(R.string.empty_password));
            }

            if (TextUtils.isEmpty(pass2)) {
                cancel = true;
                focusView = password2;
                password2.setError(getResources().getString(R.string.re_password));
            }


            if (!pass1.equals(pass2) && !cancel) {
                cancel = true;
                focusView = password1;
                password1.setError(getResources().getString(R.string.nomatch_password));
                password2.setError(getResources().getString(R.string.nomatch_password));
            }

        }
        try {
            if (!cancel) {
                controler.chequear();
                // padre.showProgress(true);
                controler.setPassword(pass1);
                controler.setCabezal(this.cabezal);

                CryptoController cc = controler.getEncryptController();
                int ccid = ManejadorCrypto.add(cc);

                Intent i = new Intent(this.getApplicationContext(), ExtractService.class);
                i.putExtra("controlerId", ccid);
                this.startService(i);

                ManejadorFile.quitarControldor(controler.getId());
                this.finish();
            }else{
                focusView.requestFocus();
            }

        } catch (Exception e) {
            this.mostrarError(e.getMessage());
        }

    }


    /*****************************************************************************/
    /**  configuracion **/
    /*****************************************************************************/
    public void initOpcions(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog =DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) this.getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this );
            }
        }

        IPreferences preferencias = PreferencesController.getPreferencesController(this);
        if (preferencias.getEncryptarMismaPassword()) {
            password1.setText(preferencias.getPassword());
            password2.setText(preferencias.getPassword());
        }

        cifrar.setChecked(cabezal.isCifrar());
        vencimiento.setChecked(cabezal.isCaducidad());
        soloaca.setChecked(cabezal.isSoloAca());
        prhoibirextraer.setChecked(cabezal.isProhibirExtraer());
        dejarcopiasinbloquear.setChecked(cabezal.isCopiaSinBloquear());
    }


    @OnCheckedChanged(R.id.cifrar)
    public void setCifrar() {
        if (cifrar.isChecked()){
            this.cabezal.setCifrar(true);
            soloaca.setEnabled(true);
            bg_passwords.setVisibility(View.VISIBLE);
        }else{
            this.cabezal.setCifrar(false);
            soloaca.setEnabled(false);
            soloaca.setChecked(false);
            bg_passwords.setVisibility(View.GONE);
        }
    }

    @OnCheckedChanged(R.id.soloaca)
    public void abrirsoloaca() {
        cabezal.setSoloAca(soloaca.isChecked());
    }


    @OnCheckedChanged(R.id.prhoibirextraer)
    public void setPrhoibirextraer() {
        cabezal.setProhibirExtraer(prhoibirextraer.isChecked());
    }

    @OnCheckedChanged(R.id.dejarcopiasinbloquear)
    public void setDejarcopiasinbloquear() {
        this.cabezal.setCopiaSinBloquear(dejarcopiasinbloquear.isChecked());
    }


    @OnCheckedChanged(R.id.vencimiento)
    public void regularCaducidad() {
        cabezal.setCaducidad(vencimiento.isChecked());
        if (vencimiento.isChecked()) {
            layout_vencimiento.setVisibility(View.VISIBLE);
            cabezal.setCaducidad(true);
            try {
                fecha.setText(cabezal.getFechaCaducidadFormat());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            layout_vencimiento.setVisibility(View.GONE);
            cabezal.setCaducidad(false);
        }
    }




    @OnClick(R.id.vencimiento)
    public void vencimiento() {
        regularCaducidad();
        if (vencimiento.isChecked()) {
            datePickerDialog.setVibrate(false);
            datePickerDialog.setYearRange(2002, 2028);
            //datePickerDialog.setCloseOnSingleTapDay(true);
            datePickerDialog.show(this.getSupportFragmentManager(), DATEPICKER_TAG);

        }
    }

    @OnClick(R.id.fecha)
    public void cambiarFecha() {
        vencimiento();
    }





    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //datePickerDialog.
        month++;
        FileHeader c = cabezal;
        c.setCaducidad(true);
        c.setFechaCaducidad(year, month,day);
        regularCaducidad();
        String vencimiento = day + "/" + month + "/"  + year;
        //Toast.makeText(this, String.format(getResources().getString(R.string.new_expiration), vencimiento), Toast.LENGTH_LONG).show();
    }














}
