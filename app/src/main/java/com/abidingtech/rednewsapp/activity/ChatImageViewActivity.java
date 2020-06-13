package com.abidingtech.rednewsapp.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.model.Chat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;


public class ChatImageViewActivity extends AppCompatActivity {
    ImageView img;
    Toolbar toolbar;
    LinearLayout llLayout;
    boolean isImageFitToScreen;
    Chat chat;
    String path;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_image_view);
        img = findViewById(R.id.img);
        toolbar = findViewById(R.id.toolbar);
        llLayout = findViewById(R.id.llLayout);
        chat = new Gson().fromJson(getIntent().getStringExtra("data"), Chat.class);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(chat.getFriendlyTime());

            getSupportActionBar().setTitle(chat.getSenderName());
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        Glide.with(this).setDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image))
                .load(chat.getFilePath())
                .into(img);
        llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isImageFitToScreen) {
                    isImageFitToScreen = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        llLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    }


                } else {
                    isImageFitToScreen = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        llLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forward_imageview, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_forward) {



            return true;
        }
        if (id == R.id.action_share) {
//            String folderName = chat.isOwnMessage() ? "redchat/sent/" : "redchat/receive/";

 /*           if (chat.media_type_id == Utils.TEXT_TYPE) {
                String shareBody = chat.getBody();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
//                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share image"));
            }*/
            Utils.shareImage(this, chat.getFilePath());

/*            else if (chat.media_type_id == Utils.IMAGE_TYPE) {
                String localPath = Environment.getExternalStorageDirectory() + File.separator + folderName + chat.getMessage_url();
                Utils.shareContent(this, localPath);
            } else if (chat.media_type_id == Utils.VIDEO_TYPE) {
                String localPath = Environment.getExternalStorageDirectory() + File.separator + folderName + chat.getMessage_url();
                Utils.shareVideo(this, localPath);

            }*/

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
