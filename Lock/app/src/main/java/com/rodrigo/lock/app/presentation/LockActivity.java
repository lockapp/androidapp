package com.rodrigo.lock.app.presentation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.devspark.appmsg.AppMsg;
import com.rodrigo.lock.app.R;

import butterknife.InjectView;
import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;

/**
 * Created by Rodrigo on 02/06/14.
 */
public class LockActivity extends ActionBarActivity {

    public @InjectView(R.id.mLoginFormView)    View mLoginFormView;
    @InjectView(R.id.progress)    RelativeLayout mProgressView;

    static class CancelAppMsg implements View.OnClickListener {
        private final AppMsg mAppMsg;

        CancelAppMsg(AppMsg appMsg) {
            mAppMsg = appMsg;
        }

        @Override
        public void onClick(View v) {
            mAppMsg.cancel();
        }
    }

    public void mostrarError(String error){
        final AppMsg.Style style = new AppMsg.Style(LENGTH_SHORT, R.color.color_error);
        AppMsg appMsg = AppMsg.makeText(this, error,  style);
        //appMsg.setAnimation(android.R.anim.fade_in, android.R.anim.slide_out_right);
        appMsg.setDuration(8000);
        appMsg.show();

       /* AppMsg appMsg = AppMsg.makeText(this, error,  AppMsg.STYLE_ALERT);
        appMsg.setDuration(8000);
        appMsg.show();*/
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
