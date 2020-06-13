package com.abidingtech.rednewsapp.dao;

import android.content.Context;
import android.util.Log;

import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.GroupModel;
import com.abidingtech.rednewsapp.model.ListPagination;
import com.abidingtech.rednewsapp.services.APIRequest;
import com.abidingtech.rednewsapp.services.Utils;
import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

public class ChatDAOCopy {

    private static ChatDAOCopy _this;
    Context context;
    String url = Utils.URL;
    public static String TAG = "CHATDAO: ";


    public static ChatDAOCopy getInstance(Context context) {
        if (_this == null) {
            _this = new ChatDAOCopy();
        }
        _this.context = context;

        return _this;
    }

    public void getGroupChat(String urls, ObjectCallback<ListPagination> callback) {
        new APIRequest(context).stringReqListPagination(urls, Chat.class, callback);
    }

    public String getGroupChatByIdPath(int id) {
        return url + "group/" + id + "/chat";
    }

    public void getGroupChatById(int id, ObjectCallback<ListPagination> callback) {
        String path = getGroupChatByIdPath(id);
//        PrefHelper helper = new PrefHelper(context);
//        List<Chat> list = helper.getArray(path,Chat.class);

/*        if(!path.contains("page") && list != null && !list.isEmpty()){
            ListPagination pagination = new ListPagination();
            pagination.next_page_url = path+"?group_id="+id+"&page=2";
            pagination.data = list;
            callback.onData(pagination);
        }
        else*/
        {
            getGroupChat(path, callback);
//            new APIRequest(context).stringReqListPagination(path, Chat.class, callback);
        }
    }

    public void createMessage(int position, Chat chat, ObjectCallback<Chat> callback) {
        new APIRequest(context).postPutRequest(url + "message", chat, new ObjectCallback<Chat>() {
            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }

            @Override
            public void onData(Chat chat) {
                chat.position = position;
                callback.onData(chat);
            }
        }, Request.Method.POST);

    }

    public void deleteMessage(Chat chat, ObjectCallback<String> callback) {
        new APIRequest(context).stringDeleteReq(url + "message/" + chat.id, new ObjectCallback<String>() {
            @Override
            public void onData(String s) {
                callback.onData(s);
                chat.is_deleted = true;
            }

            @Override
            public void onError(String msg) {
                callback.onError(msg);
            }
        });
    }

    public void createMessage(Chat chat, ObjectCallback<Chat> callback) {
        new APIRequest(context).postPutRequest(url + "message", chat, callback, Request.Method.POST);
    }

    int index = 0;
    int groupIndex = 0;
    List<Chat> uploadededChat = new ArrayList<>();

    public void sendMultiple(List<Chat> chat, List<GroupModel> groupIds, final ArrayCallback<Chat> callback) {
        chat.get(index).group_id = groupIds.get(groupIndex).id;

        forwardMsg(chat.get(index), new ObjectCallback<Chat>() {
            @Override
            public void onData(Chat s) {
                Log.e("UPLOAD", "cloud: " + s);

                uploadededChat.add(chat.get(index));
                index++;
                if (index < chat.size()) {
                    Log.e("chatlistfirst", index + "");
                    sendMultiple(chat, groupIds, callback);
                } else {
                    Log.e("chatlist", index + "");

                    index = 0;
                    groupIndex++;

                    if (groupIndex < groupIds.size()) {
                        Log.e("chatlistindex", index + "");
                        sendMultiple(chat, groupIds, callback);
                    } else {
                        callback.onData(new ArrayList<>(uploadededChat));
                        uploadededChat.clear();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                index = 0;
                uploadededChat.clear();
                callback.onError(msg);
            }
        });
    }

    public void sendSingleMsg(Chat chat, List<GroupModel> groupIds, final ArrayCallback<Chat> callback) {
        chat.group_id = groupIds.get(index).id;
        forwardMsg(chat, new ObjectCallback<Chat>() {
            @Override
            public void onData(Chat s) {
                Log.e("UPLOAD", "cloud: " + s);
                uploadededChat.add(chat);
                index++;
                if (index < groupIds.size()) {
                    sendSingleMsg(chat, groupIds, callback);
                } else {
                    callback.onData(new ArrayList<>(uploadededChat));
                    uploadededChat.clear();
                    index = 0;
                }
            }

            @Override
            public void onError(String msg) {
                index = 0;
                uploadededChat.clear();
                callback.onError(msg);
            }
        });
    }

    public void forwardMsg(Chat chat, ObjectCallback<Chat> callback) {

        new APIRequest(context).postPutRequest(url + "message", chat, callback, Request.Method.POST);
    }


    int delIndex = 0;

    public void deleteMultipleMessage(List<Chat> chatList, ArrayCallback<Chat> callback) {
        Log.e("delsize", chatList.size() + "");
        deleteMessage(chatList.get(delIndex), new ObjectCallback<String>() {
            @Override
            public void onData(String s) {
                Log.e("delindex", delIndex + "");
                delIndex++;
                if (delIndex < chatList.size()) {
                    deleteMultipleMessage(chatList, callback);
                } else {
                    callback.onData(new ArrayList<>());
                    delIndex = 0;
                }
            }

            @Override
            public void onError(String msg) {
                delIndex = 0;
                callback.onError(msg);
            }
        });
    }

}
