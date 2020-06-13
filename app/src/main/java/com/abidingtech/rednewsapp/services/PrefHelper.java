package com.abidingtech.rednewsapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.abidingtech.rednewsapp.jsonhelper.JsonParser;
import com.abidingtech.rednewsapp.model.Chat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrefHelper {
    private SharedPreferences preferences;
    private Context context;
    public PrefHelper(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences("data_pref", Context.MODE_PRIVATE);
    }
    public void saveValue(String key, String value){
        Log.e("saveValue: ",key+"-> "+value );

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public void saveValue(String key, boolean bool){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,bool);
        editor.apply();
    }
    public void saveValue(String key, Object object){
        SharedPreferences.Editor editor = preferences.edit();
        Log.e("saveValue: ",key+"-> "+new Gson().toJson(object) );
        editor.putString(key,new Gson().toJson(object));
        editor.apply();
    }
    public void saveValueInChatList(String key, Map<String,String> map){
        Gson gson = new Gson();
        Log.e("saveValueInChatList: ",gson.toJson(map)+"" );
        Chat chat = gson.fromJson(gson.toJson(map),Chat.class);
        saveValueInChatList(key,chat);
    }
    public void saveValueInChatList(String key, Chat chat){
        List<Chat> list = getArray(key,chat.getClass());
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(chat);
        if(list.size()>1)
            list.remove(0);
        saveValue(key,list);
    }
    public String getValue(String key){
        return preferences.getString(key,null);
    }
    public <T> T getObject(String key,Class clazz){
        String value = getValue(key);
        Log.e("getValue: ",key+"-> "+value);
        return value == null ? null : JsonParser.toObject(value, clazz);

    }
    public <T> List<T> getArray(String key,Class clazz){
        String value = getValue(key);
        return value == null ? null : JsonParser.toList(value, clazz);

    }



/*    public void setHideIntro(){
        saveValue("hide_intro",true);
    }
    public boolean isHideIntro(){
        return preferences.getBoolean("hide_intro",false);
    }
    public String gettingStartedActivity(){
        return getCountry() == null? "countrySelection" : "home";
    }
    public void setCountry(String name){
        saveValue("country",name);
    }
    public String getCountry(){
        return preferences.getString("country",null);
    }
    public void setNotificationFrequency(String time){ saveValue("frequencyTime",time); }
    public String getNotificationFrequency(){return preferences.getString("frequencyTime","20");}
    public void setEnable(boolean isEnable){
       saveValue(CustomConstants.IS_ENABLED,isEnable);
    }
    public  boolean getEnabled(){
        return preferences.getBoolean(CustomConstants.IS_ENABLED,true);
    }
    public boolean getIsInstalled(){
        return preferences.getBoolean(CustomConstants.ISINSTALLED,true);
    }
    public void setIsInstalled(boolean bool){
        saveValue(CustomConstants.ISINSTALLED,bool);
    }*/


}
