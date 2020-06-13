package com.abidingtech.rednewsapp.callback;

import java.util.List;

public interface ArrayCallbackV1<T> extends BaseCallback{
    void onData(List<T> list,String type);

}
