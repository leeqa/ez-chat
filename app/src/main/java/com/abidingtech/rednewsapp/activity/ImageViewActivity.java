package com.abidingtech.rednewsapp.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;


public class ImageViewActivity extends AppCompatActivity {
    ImageView displayImg;
    Toolbar toolbar;
    String path;
    String displayImage, displayImageThumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        displayImg = findViewById(R.id.displayImg);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        displayImage = getIntent().getStringExtra("displayImg");
        displayImageThumb = getIntent().getStringExtra("displayImgThumb");

        Utils.loadImageV1(this, displayImageThumb, displayImage, displayImg,R.drawable.ic_image);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }


}
