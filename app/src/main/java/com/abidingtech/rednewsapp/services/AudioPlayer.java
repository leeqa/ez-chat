package com.abidingtech.rednewsapp.services;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.abidingtech.rednewsapp.R;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class AudioPlayer extends LinearLayout {
    private static final int PAUSE_ICON = R.drawable.ic_pause_black_24dp;
    private final int PLAY_ICON = R.drawable.ic_play_arrow_black_24dp;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    public ImageView ivPlayPause;
    private SeekBar seekBar;
    public ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private String path;
    private Context context;
    private boolean isLoaded = false;


    public AudioPlayer(Context context) {
        super(context);
        init(context);
        Log.e("AUDIO", "Context Only");
    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        Log.e("AUDIO", "Context + attr");

    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

        Log.e("AUDIO", "Context + style + def");

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

        Log.e("AUDIO", "Context + attr + style + res");
    }

    public void init(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.voice_message, null);

        ivPlayPause = view.findViewById(R.id.iv_play);
        seekBar = view.findViewById(R.id.seekBar);
        progressBar = view.findViewById(R.id.progress_bar);

        ivPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoaded) {
                    showProgress();
                }
                playAudio();
            }
        });

        addView(view);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /////Helper Methods/////////
    public void playAudio() {
        if (path == null) {
            hideProgress();
            Toasty.info(context, "Audio path not set", Toast.LENGTH_SHORT, true).show();

            return;
        }
        if (mediaPlayer.isPlaying()) {
            pause();
            ivPlayPause.setImageResource(PLAY_ICON);
        } else {
            try {
                if (!isLoaded) {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    seekBar.setMax(mediaPlayer.getDuration());
                    isLoaded = true;
                }
                seekBarCountdown();
                mediaPlayer.start();
                ivPlayPause.setImageResource(PAUSE_ICON);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Log.e("onstope", "true");

        try {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.seekTo(0);
                isLoaded = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seekBarCountdown() {
        countDownTimer = new CountDownTimer(mediaPlayer.getDuration(), 1000) {
            public void onTick(long millisUntilFinished) {
                if (context instanceof Activity && ((Activity) context).isFinishing()) {
                    stop();
                }
                if (mediaPlayer.isPlaying() && ivPlayPause.getVisibility() != View.VISIBLE) {
                    hideProgress();
                }
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                if (mediaPlayer.getDuration() == mediaPlayer.getCurrentPosition()) {
                    countDownTimer.cancel();
                }
            }

            public void onFinish() {
            }

        }.start();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer.isPlaying() && fromUser) {
                    seekBar.setProgress(progress);
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pause();
                seekBar.setProgress(0);
                ivPlayPause.setImageResource(PLAY_ICON);
            }
        });
    }

    public void showProgress() {
        ivPlayPause.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public void hideProgress() {
        ivPlayPause.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }
}
