package com.rodrigo.lock.app.presentation.SeeMedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.MediaController;

import com.rodrigo.lock.app.Constants;
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
        Log.d("->Playvideo", "On Create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ButterKnife.inject(this);

        String pathfile = getIntent().getExtras().getString(Constants.SEE_VIDEO_PATH);
        imageid= getIntent().getExtras().getInt(Constants.SEE_IMAGE_ID);

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


    @Override
    public void onDestroy() {
        Log.d("->PlayVideo", "On Destroy");
        super.onDestroy();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        videoController.show();
        return false;
    }


}
