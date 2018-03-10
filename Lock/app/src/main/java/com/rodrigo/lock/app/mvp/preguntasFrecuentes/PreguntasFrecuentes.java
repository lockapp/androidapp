package com.rodrigo.lock.app.mvp.preguntasFrecuentes;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.rodrigo.lock.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreguntasFrecuentes extends AppCompatActivity {

    @BindView(R.id.r0)
    TextView r0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preguntas_frecuentes_activity);
        ButterKnife.bind(this);


        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        r0.setMovementMethod(LinkMovementMethod.getInstance());
        r0.setText(Html.fromHtml(getString(R.string.r00)));

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
