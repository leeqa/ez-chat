package com.abidingtech.rednewsapp.model;


public class User {

    public String name, address, phone, token, type, image_url, complain_time, thumbnail, designation_name,tehsil_name;
    //
    public int id,version_code;
    public String user_name, password, image_name, old_password, new_password, otp;
    public boolean is_applied, is_representative,is_program_applied;

    public User() {
    }


    public String firstname(){
        if(name!=null){
           return name.split("\\s+")[0];
        }
        return name;
    }

}
