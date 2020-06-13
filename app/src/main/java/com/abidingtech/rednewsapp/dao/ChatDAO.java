package com.abidingtech.rednewsapp.dao;

import android.content.Context;
import android.util.Log;

import com.abidingtech.rednewsapp.model.FileType;
import com.abidingtech.rednewsapp.model.User;
import com.abidingtech.rednewsapp.services.Utils;
import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.GroupModel;
import com.abidingtech.rednewsapp.model.ListPagination;
import com.abidingtech.rednewsapp.services.APIRequest;
import com.android.volley.Request;
import com.esafirm.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.List;

public interface ChatDAO {


    void getGroupChat(String urls, ObjectCallback<ListPagination> callback);

    void getGroups(String groupUrls, ArrayCallback<GroupModel> callback);
    void getProfile(ObjectCallback<User> callback);

    void getUserById(int id, ObjectCallback<GroupModel> callback);

    void getGroupChatById(int id, ObjectCallback<ListPagination> callback);

    void createMessage(int position, Chat chat, ObjectCallback<Chat> callback);

    void deleteMessage(Chat chat, ObjectCallback<String> callback);

    void createMessage(Chat chat, ObjectCallback<Chat> callback);
    void setFilePath(ObjectCallback<FileType> path);

    void sendMultiple(List<Chat> chat, List<GroupModel> groupIds, final ArrayCallback<Chat> callback);

    void sendSingleMsg(Chat chat, List<GroupModel> groupIds, final ArrayCallback<Chat> callback);

    void forwardMsg(Chat chat, ObjectCallback<Chat> callback);

    void deleteMultipleMessage(List<Chat> chatList, ArrayCallback<Chat> callback);
}
