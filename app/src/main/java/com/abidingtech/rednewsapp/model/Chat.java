package com.abidingtech.rednewsapp.model;


import android.content.Context;
import android.net.Uri;

import com.abidingtech.rednewsapp.services.MyApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.abidingtech.rednewsapp.model.Message.IMAGE_TYPE;
import static com.abidingtech.rednewsapp.model.Message.TEXT_TYPE;
import static com.abidingtech.rednewsapp.model.Message.VIDEO_TYPE;
import static com.abidingtech.rednewsapp.model.Message.VOICE_TYPE;

public class Chat {

    public static final String MSG_OBJECT_EXTRA = "msg_data";

    public int group_id, message_type_id, media_type_id, sender_id;
    public String message_url;
    public boolean is_deleted;  public int id;
    private String body,friendly_time;
    public transient boolean isUploaded, isLocal, isSelected;
    public transient int position;
    public Chat(int sender_id) {
        this.sender_id = sender_id;
    }

    public String thumbnail;
    private String sender_name;
    private String profile_url;
    private final int nameLimit = 20;

    public int getId() {
        return id;
    }

    public boolean isPendingUpload() {
        return id == 0;
    }


    public void setBody(String body) {
        this.body = body.trim();
    }

    public String getBody() {
        if (is_deleted)
            return "~This message was deleted";
        return body;
    }

    public boolean isOwnMessage() {
//        Log.e("sender_id", sender_id + "");
        return sender_id == MyApplication.getUserId();
    }

    public String getFriendlyTime() {
        if (friendly_time == null) {
            friendly_time = new SimpleDateFormat("h:mm a").format(new Date());
        }
        return friendly_time;
    }

    public String getFilePath() {
        return Message.MEDIA_PATHS.get(media_type_id) + body;
    }

    public boolean isMediaExists() {
        return new File(getFilePath()).exists();
    }


    public String getThumbnail() {
        return Uri.parse(thumbnail).getLastPathSegment();
    }


    public String getSenderName(){
        String name = sender_name;
        if(name == null)
            return null;
        if(sender_name.length() <= nameLimit)
            return name;
        String [] splitted = sender_name.split("\\s+");
        if(splitted.length > 2)
            name =  splitted[0]+" "+splitted[1]+ " " + splitted[2];
        else if(splitted.length > 1)
            name =  splitted[0]+" "+splitted[1];
        else
            name = sender_name.substring(0,nameLimit);

        return name + "...";
    }
    public String getProfileImage() {

        return profile_url;
    }


    public String getMessage_url() {
        return Uri.parse(message_url).getLastPathSegment();
    }

    public Message createMessageObject(Context context) {
        switch (media_type_id) {
            case TEXT_TYPE:
                return new TextMessage(context);
            case IMAGE_TYPE:
                return new ImageMessage(context);
            case VOICE_TYPE:
                return new VoiceMessage(context);
            case VIDEO_TYPE:
                return new VideoMessage(context);
        }
        return null;
    }
}
