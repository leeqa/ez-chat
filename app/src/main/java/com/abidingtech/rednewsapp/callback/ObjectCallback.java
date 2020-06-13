package com.abidingtech.rednewsapp.callback;

public interface ObjectCallback<T> extends BaseCallback{
    void onData(T t);
}
