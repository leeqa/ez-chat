package com.abidingtech.rednewsapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.adapter.MessageAdapter;
import com.abidingtech.rednewsapp.adapter.MessageSwipeToReply;
import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.BooleanCallback;
import com.abidingtech.rednewsapp.callback.ClickListenerCallback;
import com.abidingtech.rednewsapp.callback.ConfirmCallback;
import com.abidingtech.rednewsapp.callback.IdCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.dao.ChatDAO;
import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.FileType;
import com.abidingtech.rednewsapp.model.GroupModel;
import com.abidingtech.rednewsapp.model.ListPagination;
import com.abidingtech.rednewsapp.model.Message;
import com.abidingtech.rednewsapp.services.AlertUtil;
import com.abidingtech.rednewsapp.services.AudioRecordView;
import com.abidingtech.rednewsapp.services.MyApplication;
import com.abidingtech.rednewsapp.services.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.abidingtech.rednewsapp.model.Chat.MSG_OBJECT_EXTRA;
import static com.abidingtech.rednewsapp.services.Utils.TEXT_TYPE;

public class ChatView extends LinearLayout implements  IdCallback,BooleanCallback {

    public static final int IMAGE_PREVIEW_CODE = 1008;
    public static final String GROUP_NAME = "groupName";
    MessageAdapter adapter;
    public String TAG = "ChatActivity";
    Context context;
    Activity activity;
    Toolbar toolbar;
    public static final int REQUEST_CODE_PICKER = 1000;
    int groupId=3;
    GroupModel groupModel;
    ImageView ivTb;
    ImageView ivShareMultiple;
    TextView tvTb;
    RecyclerView recyclerView;
    LinearLayout  listEmpty;
    List<Chat> chatList;
    public AudioRecordView recordView;
    private MediaRecorder recorder;
    private String savePath;
    TextView tvMsgNote;
    boolean isLoaded;
    ProgressBar pbPagination;
    private String nextUrl;
    int sendCount;
    android.app.AlertDialog dialog;
    ImageView ivDown;
    TextView tvReceiverName, tvReceiverText;
    ImageView ivCancel;
    int scrollEvent;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    AppBarLayout appBarLayout;
    LinearLayout llReplyView;
    int userId;
    FrameLayout flSelected;
    MenuItem menuItemShare, menuItemReply;
    MenuItem menuDelete;
    ChatDAO chatDAO;
    ShimmerFrameLayout shimmer_layout;

    public void init(Context contexts){
        context=contexts;
        activity=(AppCompatActivity)contexts;
        View view = inflate(getContext(), R.layout.activity_chat, null);
        pbPagination = view.findViewById(R.id.pbPagination);
        toolbar = view.findViewById(R.id.toolbar);
        appBarLayout = view.findViewById(R.id.app_bar);
        ivDown = view.findViewById(R.id.ivDown);
        ivCancel = view.findViewById(R.id.ivCancelReply);
        tvMsgNote = view.findViewById(R.id.tvMsgNote);
        ivShareMultiple = view.findViewById(R.id.ivShareMultiple);
        shimmer_layout = view.findViewById(R.id.shimmer_layout);
        recordView = view.findViewById(R.id.recordingView);
        recyclerView = view.findViewById(R.id.recyclerView);
        listEmpty = view.findViewById(R.id.listEmpty);
        ivTb = view.findViewById(R.id.ivTb);
        flSelected = view.findViewById(R.id.flSelected);
        tvTb = view.findViewById(R.id.tvTb);
        addView(view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        initListener();
        actionModeCallback = new ActionModeCallback();
    }

    private void initData() {
        chatDAO.getUserById(groupId, new ObjectCallback<GroupModel>() {
            @Override
            public void onData(GroupModel gm) {
                groupModel = gm;
                loadToolbar();
            }

            @Override
            public void onError(String msg) {

            }

        });
    }

    private void loadToolbar() {
        tvTb.setText(groupModel.name);
        Utils.loadImageV1(context, "", groupModel.thumbnail, ivTb, R.drawable.ic_group_black_24dp);
    }

    private void loadChat() {
        chatDAO.getGroupChatById(groupId, new ObjectCallback<ListPagination>() {
            @Override
            public void onData(ListPagination listPagination) {
                chatList = listPagination.data;
                nextUrl = listPagination.next_page_url;
                isLoaded = true;
                Log.e("NEXT_URL", nextUrl + "");

                adapter = new MessageAdapter(activity, chatList,  ChatView.this,chatDAO);
                recyclerView.setAdapter(adapter);
                listVisibility(chatList.isEmpty());
            }

            @Override
            public void onError(String msg) {
                shimmer_layout.setVisibility(View.GONE);
                listEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initListener() {
        recordView.imageViewAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivityForResult(new Intent(activity, PreviewImageActivity.class)
                                .putExtra(GROUP_NAME, groupModel.name)
                        , IMAGE_PREVIEW_CODE);
                chatDAO.setFilePath(new ObjectCallback<FileType>() {
                    @Override
                    public void onData(FileType fileType) {
                        sendChat("",fileType.type,fileType.path);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });

            }
        });
        recordView.getMessageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scroll();
                    }
                }, 300);
            }
        });
        recordView.setRecordingListener(new AudioRecordView.RecordingListener() {
            @Override
            public void onRecordingStarted() {

                try {
                    recorder = new MediaRecorder();
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.mp3";
                    Log.e("path", savePath + "");
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    recorder.setOutputFile(savePath);
                    recorder.prepare();
                    recorder.start();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("onRecordingStarted =>", "true");





            }

            @Override
            public void onRecordingLocked() {
                Log.e("onRecordingLocked =>", "true");

            }

            @Override
            public void onRecordingCompleted() {
                Log.e("onRecordingCompleted =>", "true");
                releaseRecorder();
                sendChat("", Utils.VOICE_TYPE, savePath);
            }

            @Override
            public void onRecordingCanceled() {
                Log.e("onRecordingCanceled =>", "true");
                releaseRecorder();

            }
        });
        recordView.getSendView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordView.getMessageView().getText().toString().isEmpty()) {
                    recordView.getMessageView().setError("Field can't be empty!");
                    recordView.getMessageView().requestFocus();
                } else {
                    Log.e(TAG, "onClick: => getsendview");
                    sendChat(recordView.getMessageView().getText().toString(), Utils.TEXT_TYPE, "");

                }
            }
        });
        MessageSwipeToReply instance = MessageSwipeToReply.getInstance(new ClickListenerCallback() {
            @Override
            public void setBrandId(int id) {
                Log.e(TAG, "setBrandId: " + id);
                Chat chat = chatList.get(id);
                if (!chat.is_deleted) {
                    llReplyView.setVisibility(View.VISIBLE);
                    if (chat.isOwnMessage()) {
                        tvReceiverName.setText("You");
                    } else {
                        tvReceiverName.setText(chat.getSenderName());
                    }
                    tvReceiverText.setText(chat.getBody());
                }

            }
        }, context);
        new ItemTouchHelper(instance).attachToRecyclerView(recyclerView);
        ivCancel.setOnClickListener(v -> {
            Log.e("mytag", "initListener: ");
            llReplyView.setVisibility(View.GONE);
        });

    }


    private void releaseRecorder() {
        if (recorder == null)
            return;
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (Exception e) {
            Log.e("onRecordingCompleted =>", e.getMessage() + "");

        }
    }

    private void sendChat(String msg, int type, String filePath) {
        if (msg.trim().isEmpty() && type == TEXT_TYPE) {
            Toasty.warning(context, "Empty message", Toasty.LENGTH_SHORT).show();
            return;
        }
        Chat chat = new Chat(userId);
        chat.group_id = groupId;
        chat.media_type_id = type;
        if (type == TEXT_TYPE) {
            chat.setBody(msg);
            recordView.getMessageView().setText("");
            recordView.getMessageView().requestFocus();
        } else {
            chat.message_url = filePath;
        }
        chatList.add(chat);
        adapter.notifyItemInserted(chatList.size() - 1);
        scroll();
        sendCount++;
    }



    private void scroll() {

        if (chatList != null && chatList.size() != 0) {
            recyclerView.smoothScrollToPosition(chatList.size() - 1);
        }
    }


    private void listVisibility(boolean isEmpty) {
        if (isEmpty) {
            shimmer_layout.setVisibility(View.GONE);
            listEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    assert linearLayoutManager != null;
                    int pos = linearLayoutManager.findFirstVisibleItemPosition();
                    int llpos = linearLayoutManager.findViewByPosition(pos).getTop();

                    if (llpos == 0 && pos == 0) {
                        scrollEvent++;
                        Log.e("topreach", "end");
                        if (isLoaded && scrollEvent > 2) {
                            if (!nextUrl.equals("null")) {
                                pbPagination.setVisibility(View.VISIBLE);
                                isLoaded = false;
                                chatDAO.getGroupChat(nextUrl, new ObjectCallback<ListPagination>() {
                                    @Override
                                    public void onData(ListPagination listPagination) {
                                        chatList.addAll(0, listPagination.data);
                                        nextUrl = listPagination.next_page_url;
                                        adapter.notifyItemRangeInserted(0, listPagination.data.size());
                                        scrollEvent = 0;
                                        isLoaded = true;
                                        pbPagination.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(String msg) {
                                        pbPagination.setVisibility(View.GONE);

                                    }
                                });
                            }

                        }
                    }
                }
            });
            recyclerView.setVisibility(View.VISIBLE);
            listEmpty.setVisibility(View.GONE);
            shimmer_layout.setVisibility(View.GONE);
        }

    }























    private void enableActionMode(int count) {
        if (actionMode == null) {
            //ithy kam peya hoya
//            actionMode = context.startSupportActionMode(actionModeCallback);
        }
        toggleSelection(count);
    }

    private void toggleSelection(int count) {

        if (count == 0) {
            actionMode.finish();
            tbVisibility(true);
        } else {
            if (count == 1) {
                menuItemReply.setVisible(true);
            } else {
                menuItemReply.setVisible(false);
            }
            Log.e("delisown", adapter.isOther() + "");
            menuDelete.setVisible(!adapter.isOther());
            actionMode.setTitle("" + String.valueOf(count));
            actionMode.invalidate();
            tbVisibility(false);

        }
    }



    @Override
    public void onSuccess(int id) {
        sendCount = id;
        Log.e("mytag", "onSuccess: " + id);
        if (id > 0) {
            enableActionMode(id);
        } else {
            actionMode.finish();
            tbVisibility(true);
        }


    }

    @Override
    public void onData(boolean exists) {
        if (exists) {
            ivDown.setVisibility(View.VISIBLE);
        } else {
            ivDown.setVisibility(View.GONE);

        }
    }

    @Override
    public void onError(String msg) {

    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            Tools.setSystemBarColor(NotificationActivity.this, R.color.colorPrimary);
            Log.e(TAG, "onCreateActionMode: ");
            mode.getMenuInflater().inflate(R.menu.menu_forward, menu);
            menuItemShare = menu.findItem(R.id.action_share);
            menuItemReply = menu.findItem(R.id.action_reply);
            menuDelete = menu.findItem(R.id.action_delete);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_forward) {
                if (sendCount > 0) {
                    Log.e("abcd", "onActionItemClicked: " + sendCount);
                    getSelected();
                    mode.finish();
                    return true;
                } else {
                    Toasty.info(context, "", Toasty.LENGTH_SHORT, true).show();
                    return true;

                }
            }
            if (id == R.id.action_delete) {
                dialog = AlertUtil.showConfirmationDialogV1(context, "Do you want to delete", "Do you want to delete for everyone", "Cancel", new ConfirmCallback() {
                    @Override
                    public void onConfirmed() {
                        dialog.dismiss();
                        deleteMultipleMessages();

                    }

                    @Override
                    public void onCancel() {
                        dialog.dismiss();

                    }
                });

            }
            if (id == R.id.action_reply) {
                Chat chat = adapter.getSelected().get(0);
                if (!chat.is_deleted) {
                    llReplyView.setVisibility(View.VISIBLE);
                    tvReceiverName.setText(chat.getSenderName());
                    tvReceiverText.setText(chat.getBody());
                }

            }
            if (id == R.id.action_share) {
                if (sendCount == 0) {
                    Chat chatShare = adapter.getSelected().get(0);
                    mode.finish();
                    String folderName = chatShare.isOwnMessage() ? "redchat/sent/" : "redchat/receive/";
                    String localPath = "";
                    if (chatShare.media_type_id != Utils.TEXT_TYPE) {
                        localPath = Environment.getExternalStorageDirectory() + File.separator + folderName + chatShare.getMessage_url();
                    }
                    if (chatShare.media_type_id == Utils.TEXT_TYPE) {
                        String shareBody = chatShare.getBody();
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
//                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share imagehs"));
                    } else if (chatShare.media_type_id == Utils.IMAGE_TYPE) {

                        Utils.shareContent(activity, localPath);
                    } else if (chatShare.media_type_id == Utils.VIDEO_TYPE) {

                        Utils.shareVideo(activity, localPath);

                    } else if (chatShare.media_type_id == Utils.VOICE_TYPE) {
                        Uri uri = Uri.parse(localPath);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("audio/*");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                       activity. startActivity(Intent.createChooser(share, "Share Sound File"));


                    }
                    adapter.reset();

                    return true;
                } else {
                    Toasty.info(context, "", Toasty.LENGTH_SHORT, true).show();
                    return true;

                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.reset();
            actionMode = null;
            tbVisibility(true);
//            Tools.setSystemBarColor(NotificationActivity.this, R.color.colorPrimary);
        }
    }


    private void tbVisibility(boolean isVisisble) {
        if (isVisisble) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    appBarLayout.setVisibility(View.VISIBLE);

                }
            }, 350);
        } else {
            appBarLayout.setVisibility(View.GONE);

        }
    }


    private void getSelected() {

        tbVisibility(true);

    }

    public void deleteMultipleMessages() {
        Utils.showProgressDialog(activity);
        chatDAO.deleteMultipleMessage(adapter.getSelected(), new ArrayCallback<Chat>() {
                    @Override
                    public void onData(List<Chat> list) {
                        actionMode.finish();
                        for (Message message : adapter.getSelectedMessages()) {
                            message.delete();
                        }
                        adapter.getSelectedMessages().clear();
                        Utils.dismissProgressDialog();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                }
        );
    }


    public ChatView(Context context) {
        super(context);
        init(context);
    }

    public void setChatDAO(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
        loadChat();
        initData();
    }

    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ChatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }
}
