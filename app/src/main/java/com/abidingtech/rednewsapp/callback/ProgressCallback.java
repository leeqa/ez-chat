package com.abidingtech.rednewsapp.callback;

public interface ProgressCallback<T> extends BaseCallback{
    void onCompleted(T t);
    void onProgress(double progress);
}
