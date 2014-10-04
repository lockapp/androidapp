package com.rodrigo.lock.app.presentation;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.rodrigo.lock.app.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ErrorActivity extends ActionBarActivity {
    @InjectView(R.id.imageView)
    ImageView imageView;

    @InjectView(R.id.textView2)  TextView descrip;

    AnimationDrawable errorAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.inject(this);

        String descerror = getIntent().getStringExtra("error");
        Log.d("error activity", descerror);

        descrip.setText(descerror);

/*
        imageView.setBackgroundResource(R.drawable.error_animation);
        errorAnimation = (AnimationDrawable) imageView.getBackground();
*/

    }

/*
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        errorAnimation.start();
    }

*/


}
