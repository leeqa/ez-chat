package com.abidingtech.rednewsapp.services;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Objects;

public class MapHelper {

    private Map<String,String> map;

    public MapHelper(@NonNull Map<String,String> map) {
        this.map = map;
    }

    public boolean getBoolean(String key){
        return Boolean.parseBoolean(map.get(key));
    }
    public String getString(String key){
        return map.get(key);
    }
    public Object get(String key){
        return map.get(key);
    }
    public String [] getArray(String key){
        if(!map.containsKey(key))
            return new String[]{};

        return new Gson().fromJson(map.get(key), new TypeToken<String[]>() {}.getType());
    }
    public int getInteger(String key){
        try{
            return Integer.parseInt(Objects.requireNonNull(map.get(key)));
        }
        catch (Exception e){
            return -1;
        }
    }
}
