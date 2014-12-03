package com.rodrigo.lock.app.presentation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.devspark.appmsg.AppMsg;
import com.rodrigo.lock.app.Core.Interfaces.IPreferences;
import com.rodrigo.lock.app.Core.controllers.PreferencesController;
import com.rodrigo.lock.app.R;

//import org.jraf.android.backport.switchwidget.Switch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class ConfigActivity extends ActionBarActivity {


    @InjectView(R.id.enc_mismapassword)
    Switch enc_mismapassword;
    @InjectView(R.id.enc_layoutpass)
    LinearLayout enc_layoutpass;
    @InjectView(R.id.enc_pass1)
    EditText enc_pass1;
    @InjectView(R.id.enc_pass2)
    EditText enc_pass2;



    IPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.inject(this);

        pref = PreferencesController.getPreferencesController(this);
        initVista();
    }


    private void initVista() {
        enc_mismapassword.setChecked(pref.getEncryptarMismaPassword());

        if (pref.getEncryptarMismaPassword()) {
            enc_pass1.setText(pref.getPassword());
            enc_pass2.setText(pref.getPassword());
        }

        visiblePassword();
    }


    @OnClick(R.id.save)
    void save() {

        Boolean cancel = false;
        String pass1 = null;
        String pass2;
        if (enc_mismapassword.isChecked()) {
            pass1 = enc_pass1.getText().toString();
            pass2 = enc_pass2.getText().toString();

            if (TextUtils.isEmpty(pass1)) {
                cancel = true;
                enc_pass1.setError( getResources().getString(R.string.empty_password));
            }

            if (TextUtils.isEmpty(pass2)) {
                cancel = true;
                enc_pass2.setError(getResources().getString(R.string.re_password));
            }


            if (!pass1.equals(pass2) && !cancel) {
                cancel = true;
                enc_pass1.setError(getResources().getString(R.string.nomatch_password));
                enc_pass2.setError(getResources().getString(R.string.nomatch_password));
            }
        }


        if (!cancel) {
            pref.setEncryptarMismaPassword(enc_mismapassword.isChecked());
            if (enc_mismapassword.isChecked()) {
                pref.setPassword(pass1);
            }

            finish();
        }


    }

    @OnCheckedChanged(R.id.enc_mismapassword)
    public void visiblePassword() {
        if (enc_mismapassword.isChecked()) {
            enc_layoutpass.setVisibility(View.VISIBLE);
        } else {
            enc_layoutpass.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.cancel)
    void cancel() {
        this.finish();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            pref.setEncryptarMismaPassword(pref.isDefaultEncryptarMismaPassword());

            initVista();
            saveMensaje();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

*/


    public void saveMensaje(){
        AppMsg appMsg = AppMsg.makeText(this, getResources().getString(R.string.save_changes),  AppMsg.STYLE_INFO);
        appMsg.setDuration(5000);
        appMsg.show();
    }


}
