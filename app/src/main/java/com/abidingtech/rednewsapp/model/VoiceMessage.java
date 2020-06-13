package com.abidingtech.rednewsapp.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.callback.ActionCallback;
import com.abidingtech.rednewsapp.callback.ProgressCallback;
import com.abidingtech.rednewsapp.download.CustomFetchListener;
import com.abidingtech.rednewsapp.services.Upload;
import com.bumptech.glide.Glide;
import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.abidingtech.rednewsapp.adapter.MessageAdapter.fetchFactory;

public class VoiceMessage extends Message {

    private  AudioPlayMessage audioPlayMessage;
    ImageView ivProfile;
    LinearLayout llAudioMessage;

    public VoiceMessage(Context context) {
        super(context);
    }

    @Override
    public View loadView(ViewGroup viewGroup) {

        final View view = inflater.inflate(R.layout.voice_center, null, false);

        audioPlayMessage = new AudioPlayMessage(context);
        audioPlayMessage.ivPlayPause=view.findViewById(R.id.ivAudioMessage);
        ivProfile=view.findViewById(R.id.civMessageProfile);
        audioPlayMessage.tvTotalTime=view.findViewById(R.id.tvTotalPlayTime);
        //audioPlayMessage.setPath(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString());
        audioPlayMessage.progressBar=view.findViewById(R.id.pdloadMessage);
        audioPlayMessage.seekBar=view.findViewById(R.id.pdPlayMessage);
        llAudioMessage=view.findViewById(R.id.llAudioMessage);
//        audioPlayMessage.setIvPlayPauseListener(audioPlayMessage.ivPlayPause);
        audioPlayMessage.ivPlayPause.setOnClickListener(v -> {
            if(!audioPlayMessage.isPlaying()){
                acquirable.onAcquire(this);
            }
            else {
                acquirable.onAcquire(null);
            }
            audioPlayMessage.playAudio();
        });
        //shimmer = view.findViewById(R.id.voice_shimmer_layout);
        llCenter.addView(view);
        return null;
    }

    @Override
    public void loadData() {


        if(chat.getProfileImage() != null){
            Glide.with(context).load(chat.getProfileImage()).into(ivProfile);
        }
        else {
            Glide.with(context).load(R.drawable.profile).into(ivProfile);
        }Utils.loadImageV1(context,"",chat.getProfileImage(),ivProfile,R.drawable.profile);
        if(chat.isMediaExists()){
            makePlayable(chat.getFilePath());
        }
        else {
            audioPlayMessage.showProgress();
            fetchFactory.addDownloadRequest(chat.id, chat.message_url,VOICE_DIR);
        }
        fetchFactory.addListener(chat.id, new CustomFetchListener() {
            @Override
            public void onCompleted(@NotNull Download download) {
                Log.e("DOWNLOAD", "COMPLETED_VOICE-> "+download.getIdentifier() +" "+download.getFile());
                makePlayable(download.getFile());
            }

            @Override
            public void onStart(Download download) {
                audioPlayMessage.showProgress();
            }

            @Override
            public void onPaused(@NotNull Download download) {
                audioPlayMessage.showProgress();
            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
                Log.e("DOWNLOAD", "PROGRESS_VOICE-> "+download.getIdentifier()+"  "+l+"  "+l1 );
               audioPlayMessage.progressBar.setProgress((int) l);

            }

            @Override
            public void onResumed(@NotNull Download download) {
                audioPlayMessage.showProgress();
            }
        });

    }

    @Override
    protected void create() {

        if(chat.message_url == null){
            throw new NullPointerException("Message URL cannot be null");
        }
        if(chat.message_url.isEmpty()){
            throw new IllegalStateException("Message URL cannot be empty");
        }
        chat.media_type_id = VOICE_TYPE;

        new Upload(context, chat.message_url, Utils.URL + "message/upload_audio").multipartFileUpload(new ProgressCallback<String>() {
            @Override
            public void onCompleted(String s) {
                Log.e("UploadFileSuccess", s + "");
                chat.setBody(s);
                try {
                    Utils.copyFile(new File(chat.message_url),new File(MEDIA_PATHS.get(VOICE_TYPE)+chat.getBody()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Utils.deleteFile(chat.message_url);
                saveMessage(new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        makePlayable(chat.message_url);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
            }

            @Override
            public void onProgress(double progress) {


            }

            @Override
            public void onError(String msg) {
                Log.e("Upload", msg + "");

            }
        });

/*        ChatDAO.getInstance(context).createMessage(chat, new ObjectCallback<Chat>() {
            @Override
            public void onData(Chat chat1) {
                chat.pendingUpload = false;
                ivStatus.setImageResource(R.drawable.ic_check_black_24dp);
            }

            @Override
            public void onError(String msg) {
                ivStatus.setImageResource(R.drawable.ic_error);
            }
        });*/

/*        voicePlayer.setAudio(download.getFile());
        voicePlayer.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);*/

    }
    private void makePlayable(String path){
        if(!displayView)
            return;
        audioPlayMessage.hideProgress();
        audioPlayMessage.setPath(path);
    }

    @Override
    public void stopAndReleaseResources() {
        super.stopAndReleaseResources();
        audioPlayMessage.stop();
    }
}
