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

public class FileType {
    public String path;
    public int type;
}