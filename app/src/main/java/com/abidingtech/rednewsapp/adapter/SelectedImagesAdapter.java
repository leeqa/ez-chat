package com.abidingtech.rednewsapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.model.ImageEntity;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.model.Image;

import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.SelectedViewHolder> {
    Context mContext;
    List<ImageEntity> imageList;
    ObjectCallback<Integer> callback;

    public SelectedImagesAdapter(Context mContext, List<ImageEntity> imageList,ObjectCallback<Integer> callback) {
        this.mContext = mContext;
        this.imageList = imageList;
        this.callback=callback;
        Log.e("mytag", "SelectedImagesAdapter: "+imageList.size() );
    }

    @NonNull
    @Override
    public SelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.selected_image_item, parent, false);
        Log.e("mytag", "onCreateViewHolder: "+imageList.size() );
        return new SelectedViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedViewHolder holder, final int position) {

         Image image = imageList.get(position).img;
         //Bitmap bitmapImage =convertImageToBitmap(image);
        //Log.e("mytag",image.getName()+" "+image.getPath());
        Glide.with(mContext).load(image.getPath()).into(holder.imageView);
        //holder.imageView.setImageBitmap(bitmapImage);

        if(imageList.get(position).isSelected) {
            holder.imageView.setBackgroundResource(R.drawable.img_border);
        }else{
            holder.imageView.setBackgroundResource(R.drawable.non_selected);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<imageList.size();i++){
                    imageList.get(i).isSelected=false;
                }
                imageList.get(position).isSelected=true;
                callback.onData(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    public Bitmap convertImageToBitmap(Image image){
        return BitmapFactory.decodeFile(image.getPath());
    }
    class SelectedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
         public SelectedViewHolder(@NonNull View itemView) {
             super(itemView);
             imageView= itemView.findViewById(R.id.ivTile);
         }
     }
}
