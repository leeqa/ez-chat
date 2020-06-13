package com.abidingtech.rednewsapp.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abidingtech.rednewsapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

public class Utils {
    public final static int ACTION = 3;
    public final static int TEXT_TYPE = 1;
    public final static int VOICE_TYPE = 2;
    public final static int IMAGE_TYPE = 3;
    public final static int VIDEO_TYPE = 4;

    public static final int PICK_IMAGE = 2223;
    public static final String TYPE_EXTRA_ID = "typeExtraId";
    private static AlertDialog dialog;
    public static final RequestOptions imageRequestOptions = new RequestOptions()
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher);
    public static final RequestOptions clearCacheGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);
    public static String URL = "https://api.rednewstv.tv/public/";

    public static boolean isVideo(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }



    @SuppressLint("SetTextI18n")
    public static void showProgressDialog(Context activity) {

        if (dialog != null && dialog.isShowing())
            return;
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(40, llPadding, llPadding, llPadding);
//        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);
        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(activity);

        tvText.setText("Please wait...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(15);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(ll);

        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public static void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }




    public static void loadImageV1(Context context, String thumbnailUrl, String imageUrl, ImageView iv, int image) {
        Glide.with(context.getApplicationContext()).setDefaultRequestOptions(new RequestOptions()
                .placeholder(image)
                .error(image))
                .load(imageUrl)
                .thumbnail(Glide.with(context.getApplicationContext()).load(thumbnailUrl))
                .into(iv);
    }


    public static void deleteFile(String path) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {

        Log.e("copyFile: ", sourceFile.getAbsolutePath() + "    " + destFile.getAbsolutePath());
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
///storage/emulated/0/Red/voice/2020-04-12_37681664.mp3
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void shareImage(Activity context, String path) {
        Log.e("imagepath", path + "");
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "Share Image"));
    }
    public static void shareContent(Activity context, String path) {
        Log.e("imagepath", path + "");
        Utils.showProgressDialog(context);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            File file = new File(context.getExternalCacheDir(), "logicchip.png");
                            FileOutputStream fOut = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                            fOut.flush();
                            fOut.close();
//            file.setReadable(true, false);
                            Utils.dismissProgressDialog();
                            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(Intent.EXTRA_TEXT, movieName);
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            intent.setType("image/png");

                            context.startActivity(Intent.createChooser(intent, "Share image via"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
////        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        Bitmap bitmap = getBitmapFromView(iv);

    }

    public static void shareVideo(Activity context, String path) {
        Utils.showProgressDialog(context);
        MediaScannerConnection.scanFile(context, new String[]{path},

                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, "Video");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, "Video");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        Utils.dismissProgressDialog();
                        context.startActivity(Intent.createChooser(shareIntent,
                                "share this video"));

                    }
                });
    }

}
