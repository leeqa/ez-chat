package com.abidingtech.rednewsapp.model;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.Acquirable;
import com.abidingtech.rednewsapp.callback.ActionCallback;
import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.dao.ChatDAO;
import com.abidingtech.rednewsapp.services.APIRequest;
import com.abidingtech.rednewsapp.services.Utils;
import com.android.volley.Request;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.content.res.Resources.getSystem;

public abstract class Message  {

    public static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/Red/";
    public static final String VOICE_DIR = "voice/";
    public static final String IMAGE_DIR = "image/";
    public static final String VIDEO_DIR = "video/";
    public static final Map<Integer, String> MEDIA_PATHS = new HashMap<Integer, String>() {{
        put(VOICE_TYPE, BASE_PATH + VOICE_DIR);
        put(IMAGE_TYPE, BASE_PATH + IMAGE_DIR);
        put(VIDEO_TYPE, BASE_PATH + VIDEO_DIR);
    }};


    public final static int TEXT_TYPE = 1;
    public final static int VOICE_TYPE = 2;
    public final static int IMAGE_TYPE = 3;
    public final static int VIDEO_TYPE = 4;

    public final static int TYPE_NORMAL = 1;
    public final static int TYPE_ACTION = 3;
    public final static int TYPE_ACTION_CODE = -1;
    public final static int TYPE_ADMIN = 2;
    protected static final int MAX_WIDTH = getSystem().getDisplayMetrics().widthPixels * 80 / 100;
    protected TextView tvTime;
    TextView tvUser, tvDeleted;
    CardView cv;
    ImageView ivForward, ivStatus;
    LinearLayout root, llCenter;
    LayoutInflater inflater;
    //    protected LinearLayout.LayoutParams params;
    protected int bgColor;
    protected Chat chat;
    protected Context context;
    private List<String> ownOptions = new ArrayList<>();
    private List<String> otherOptions = new ArrayList<>();
    protected Acquirable acquirable;
    private View view;
    protected boolean displayView;
    private ActionCallback savedCallback;
    ChatDAO chatDAO;

    //    public Message(){
//        optionList.add("Forward");
//        optionList.add("Cancel");
//    }
    public Message(Context context) {
        ownOptions.add("Forward");
        ownOptions.add("Delete for everyone");
        ownOptions.add("Cancel");

        otherOptions.add("Forward");
        otherOptions.add("Cancel");
        this.context = context;
    }

    public void setChatDAO(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public final void setData(Chat chat, boolean displayView, ActionCallback savedCallback) {
        if (chat == null) {
            throw new NullPointerException("Chat object cannot be null");
        }
        Log.e("chat",new Gson().toJson(chat));
//        if (chat.group_id == 0) {
//            throw new IllegalStateException("Group Id not set");
//        }
        this.chat = chat;
        this.savedCallback = savedCallback;
        this.displayView = displayView;
        if (displayView) {
            if (chat.isOwnMessage()) {
                root.setGravity(Gravity.END);
                bgColor = context.getResources().getColor(R.color.lightRed);
                tvUser.setVisibility(View.GONE);
                ivStatus.setVisibility(View.VISIBLE);
                ivForward.setVisibility(View.GONE);
            } else {
                root.setGravity(Gravity.START);
                bgColor = context.getResources().getColor(R.color.white);
                ivStatus.setVisibility(View.GONE);
                tvUser.setVisibility(View.VISIBLE);
                ivForward.setVisibility(View.VISIBLE);
            }

            if (chat.is_deleted) {
                ivForward.setVisibility(View.GONE);
                llCenter.setVisibility(View.GONE);
                tvDeleted.setVisibility(View.VISIBLE);
            } else {
                llCenter.setVisibility(View.VISIBLE);
                tvDeleted.setVisibility(View.GONE);
            }
            tvUser.setText(chat.getSenderName());
            tvTime.setText(chat.getFriendlyTime());
            cv.setCardBackgroundColor(bgColor);
            attachLongPress(view);
            loadData();
        }

        if (chat.isPendingUpload()) {
            if (displayView)
                ivStatus.setImageResource(R.drawable.ic_access_time);
            create();
        }
    }

    protected void attachLongPress(View view) {

        List<String> list = chat.isOwnMessage() ? ownOptions : otherOptions;
        final String[] options = new String[list.size()];
        list.toArray(options);

        if (!chat.is_deleted) {

//            view.setOnLongClickListener(v -> {
//                    chat.isSelected = !chat.isSelected;
//                    if (chat.isSelected)
//                        view.setBackgroundColor(Color.WHITE);
//                    else {
//                        view.setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    acquirable.onLongPress(chat);
////                AlertDialog.Builder builder = new AlertDialog.Builder(context);
////                builder.setTitle("Pick an option");
////                builder.setItems(options, (dialog, which) -> {
////                    if(options[which].equals("Cancel")){
////                        dialog.dismiss();
////                    }
////                    else {
////                        consumeOption(which);
////                    }
////                });
////                builder.show();
//                    return true;
//            });
        }
    }

    public Chat getChat() {
        return chat;
    }

    private void consumeOption(int which) {
        switch (which) {
            case 0:
                forwardMessage();
                break;
            case 1:
                chatDAO.deleteMessage(chat, new ObjectCallback<String>() {
                    @Override
                    public void onData(String response) {
                        llCenter.setVisibility(View.GONE);
                        tvDeleted.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(String msg) {
                        Toasty.error(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
        }
    }

    public void delete() {
        llCenter.setVisibility(View.GONE);
        tvDeleted.setVisibility(View.VISIBLE);
    }


    public View getLoadedView() {
        return view;
    }

    public View getView(Context context, ViewGroup viewGroup, Acquirable acquirable) {
        this.context = context;
        this.acquirable = acquirable;
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.message_text_item, viewGroup, false);

        cv = view.findViewById(R.id.cv);
        root = view.findViewById(R.id.root);
        tvUser = view.findViewById(R.id.tvUserName);
        tvTime = view.findViewById(R.id.tvTimeAgo);
        ivForward = view.findViewById(R.id.ivForward);
        ivStatus = view.findViewById(R.id.ivStatus);
        llCenter = view.findViewById(R.id.llCenter);
        tvDeleted = view.findViewById(R.id.tvDeleted);

        ivForward.setOnClickListener(v -> forwardMessage());

        View centerView = loadView(viewGroup);
        if (centerView != null) {
            return centerView;
        }

        return view;
    }

    protected abstract void loadData();

    protected abstract void create();

    public abstract View loadView(ViewGroup viewGroup);

    protected void saveMessage(ActionCallback callback) {
        chatDAO.createMessage(chat, new ObjectCallback<Chat>() {
            @Override
            public void onData(Chat added) {
//                chat.pendingUpload = false;
                chat.id = added.id;
                if (displayView)
                    ivStatus.setImageResource(R.drawable.ic_check_white_24dp);
                callback.onSuccess();
                if (savedCallback != null) {
                    savedCallback.onSuccess();
                }
            }

            @Override
            public void onError(String msg) {
                ivStatus.setImageResource(R.drawable.ic_error);
                callback.onError(msg);
                if (savedCallback != null) {
                    savedCallback.onError(msg);
                }
            }
        });
    }

    public final void forwardMessage() {

    }

    public void stopAndReleaseResources() {

    }


}
