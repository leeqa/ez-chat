package com.abidingtech.rednewsapp.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.activity.ChatImageViewActivity;
import com.abidingtech.rednewsapp.callback.ActionCallback;
import com.abidingtech.rednewsapp.callback.ProgressCallback;
import com.abidingtech.rednewsapp.download.CustomFetchListener;
import com.abidingtech.rednewsapp.services.ImageUtil;
import com.abidingtech.rednewsapp.services.Upload;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.abidingtech.rednewsapp.adapter.MessageAdapter.fetchFactory;

public class ImageMessage extends Message {

    private ImageView imageView;
    private ProgressBar progressBar;

    public ImageMessage(Context context) {
        super(context);
    }

    @Override
    public View loadView(ViewGroup viewGroup) {

        final View view = inflater.inflate(R.layout.image_center, null, false);
        progressBar = view.findViewById(R.id.pbImage);
        imageView = view.findViewById(R.id.ivImage);
        llCenter.addView(view);
        return null;
    }

    @Override
    public void loadData() {

        imageView.setOnClickListener(v -> {
            if(chat.isMediaExists()){
                Intent intent = new Intent(context, ChatImageViewActivity.class);
                intent.putExtra("data", new Gson().toJson(chat));
                context.startActivity(intent);
                acquirable.onAcquire(this);
            }

        });
        attachLongPress(imageView);

        if(chat.isMediaExists()){
            showImage(chat.getFilePath());
        }
        else {
            hideImage();
            fetchFactory.addDownloadRequest(chat.id, chat.message_url,IMAGE_DIR);
        }

        fetchFactory.addListener(chat.id, new CustomFetchListener() {
            @Override
            public void onCompleted(@NotNull Download download) {
                Log.e("DOWNLOAD", "COMPLETED_VOICE-> "+download.getIdentifier() +" "+download.getFile());
                showImage(download.getFile());
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
                Log.e("DOWNLOAD", "PROGRESS_VOICE-> "+download.getIdentifier()+"  "+l+"  "+l1 );

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onStart(Download download) {

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
        hideImage();
        chat.media_type_id = IMAGE_TYPE;
        chat.message_url = ImageUtil.compressImage(context,chat.message_url);

        new Upload(context, chat.message_url, Utils.URL + "message/upload_image").multipartFileUpload(new ProgressCallback<String>() {
            @Override
            public void onCompleted(String s) {
                Log.e("UploadFileSuccess", s + "");

                chat.setBody(s);
                saveMessage(new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        try {
                            Utils.copyFile(new File(chat.message_url),new File(MEDIA_PATHS.get(IMAGE_TYPE)+chat.getBody()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showImage(chat.message_url);
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
    private void hideImage(){
        if(!displayView)
            return;
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }

    private void showImage(String path){
        if(!displayView)
            return;
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        Glide.with(context.getApplicationContext())
                .load(path)
                .into(imageView);
    }
}
