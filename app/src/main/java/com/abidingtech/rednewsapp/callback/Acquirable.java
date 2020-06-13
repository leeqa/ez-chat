package com.abidingtech.rednewsapp.callback;

import com.abidingtech.rednewsapp.model.Chat;
import com.abidingtech.rednewsapp.model.Message;

public interface Acquirable {
    void onAcquire(Message message);
    void onLongPress(Chat chat);
}
