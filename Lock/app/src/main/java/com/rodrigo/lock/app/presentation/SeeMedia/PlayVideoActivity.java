package com.rodrigo.lock.app.presentation.SeeMedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.MediaController;

import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.presentation.UI.VideoView;

import java.io.File;
import java.io.FileInputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayVideoActivity extends MediaActivity  {

    @InjectView(R.id.videoSurface)
    VideoView videoView;

    MediaController videoController;

    private int imageid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ButterKnife.inject(this);

        String pathfile = getIntent().getExtras().getString("videopath");
        imageid= getIntent().getExtras().getInt("imageID");

        try {
            //player.setAudioStreamType(AudioManager.);
            File file = new File(pathfile);
            FileInputStream inputStream = new FileInputStream(file);

            videoController = new MediaController(this);
            videoController.setAnchorView(videoView);
            videoController.setMediaPlayer(videoView);

            videoView.setMediaController(videoController);
            videoView.setVideoFD(inputStream.getFD());

            videoView.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    boolean back=false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            clearCacheFiles = false;
            deleteMediaController =false;
            Intent i = new Intent(this,ListMediaActivity.class );
            i.putExtra("controlerId", idCC);
            i.putExtra("acutalpage",imageid);
            startActivity(i);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



    ///////////////////////////////////////////////////
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        videoController.show();
        return false;
    }


}
