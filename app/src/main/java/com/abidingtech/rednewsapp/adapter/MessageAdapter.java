package com.abidingtech.rednewsapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.Acquirable;
import com.abidingtech.rednewsapp.callback.BooleanCallback;
import com.abidingtech.rednewsapp.callback.IdCallback;
import com.abidingtech.rednewsapp.dao.ChatDAO;
import com.abidingtech.rednewsapp.download.CustomFetchListener;
import com.abidingtech.rednewsapp.download.FetchFactory;
import com.abidingtech.rednewsapp.model.ActionMessage;
import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.ImageMessage;
import com.abidingtech.rednewsapp.model.Message;
import com.abidingtech.rednewsapp.model.TextMessage;
import com.abidingtech.rednewsapp.model.VideoMessage;
import com.abidingtech.rednewsapp.model.VoiceMessage;
import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.abidingtech.rednewsapp.model.Message.IMAGE_TYPE;
import static com.abidingtech.rednewsapp.model.Message.TYPE_ACTION;
import static com.abidingtech.rednewsapp.model.Message.TYPE_ACTION_CODE;
import static com.abidingtech.rednewsapp.model.Message.VIDEO_TYPE;
import static com.abidingtech.rednewsapp.model.Message.VOICE_TYPE;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ProgrammingViewHolder> implements CustomFetchListener {

    public List<Chat> list;
    private Activity context;
    private boolean isMultiple = false;
    public static FetchFactory fetchFactory;
    private IdCallback selectedListCallback = null;
    Message acquired;
    Chat mSelectedChat;
    List<Message> selectedMessages = new ArrayList<>();
    ChatDAO chatDAO;

    public MessageAdapter(Activity context, List<Chat> list, IdCallback onClickListener,ChatDAO chatDAO) {
        this.list = list;
        this.context = context;
        this.chatDAO = chatDAO;
        fetchFactory = FetchFactory.getInstance(context);
        this.selectedListCallback = onClickListener;
    }

    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        Message message = new TextMessage(context);

        if (viewType == TYPE_ACTION_CODE) {
            message = new ActionMessage(context);
        } else if (viewType == VOICE_TYPE) {
            message = new VoiceMessage(context);
        } else if (viewType == IMAGE_TYPE) {
            message = new ImageMessage(context);
        } else if (viewType == VIDEO_TYPE) {
            message = new VideoMessage(context);
        }


        return new ProgrammingViewHolder(message, viewGroup);
    }


    @SuppressLint({"SetTextI18n", "RestrictedApi"})
    @Override
    public void onBindViewHolder(@NonNull final ProgrammingViewHolder holder, final int i) {
        final Chat c = list.get(i);
        holder.message.setData(c, true, null);
        List<Chat> selected = getSelected();
        View root = holder.message.getLoadedView();
        checkSelected(holder, i, root);
        root.setOnLongClickListener(v -> {
            Log.e("mytag", "onBindViewHolder: long click");
            if (getSelected().isEmpty() && !isMultiple) {
                if (!holder.message.getChat().is_deleted) {
                    isMultiple = true;
                    if (!holder.message.getChat().isSelected) {
                        selectedMessages.add(holder.message);
                    }
                    holder.message.getChat().isSelected = true;
                    selectedListCallback.onSuccess(getSelected().size());
                    checkSelected(holder, i, root);

                }
                return true;
            } else {
                return false;
            }
        });
        root.setOnClickListener(v -> {
            Log.e("mytag", "onBindViewHolder: click" + holder.message.getChat().isSelected);
            if (!getSelected().isEmpty() && isMultiple) {
                if (!holder.message.getChat().is_deleted) {
                    if (!holder.message.getChat().isSelected) {
                        selectedMessages.add(holder.message);
                    } else {
                        selectedMessages.remove(holder.message);
                    }
                    holder.message.getChat().isSelected = !holder.message.getChat().isSelected;
                    selectedListCallback.onSuccess(getSelected().size());

                    checkSelected(holder, i, root);
                }
            } else {
                isMultiple = false;
            }
        });
    }

    private void checkSelected(@NonNull ProgrammingViewHolder holder, int i, View root) {
        if (holder.message.getChat().isSelected)
            root.setBackgroundColor(context.getResources().getColor(R.color.blue_opacity));
        else {
            root.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public void onCompleted(@NotNull Download download) {
        Log.e("DOWNLOAD", "onCompleted: " + download.getIdentifier()
                + "  file-> " + download.getFile());
    }

    @Override
    public void onPaused(@NotNull Download download) {
        Log.e("DOWNLOAD", "onPaused: " + download.getId());
    }

    @Override
    public void onProgress(@NotNull Download download, long l, long l1) {
        Log.e("DOWNLOAD", "onProgress: " + download.getId() + " " + l + " " + l1);
    }

    @Override
    public void onResumed(@NotNull Download download) {
        Log.e("DOWNLOAD", "onResumed: " + download.getId());
    }

    @Override
    public void onStart(Download download) {

    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProgrammingViewHolder extends RecyclerView.ViewHolder {


        Message message;

        ProgrammingViewHolder(@NonNull Message message, ViewGroup viewGroup) {
            super(message.getView(context, viewGroup, new Acquirable() {
                @Override
                public void onAcquire(Message message) {
                    if (acquired != null) {
                        acquired.stopAndReleaseResources();
                    }
                    acquired = message;
                }

                @Override
                public void onLongPress(Chat chat) {
                    mSelectedChat = chat;
                }
            }));
            this.message = message;
            message.setChatDAO(chatDAO);
        }

    }


    @Override
    public int getItemViewType(int position) {
        Chat chat = list.get(position);
        if (chat.message_type_id == TYPE_ACTION)
            return TYPE_ACTION_CODE;
        return chat.media_type_id;

    }



    public List<Chat> getSelected() {
        Log.e("mytag", "getSelected: ");
        List<Chat> chats = new ArrayList<>();
        for (Message message : selectedMessages) {
            chats.add(message.getChat());
        }
        return chats;
    }

    public List<Message> getSelectedMessages() {
        return selectedMessages;
    }

    public boolean isOther() {
        for (Chat chat : getSelected()) {
            if (!chat.isOwnMessage()) {
                return true;
            }
        }
        return false;
    }


    public void reset() {
        Log.e("reset", "true");
        for (Message chat : getSelectedMessages()) {
            if (chat.getChat().isSelected) {
                chat.getChat().isSelected = false;
            }
        }
        getSelectedMessages().clear();

        notifyDataSetChanged();
    }

}
