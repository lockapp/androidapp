package com.rodrigo.lock.app.presentation.UI;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.rodrigo.lock.app.R;

/**
 * Created by Rodrigo on 09/12/2014.
 */
public class ViewUtils {

    public static void feedOutView(final View view, Context appContext){
        Animation fadeOut = AnimationUtils.loadAnimation(appContext,  R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Called when the Animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Called when the Animation ended
                // Since we are fading a View out we set the visibility
                // to GONE once the Animation is finished
                 view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This is called each time the Animation repeats
            }
        });
        view.startAnimation(fadeOut);
    }

}
