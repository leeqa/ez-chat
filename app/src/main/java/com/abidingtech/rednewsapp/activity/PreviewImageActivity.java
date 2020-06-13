package com.abidingtech.rednewsapp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.adapter.SelectedImagesAdapter;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.model.ImageEntity;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class PreviewImageActivity extends AppCompatActivity implements ObjectCallback<Integer> {
    List<Image> imagesList;
    ImageView imageView;
    List<ImageEntity> imageEntities;
    RecyclerView recyclerView;
    ImageView ivDelete;
    ImageView ivAdd;
    FloatingActionButton fabPlayVideo;
    TextView tvSenderName;
    SelectedImagesAdapter adapter;
    FloatingActionButton fabSend;
    int mSelectedImage = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview_layout);
        Intent chat=getIntent();

        imagesList = new ArrayList<>();
        imageEntities=new ArrayList<>();
        imageView=findViewById(R.id.ivPreview);
        fabPlayVideo=findViewById(R.id.fabPlayVideo);
        tvSenderName=findViewById(R.id.tvSenderName);
        ivDelete = findViewById(R.id.ivDeleteImg);
        fabSend=findViewById(R.id.fabSend);
        ivAdd=findViewById(R.id.ivAddImages);
        if(chat!=null) {
            tvSenderName.setText(chat.getStringExtra(ChatView.GROUP_NAME));
        }
        recyclerView = findViewById(R.id.selectedImageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);


        ivAdd.setOnClickListener(view->{
            if(imagesList.size()<=10) {
                showSelectedImages();
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "You have already Seleted maximam", Toast.LENGTH_SHORT).show();
            }

        });
        ivDelete.setOnClickListener(view->{
            ivDeleteCurentImage();
        });
        fabSend.setOnClickListener(task->{
            backToChatACtivityWithResult(imagesList);
            finish();

        });
        fabPlayVideo.setOnClickListener(v->{
            if(mSelectedImage>-1){
                playVideo(imagesList.get(mSelectedImage).getPath());
            }else{
                playVideo(imagesList.get(0).getPath());
            }
        });
        showSelectedImages();

    }

    private void showSelectedImages() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            ArrayList<Image> images = new ArrayList<>();
                            ImagePicker.create(PreviewImageActivity.this)
//                                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                                    .folderMode(true) // folder mode (false by default)
                                    .toolbarFolderTitle("Folder") // folder selection title
                                    .toolbarImageTitle("Tap to select") // image selection title
                                    //.toolbarArrowColor(Color.RED) // Toolbar 'up' arrow color
                                    .includeVideo(true) // Show video on image picker
//                                    .single() // single mode
                                    .multi() // multi mode (default mode)
                                    .limit(10) // max images can be selected (99 by default)
                                    .showCamera(true) // show camera or not (true by default)
                                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                                    .origin(images) // original selected images, used in multi mode
//                                    .exclude(images) // exclude anything that in image.getPath()
//                                    .excludeFiles(files) // same as exclude but using ArrayList<File>
//                                    .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                                    .enableLog(false) // disabling log
                                    .start(); // start image picker activity with request code
//                            Intent intent = new Intent(ChatActivity.this, ImagePickerActivity.class);
//                            intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
//                            intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_MULTIPLE);
//                            intent.putExtra(ImagePicker.EXTRA_LIMIT, 10);
//                            intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, true);
//                            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images);
//                            intent.putExtra(ImagePicker.EXTRA_FOLDER_TITLE, "Album");
//                            intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, "Recent");
//                            intent.putExtra(ImagePicker.EXTRA_IMAGE_DIRECTORY, "Camera");//default is false
//                            Log.e("mytag", "showSelectedImages: " );
//
//                            startActivityForResult(intent, REQUEST_CODE_PICKER);

                            Log.e("mytag", "showSelectedImages: ");
                            //finish();
                        } else {
                            //Toast.makeText(getActivity(), "Allow permissions", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("mytag1", "onActivityResult: ");
        if (resultCode == RESULT_OK) {
            Log.e("mytag1", "onActivityResult: ");

            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                List<Image> list = ImagePicker.getImages(data);
                imagesList.addAll(list);
                if (imagesList != null && imagesList.size() > 0) {
                    Image image = imagesList.get(0);
                    fabPlayVideoVisbility(image.getPath());
                    Glide.with(this).load(image.getPath()).into(imageView);
                    Log.e("video", "onActivityResult: "+image.getPath() );
                    //imageView.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
                    imageEntities.clear();
                    for (Image i:imagesList) {
                        ImageEntity entity = new ImageEntity();
                        entity.img=i;
                        imageEntities.add(entity);
                    }
                    imageEntities.get(0).isSelected=true;
                    adapter = new SelectedImagesAdapter(this,imageEntities, this);
                    recyclerView.setAdapter(adapter);
                    if(!imagesList.isEmpty()){
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        recyclerView.setVisibility(View.GONE);
                    }
                    //finish();
                }
            }
        }else {
            Log.e("mytag1", "onActivityResult: ");
            if(imagesList.isEmpty()) {
                finish();
            }
        }
    }

    private void backToChatACtivityWithResult(List<Image> list) {
        Intent i=new Intent();
        i.putExtra("selectedImage",new Gson().toJson(list));
        setResult(RESULT_OK,i);
    }
    private void showDefaultImage() {
        if (imageEntities.size() > 0) {
            if (adapter != null) {

                //Bitmap bitmap = adapter.convertImageToBitmap(imagesList.get(0));
                Glide.with(this).load(imagesList.get(0).getPath()).into(imageView);
                //imageView.setImageBitmap(bitmap);
            }
        } else {
            backToChatACtivityWithResult(imagesList);
            finish();
        }
    }

    private void ivDeleteCurentImage() {
        if (mSelectedImage > -1 && imageEntities.size() > 0 && mSelectedImage<imageEntities.size()) {
            imageEntities.remove(mSelectedImage);
            imagesList.remove(mSelectedImage);
        } else {
            if (imageEntities.size() > 0) {
                imageEntities.remove(0);
                imagesList.remove(0);
            }
        }
        adapter.notifyDataSetChanged();
        showDefaultImage();
    }


    @Override
    public void onData(Integer img) {
        mSelectedImage = img;
        Bitmap image = adapter.convertImageToBitmap(imagesList.get(img));
        fabPlayVideoVisbility(imagesList.get(img).getPath());
        Glide.with(this).load(imagesList.get(img).getPath()).into(imageView);
        //imageView.setImageBitmap(image);
    }

    private void fabPlayVideoVisbility(String img) {
        if(img.endsWith(".mp4")){
            fabPlayVideo.setVisibility(View.VISIBLE);
        }else{
            fabPlayVideo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String msg) {

    }
    public void playVideo(String path){
        Uri uri=Uri.parse(path);
        Intent playIntent=new Intent(Intent.ACTION_VIEW);
        playIntent.setDataAndType(uri,"video/*");
        startActivity(playIntent);
    }
}
