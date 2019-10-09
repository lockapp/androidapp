package com.rodrigo.lock.app.mvp.UI;


import android.content.Context;
import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: Rodrigo
 * Date: 19/12/13
 * Time: 0:56
 * To change this template use File | Settings | File Templates.
 */
public class PasswordEditText extends EditText {

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PasswordEditText(Context context) {
        super(context);
        init();
    }

    private void init(){
        this.setTypeface(Typeface.DEFAULT);
        this.setTransformationMethod(new PasswordTransformationMethod());
    }


}
