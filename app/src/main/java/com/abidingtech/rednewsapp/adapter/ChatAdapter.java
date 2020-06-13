package com.abidingtech.rednewsapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.abidingtech.rednewsapp.services.AudioPlayer;
import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.activity.ChatImageViewActivity;
import com.abidingtech.rednewsapp.callback.BooleanCallback;
import com.abidingtech.rednewsapp.callback.IdCallback;
import com.abidingtech.rednewsapp.download.CustomFetchListener;
import com.abidingtech.rednewsapp.download.FetchFactory;
import com.abidingtech.rednewsapp.model.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ProgrammingViewHolder> implements CustomFetchListener {

    public List<Chat> list;
    private Activity context;
    private int MSG_TYPE_LEFT = 0;
    private int MSG_TYPE_RIGHT = 1;
    private int MSG_TYPE_CENTER = 2;
    private BooleanCallback callback;
    FetchFactory fetchFactory;
    Map<String, ProgressBar> progreesMap = new HashMap<>();
    final Map<String, CoordinatorLayout> clProgreesMap = new HashMap<>();

    private IdCallback selectedListCallback = null;


    public ChatAdapter(Activity context, List<Chat> list, BooleanCallback callback, IdCallback onClickListener) {
        this.list = list;
        this.context = context;
        this.callback = callback;
        fetchFactory = FetchFactory.getInstance(context);
        this.selectedListCallback = onClickListener;

    }

    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            return setLayout(viewGroup, R.layout.chatlist_item_right);

        } else if (viewType == MSG_TYPE_LEFT) {
            return setLayout(viewGroup, R.layout.chatlist_item_left);

        } else {
            return setLayout(viewGroup, R.layout.chatlist_item_center);
        }
    }

    private ProgrammingViewHolder setLayout(ViewGroup viewGroup, int layout) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(layout, viewGroup, false);
        return new ProgrammingViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "RestrictedApi"})
    @Override
    public void onBindViewHolder(@NonNull final ProgrammingViewHolder holder, final int i) {
        final Chat chat = list.get(i);

        if (chat.message_type_id == Utils.ACTION) {
            holder.tvMsgBody.setText(chat.getBody());
        } else {
            holder.tvTimeAgo.setText(chat.getFriendlyTime());
            if (chat.media_type_id != Utils.TEXT_TYPE && !chat.isLocal && !isFileExists(list.get(i).isOwnMessage(), Uri.parse(chat.message_url).getLastPathSegment())) {
                holder.clDownload.setVisibility(View.VISIBLE);
                startDownload(chat.message_url, chat.thumbnail, holder.pbDownload, list.get(i).isOwnMessage(), holder.clDownload);

            } else {
                holder.clDownload.setVisibility(View.GONE);
            }
            setChat(holder, chat);
            holder.fabPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = chat.isOwnMessage() ? Environment.getExternalStorageDirectory() + File.separator + "redchat/sent" + "/" : Environment.getExternalStorageDirectory() + File.separator + "redchat/receive" + "/";
                    String paths = chat.isLocal ? chat.message_url : path + chat.getMessage_url();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(paths), "video/*");
                    context.startActivity(intent);
                }
            });
            holder.ivMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.clDownload.getVisibility() == View.GONE) {

                        if (chat.media_type_id == Utils.IMAGE_TYPE) {
                            Intent intent = new Intent(context, ChatImageViewActivity.class);
                            intent.putExtra("data", new Gson().toJson(chat));
                            context.startActivity(intent);
                        }

                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSelected().size() > 0) {
                        setItemSelection(holder, i);

                    }


                }
            });

            holder.ivForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//

                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getSelected().size() == 0) {
                        setItemSelection(holder, i);
                        return true;
                    }
                    return false;
                }
            });
            holder.ivMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getSelected().size() == 0) {
                        setItemSelection(holder, i);
                        return true;
                    }
                    return false;
                }
            });

            selectChecked(holder.itemView, chat.isSelected);
        }


    }


    private void startDownload(String songUrl, String thumb, ProgressBar progressBar, boolean isSent, CoordinatorLayout cl) {
//        Toast.makeText(context, Uri.parse(songUrl).getLastPathSegment() + " download starts", Toast.LENGTH_SHORT).show();
        final int[] requestId = new int[1];
        requestId[0] = fetchFactory.addDownloadRequest(songUrl, isSent, false);
        if (thumb != null) {

            requestId[0] = fetchFactory.addDownloadRequest(thumb, isSent, true);
        }
        progreesMap.put(String.valueOf(requestId[0]), progressBar);
        clProgreesMap.put(String.valueOf(requestId[0]), cl);
        Log.e("mytag", "onClick: => mapsize" + clProgreesMap.size());
    }

    @Override
    public void onCompleted(@NotNull Download download) {
        Log.e("mytag", "onCompleted: => mapsize" + clProgreesMap.size());
//        ProgressBar progressBar=progreesMap.get(download.getUrl());
        CoordinatorLayout progressBar = clProgreesMap.get(String.valueOf(download.getRequest().getId()));
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
//            Toast.makeText(context, String.valueOf(Uri.parse(download.getUrl()).getLastPathSegment()) + "  Completeted", Toast.LENGTH_SHORT).show();
        }
        //holder.songProgress.setVisibility(View.GONE);
//        fetchFactory.removeListener();
    }

    @Override
    public void onPaused(@NotNull Download download) {
        Log.e("mytag", "onPaused: ");
//        Toast.makeText(context, String.valueOf(Uri.parse(download.getUrl()).getLastPathSegment()) + "  pause ", Toast.LENGTH_SHORT).show();
        fetchFactory.pauseDownload(download.getRequest().getId());


    }

    @Override
    public void onProgress(@NotNull Download download, long l, long l1) {
        Log.e("mytag", "onProgress: => mapsize " + clProgreesMap.size());
//        Log.e("mytag", "onProgress: => " + clProgreesMap.size() + download.getProgress() + " " + download.getRequest().getId());
        // ProgressBar progressBar=progreesMap.get(download.getUrl());
        ProgressBar progressBar = progreesMap.get(String.valueOf(download.getRequest().getId()));
        if (progressBar != null) {
//            Toast.makeText(context, String.valueOf(Uri.parse(download.getUrl()).getLastPathSegment()) + "  in progress ", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(100);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(download.getProgress());
        }
    }

    @Override
    public void onResumed(@NotNull Download download) {
        Log.e("mytag", "onResume: ");
//        Toast.makeText(context, String.valueOf(Uri.parse(download.getUrl()).getLastPathSegment()) + "  resume ", Toast.LENGTH_SHORT).show();
        fetchFactory.resumeDownload(download.getRequest().getId());
    }

    @Override
    public void onStart(Download download) {

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProgrammingViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivUser;
        TextView tvUserName, tvMsgBody, tvTimeAgo;
        AudioPlayer audioPlayer;
        ImageView ivMessage, ivTick, ivTime, ivForward;
        ProgressBar pbImage, pbDownload;
        CoordinatorLayout clImage, clDownload;
        FloatingActionButton fabPlay;
        CardView cardView;


        ProgrammingViewHolder(@NonNull View itemView) {
            super(itemView);
//            ivUser = itemView.findViewById(R.id.ivCommentUser);
            audioPlayer = itemView.findViewById(R.id.audioPlayer);
            cardView = itemView.findViewById(R.id.cv);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivTime = itemView.findViewById(R.id.ivTime);
            ivForward = itemView.findViewById(R.id.ivForward);
            ivTick = itemView.findViewById(R.id.ivTick);
            pbImage = itemView.findViewById(R.id.pbImage);
            tvMsgBody = itemView.findViewById(R.id.tvCommentBody);
            fabPlay = itemView.findViewById(R.id.fabPlay);
            ivMessage = itemView.findViewById(R.id.ivImage);
            clDownload = itemView.findViewById(R.id.clDownload);
            pbDownload = itemView.findViewById(R.id.pbDownload);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            clImage = itemView.findViewById(R.id.clImage);

        }

    }


    @Override
    public int getItemViewType(int position) {

        if (list.get(position).isOwnMessage() && list.get(position).message_type_id != Utils.ACTION) {
            return MSG_TYPE_RIGHT;
        }
        if (!list.get(position).isOwnMessage() && list.get(position).message_type_id != Utils.ACTION) {
            return MSG_TYPE_LEFT;
        } else {
            return MSG_TYPE_CENTER;
        }
    }


    private boolean isFileExists(boolean isSent, String fileName) {
        String path = isSent ? "redchat/sent" : "redchat/receive";
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + path + "/" + fileName);
        Log.e("pathexist", path + fileName + " => " + file.exists());
        return file.exists();

    }

    private void setChat(ProgrammingViewHolder holder, Chat chat) {
        String path = chat.isOwnMessage() ? Environment.getExternalStorageDirectory() + File.separator + "redchat/sent" + "/" : Environment.getExternalStorageDirectory() + File.separator + "redchat/receive" + "/";
        String thumbPath = Environment.getExternalStorageDirectory() + File.separator + "redchat/thumb" + "/";
        if (chat.isOwnMessage()) {
            holder.tvUserName.setVisibility(View.GONE);
            if (chat.isUploaded) {
                holder.ivTime.setVisibility(View.VISIBLE);
                holder.ivTick.setVisibility(View.GONE);
            } else {
                holder.ivTime.setVisibility(View.GONE);
                holder.ivTick.setVisibility(View.VISIBLE);
            }
            holder.ivForward.setVisibility(View.GONE);
        } else {
            holder.tvUserName.setVisibility(View.VISIBLE);
            holder.ivForward.setVisibility(View.VISIBLE);
            holder.tvUserName.setText(chat.getSenderName());
            holder.tvUserName.setTextColor(context.getResources().getColor(R.color.c1));
//            if (chat.message_type_id == Utils.ADMIN) {
//                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.ultra_light_grey));
//            } else {
//                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.lightRed));
//            }
        }
        if (chat.media_type_id == Utils.VOICE_TYPE) {
            holder.tvMsgBody.setVisibility(View.GONE);
            holder.audioPlayer.setVisibility(View.VISIBLE);
            if (chat.isLocal) {
                holder.audioPlayer.setPath(chat.message_url);

            } else {
                holder.audioPlayer.setPath(path + chat.getMessage_url());
            }
            holder.clImage.setVisibility(View.GONE);
            if (chat.isUploaded) {
                holder.audioPlayer.showProgress();
            } else {
                holder.audioPlayer.hideProgress();
            }

        } else if (chat.media_type_id == Utils.IMAGE_TYPE) {
            holder.audioPlayer.setVisibility(View.GONE);
            holder.clImage.setVisibility(View.VISIBLE);
            holder.pbImage.setVisibility(chat.isUploaded ? View.VISIBLE : View.GONE);
            holder.tvMsgBody.setVisibility(View.GONE);
            if (chat.isLocal) {
                Utils.loadImageV1(context, "", chat.message_url, holder.ivMessage, R.drawable.ic_image);
            } else {
                Utils.loadImageV1(context, thumbPath + chat.getThumbnail(), path + chat.getMessage_url(), holder.ivMessage, R.mipmap.ic_launcher);
//                Utils.loadImageV1(context, "", chat.thumbnail, holder.ivMessage, R.drawable.ic_image);

            }
        } else if (chat.media_type_id == Utils.VIDEO_TYPE) {
            holder.audioPlayer.setVisibility(View.GONE);
            holder.clImage.setVisibility(View.VISIBLE);
            holder.pbImage.setVisibility(chat.isUploaded ? View.VISIBLE : View.GONE);
            Log.e("bindviewimage", "true");
            holder.fabPlay.setVisibility(chat.isUploaded ? View.GONE : View.VISIBLE);
            holder.tvMsgBody.setVisibility(View.GONE);
            if (chat.isLocal) {
                Utils.loadImageV1(context, "", chat.message_url, holder.ivMessage, R.drawable.ic_video);
            } else {
                Utils.loadImageV1(context, "", path + chat.getMessage_url(), holder.ivMessage, R.drawable.ic_video);

            }
        } else {
            holder.audioPlayer.setVisibility(View.GONE);
            holder.tvMsgBody.setVisibility(View.VISIBLE);
            holder.clImage.setVisibility(View.GONE);
            holder.fabPlay.setVisibility(View.GONE);
            holder.tvMsgBody.setText(chat.getBody());
        }
    }

    private void selectChecked(View holder, boolean isSelected) {
        if (isSelected) {
            holder.setBackgroundColor(context.getResources().getColor(R.color.ultra_light_select));
        } else {
            holder.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    public void remove() {
        for (Chat chat : list) {
            if (chat.isSelected) {
                chat.isSelected = false;

            }
        }
        notifyDataSetChanged();
    }

    public List<Chat> getSelected() {
        List<Chat> chats = new ArrayList<>();
        for (Chat chat : list) {
            if (chat.isSelected) {
                chats.add(chat);
            }
        }
        return chats;
    }

    private void setItemSelection(ProgrammingViewHolder holder, int i) {
        if (list.get(i).isSelected) {
            list.get(i).isSelected = false;
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));

        } else {
            list.get(i).isSelected = true;
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.ultra_light_select));

        }
        int size = 0;
        for (Chat chat : list) {
            if (chat.isSelected) {
                size++;
            }
        }
        selectedListCallback.onSuccess(size);
    }


}
