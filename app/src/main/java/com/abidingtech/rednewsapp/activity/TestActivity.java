package com.abidingtech.rednewsapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.dao.ChatDAO;
import com.abidingtech.rednewsapp.download.FetchFactory;
import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.FileType;
import com.abidingtech.rednewsapp.model.GroupModel;
import com.abidingtech.rednewsapp.model.ListPagination;
import com.abidingtech.rednewsapp.model.User;
import com.abidingtech.rednewsapp.services.APIRequest;
import com.abidingtech.rednewsapp.services.Utils;
import com.android.volley.Request;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.abidingtech.rednewsapp.activity.ChatView.IMAGE_PREVIEW_CODE;
import static com.abidingtech.rednewsapp.activity.ChatView.REQUEST_CODE_PICKER;
import static com.abidingtech.rednewsapp.model.Message.IMAGE_TYPE;
import static com.abidingtech.rednewsapp.services.Utils.PICK_IMAGE;
import static com.abidingtech.rednewsapp.services.Utils.VIDEO_TYPE;

public class TestActivity extends AppCompatActivity implements ChatDAO {
    int index = 0;
    int groupIndex = 0;
    List<Chat> uploadededChat = new ArrayList<>();
    int delIndex = 0;
    Context context;
    String url = Utils.URL;
    List<Image> imagesList;
    ChatView chatView;
    ChatDAO chatDAO;
    String pdfPath,imgPath,selectedVideoPath;
    ObjectCallback fileCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_test);

        chatView = findViewById(R.id.chatView);
        chatDAO = this;
        chatView.setChatDAO(this);


    }

    @Override
    public void getGroupChat(String urls, ObjectCallback<ListPagination> callback) {
        new APIRequest(context).stringReqListPagination(urls, Chat.class, callback);
    }

    @Override
    public void getGroups(String groupUrls, ArrayCallback<GroupModel> callback) {
        new APIRequest(context).getArrayRequest(url + groupUrls, GroupModel.class, callback);
    }

    @Override
    public void getProfile(ObjectCallback<User> callback) {
        new APIRequest(context).getObjectRequest(url + "user", GroupModel.class, callback);

    }

    @Override
    public void getUserById(int id, ObjectCallback<GroupModel> callback) {
        new APIRequest(context).getObjectRequest(url + "group/" + 3, GroupModel.class, callback);
    }

    @Override
    public void getGroupChatById(int id, ObjectCallback<ListPagination> callback) {
        String path = Utils.URL + "group/" + 3 + "/chat";
        {
            getGroupChat(path, callback);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void createMessage(Chat chat, ObjectCallback<Chat> callback) {
        new APIRequest(context).postPutRequest(url + "message", chat, callback, Request.Method.POST);
    }

    @Override
    public void setFilePath(ObjectCallback<FileType> path) {
        fileCallBack=path;
    }


    @Override
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

    @Override
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

    @Override
    public void forwardMsg(Chat chat, ObjectCallback<Chat> callback) {
        new APIRequest(this).postPutRequest(Utils.URL + "message", chat, callback, Request.Method.POST);
    }

    @Override
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

    //    @Override
//    public void onResume() {
//        super.onResume();
//        chatId = groupId;
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(MSG_BROADCAST);
//        this.registerReceiver(this.receiver, filter);
//
//    }
//@Override
//protected void onPause() {
//    super.onPause();
//    chatId = 0;
//    this.unregisterReceiver(this.receiver);
//    releaseRecorder();
//}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileType fileType=new FileType();
Log.e("imagesList","true");
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PREVIEW_CODE) {
                Log.e("imagesList","true2");

                String json = data.getStringExtra("selectedImage");
                Type typeOfObjectsList = new TypeToken<ArrayList<Image>>() {
                }.getType();
                imagesList = new Gson().fromJson(json, typeOfObjectsList);
                if (imagesList != null && imagesList.size() > 0) {
                    for (Image image : imagesList) {
                        String path = image.getPath();
                        if (new File(path).exists()) {
                            int type = Utils.isVideo(path) ? VIDEO_TYPE : IMAGE_TYPE;
                            Log.e("imagesList","true");
                            fileType.path=image.getPath();
                            fileType.type=type;

                            fileCallBack.onData(fileType);

                        }

                    }
                }

            }
            if (requestCode == REQUEST_CODE_PICKER) {
                Log.e("mytag", "showSelectedImages: 1");


            } else if (requestCode == 10) {
                // PdfPath of pdf file
                if (data != null) {
                    pdfPath = getRealPathFromURI(data.getData());
                    if (pdfPath != null) {
                        Log.e("resultok", pdfPath + "");
                    }
                }

            } else if (requestCode == PICK_IMAGE) {
                Uri vid = data.getData();

                imgPath = getRealPathFromURI(vid);
                Log.e("imgPath", imgPath + "");
//                uploadImage();

            } else if (requestCode == 1) {
                Uri vid = data.getData();

                selectedVideoPath = getRealPathFromURI(vid);

                File file = new File(selectedVideoPath);
                int a = Integer.parseInt(String.valueOf(file.length() / 1024));
                if (a > 20000) {
                    Toasty.info(context, "Video limit is 20 Mb", Toasty.LENGTH_SHORT, true).show();
                } else {
//                    uploadVideo();
//                    chatDAO.setFilePath(selectedVideoPath, VIDEO_TYPE);

                }


            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Video.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                assert c != null;
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedVideoPath = c.getString(columnIndex);
                c.close();
                Log.e("SelectedVideoPath", selectedVideoPath);
                File file = new File(selectedVideoPath);
                int a = (int) (file.length() / 1024);

                if (a > 26000) {
                    Toasty.info(context, "Video Limit is 26 Mb", Toasty.LENGTH_SHORT, true).show();
                } else {
//                    uploadVideo();
//                    chatDAO.setFilePath(selectedVideoPath, VIDEO_TYPE);

                }
            }
        }


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.group_option_menu, menu);
//        menuItemInactive = menu.findItem(R.id.btnInActive);
//        menuItemActive = menu.findItem(R.id.btnActive);
//        if (isSuperAdmin)
//            return super.onCreateOptionsMenu(menu);
//        return false;
//    }
//@Override
//protected void onDestroy() {
//    doUnbindService();
//    FetchFactory.getInstance().removeListener();
//    super.onDestroy();
//
//}

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//    }
public String getRealPathFromURI(Uri contentUri) {
    String[] proj = {MediaStore.Images.Media.DATA};
    Cursor cursor = managedQuery(contentUri, proj, null, null, null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(column_index);
}

}
