package com.abidingtech.rednewsapp.callback;

import java.util.List;

public interface ArrayCallback<T> extends BaseCallback{
    void onData(List<T> list);

}
