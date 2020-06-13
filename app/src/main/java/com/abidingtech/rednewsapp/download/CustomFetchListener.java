package com.abidingtech.rednewsapp.download;

import com.tonyodev.fetch2.Download;

import org.jetbrains.annotations.NotNull;

public interface CustomFetchListener{
    void onCompleted(@NotNull Download download);

    void onPaused(@NotNull Download download);

    void onProgress(@NotNull Download download, long l, long l1);

    void onResumed(@NotNull Download download);

    void onStart(Download download);
}
