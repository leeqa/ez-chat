package com.abidingtech.rednewsapp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.callback.ActionCallback;
import com.abidingtech.rednewsapp.callback.ProgressCallback;
import com.abidingtech.rednewsapp.download.CustomFetchListener;
import com.abidingtech.rednewsapp.services.Upload;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static com.abidingtech.rednewsapp.adapter.MessageAdapter.fetchFactory;

public class VideoMessage extends Message {

    private ImageView imageView;
    private ProgressBar progressBar;
    private FloatingActionButton btnPlay;

    public VideoMessage(Context context) {
        super(context);
    }

    @Override
    public View loadView(ViewGroup viewGroup) {

        final View view = inflater.inflate(R.layout.video_center, null, false);
        progressBar = view.findViewById(R.id.pbImage);
        imageView = view.findViewById(R.id.ivImage);
        btnPlay = view.findViewById(R.id.fabPlay);
        llCenter.addView(view);
        return null;

    }

    @Override
    public void loadData() {

//        Log.e("TAG", "loadData: " );

        btnPlay.setOnClickListener(v -> {
            Log.e("TAG", "loadData: " +chat.isMediaExists()+"  "+chat.getFilePath());
            if(chat.isMediaExists()){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(chat.getFilePath()), "video/*");
                context.startActivity(intent);
                acquirable.onAcquire(this);
            }

        });

        if(chat.isMediaExists()){
            showVideo(chat.getFilePath());
        }
        else {
            hideVideo();
            fetchFactory.addDownloadRequest(chat.id, chat.message_url,VIDEO_DIR);
        }

        fetchFactory.addListener(chat.id, new CustomFetchListener() {
            @Override
            public void onCompleted(@NotNull Download download) {
                Log.e("DOWNLOAD", "COMPLETED_VOICE-> "+download.getIdentifier() +" "+download.getFile());
                chat.setBody(Uri.parse(chat.message_url).getLastPathSegment());
                showVideo(download.getFile());
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
//        Log.e("TAG", "CreateImage: " );

        if(chat.message_url == null){
            throw new NullPointerException("Message URL cannot be null");
        }
        if(chat.message_url.isEmpty()){
            throw new IllegalStateException("Message URL cannot be empty");
        }
        hideVideo();
        chat.media_type_id = VIDEO_TYPE;
//        chat.message_url = ImageUtil.compressImage(context,chat.message_url);

        new Upload(context, chat.message_url, Utils.URL + "message/upload_video").multipartFileUpload(new ProgressCallback<String>() {
            @Override
            public void onCompleted(String s) {
                Log.e("UploadFileSuccess", s + "");

                chat.setBody(s);
                saveMessage(new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        try {
                            Utils.copyFile(new File(chat.message_url),new File(MEDIA_PATHS.get(VIDEO_TYPE)+chat.getBody()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showVideo(chat.message_url);
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

    }
    private void hideVideo(){
//        Log.e("TAG", "hideImage: " );
        if(!displayView)
            return;
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.GONE);
    }

    private void showVideo(String path){
        if(!displayView)
            return;
//        Log.e("TAG", "showImage: " );
        Glide.with(context.getApplicationContext())
                .load(path)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        btnPlay.setVisibility(View.VISIBLE);

                        return false;
                    }
                })
                .into(imageView);
    }
}
