package com.rodrigo.lock.app.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.scrollActionbar.AlphaForegroundColorSpan;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends ActionBarActivity  implements ObservableScrollViewCallbacks {

   @InjectView(R.id.version) TextView version;
    @InjectView(R.id.r0) TextView r0;



    @InjectView(R.id.header) View mImageView;
    @InjectView(R.id.toolbar) Toolbar mToolbarView;
    int mParallaxImageHeight;

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private int mActionBarTitleColor;
    private SpannableString mSpannableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //inicializa el titulo del action bar
        mActionBarTitleColor = getResources().getColor(R.color.white);
        mSpannableString = new SpannableString(getString(R.string.app_name));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);
        setTitleAlpha(0.0F);

        setSupportActionBar(mToolbarView);
        setBackgroundAlpha(mToolbarView, 0, getResources().getColor(R.color.bg_primario));

        if (isFirstTime()){
            tutorial();
        }

        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        String versionName="";
        try {
            versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText(versionName);

        //respuesta 0
        r0.setMovementMethod(LinkMovementMethod.getInstance());
        r0.setText(Html.fromHtml(getString(R.string.r0)));






    }

    @OnClick(R.id.tutorial)
    public void tutorial(){
        startActivity(new Intent(this, InstructionsActivity.class));
    }

    @OnClick(R.id.donar)
    public void donarl() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=7R9PXAXWHZ8HU"));
        startActivity(browserIntent);
    }


    @OnClick(R.id.contacto)
    public void Contacto() {
        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"lock.app.android@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "from android");
        i.putExtra(Intent.EXTRA_TEXT   , getResources().getString(R.string.enterhere));
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


/*

public void initToolbar(){
    // Set an OnMenuItemClickListener to handle menu item clicks
    mToolbarView.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                startActivity (new Intent(MainActivity.this, ConfigActivity.class));
            }
            return true;
        }
    });

    // Inflate a menu to be displayed in the toolbar
    mToolbarView.inflateMenu(R.menu.main);
}
*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity (new Intent(this, ConfigActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /***
     * Checks that application runs first time and write flag at SharedPreferences
     * @return true if 1st time
     */
    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }


    private void setTitleAlpha(float alpha) {
        Log.d("set alpa con -->", "" + alpha);
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mToolbarView.setTitle(mSpannableString);

    }

    /**** scrool**/


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.bg_primario);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        setBackgroundAlpha(mToolbarView, alpha, baseColor);
        setTitleAlpha(alpha);
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
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

}
