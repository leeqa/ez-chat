package com.abidingtech.rednewsapp.model;

import java.util.List;

public class GroupModel {

    public int id, user_id, media_type_id;

    public String name, image_name, thumbnail, image_url, msg, message_body, last_message_time,user_name,creator,date;
    public int group_id;
    public boolean is_admin, is_active;
    public List<UserId> members;
    public transient  boolean isSelected;

    public String firstName(){
        if(name != null){
            return name.split("\\s+")[0];
        }
        return name;
    }

}

