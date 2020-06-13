package com.abidingtech.rednewsapp.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.abidingtech.rednewsapp.model.Message.BASE_PATH;

public class FetchFactory implements FetchListener {
    public static Fetch fetch;
    private static FetchFactory _this;
//    private static CustomFetchListener _customFetchListener;

    public static FetchFactory getInstance() {
        if (_this == null) {
            _this = new FetchFactory();
        }
        return _this;
    }
    Map<Long,CustomFetchListener> map = new HashMap<>();

    public static FetchFactory getInstance(Context context) {
        if (_this == null) {
            _this = new FetchFactory();
            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                    .setDownloadConcurrentLimit(2)
                    .build();

            fetch = Fetch.Impl.getInstance(fetchConfiguration);
        }
        fetch.addListener(_this);
        return _this;
    }
    public void addListener(long id, CustomFetchListener customFetchListener) {
        map.put(id,customFetchListener);
    }

    public long addDownloadRequest(int id, String url, String dir) {
        Log.e("DOWNLOAD", "URL-> "+url );

        if(url == null || url.isEmpty()){
            return -1;
        }

        File yourAppDir = new File( BASE_PATH + dir);

        if (!yourAppDir.exists() && !yourAppDir.isDirectory()) {
            Log.e("CreateDir", yourAppDir+" ");

            // create empty directory
            if (yourAppDir.mkdirs()) {
                Log.e("CreateDir", "App dir created");
            } else {
                Log.e("CreateDir", "Unable to create app dir!");
            }
        } else {
            Log.e("CreateDir", "App dir already exists");
        }
        String file = yourAppDir + "/" + Uri.parse(url).getLastPathSegment();
        Request request = new Request(url, file);
        request.setIdentifier(id);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        long requestId = request.getIdentifier();
        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
            Log.e("enqueue", "request_id: " + requestId);

        }, error -> {
            Log.e("error", "downloadSong: ");
        });
        return requestId;
    }
    public int addDownloadRequest(String songUrl, boolean isSent, boolean isThumb) {
        String realPath = isThumb ? "redchat/thumb" : isSent ? "redchat/sent" : "redchat/receive";
        File yourAppDir = new File(Environment.getExternalStorageDirectory() + File.separator + realPath);

        if (!yourAppDir.exists() && !yourAppDir.isDirectory()) {

            // create empty directory
            if (yourAppDir.mkdirs()) {
                Log.i("CreateDir", "App dir created");
            } else {
                Log.w("CreateDir", "Unable to create app dir!");
            }
        } else {
            Log.i("CreateDir", "App dir already exists");
        }
        String file = yourAppDir + "/" + Uri.parse(songUrl).getLastPathSegment();
        Request request = new Request(songUrl, file);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        int requestId = request.getId();
        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
            Log.e("enqueue", "request_id: " + requestId);

        }, error -> {
            Log.e("error", "downloadSong: ");
        });
        return requestId;
    }

    @Override
    public void onAdded(@NotNull Download download) {

    }

    @Override
    public void onQueued(@NotNull Download download, boolean b) {

    }

    @Override
    public void onWaitingNetwork(@NotNull Download download) {

    }

    @Override
    public void onCompleted(@NotNull Download download) {
        Log.e("DOWNLOAD", "COMPLETED_ID-> "+download.getIdentifier() );
        CustomFetchListener listener = map.get(download.getIdentifier());
        if(listener != null){
            listener.onCompleted(download);
        }

    }

    @Override
    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {

    }

    @Override
    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

    }

    @Override
    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
        CustomFetchListener listener = map.get(download.getIdentifier());
        if(listener != null){
            listener.onStart(download);
        }
    }

    @Override
    public void onProgress(@NotNull Download download, long l, long l1) {
        Log.e("DOWNLOAD", "PROGRESS-> "+download.getIdentifier()+"  "+l+"  "+l1 );
        CustomFetchListener listener = map.get(download.getIdentifier());
        if(listener != null){
            listener.onProgress(download,l,l1);
        }
    }

    @Override
    public void onPaused(@NotNull Download download) {
        CustomFetchListener listener = map.get(download.getIdentifier());
        if(listener != null){
            listener.onPaused(download);
        }
    }

    @Override
    public void onResumed(@NotNull Download download) {
        CustomFetchListener listener = map.get(download.getIdentifier());
        if(listener != null){
            listener.onResumed(download);
        }
    }

    @Override
    public void onCancelled(@NotNull Download download) {

    }

    @Override
    public void onRemoved(@NotNull Download download) {

    }

    @Override
    public void onDeleted(@NotNull Download download) {

    }

    public void removeListener() {
        if (fetch != null) {
            fetch.removeListener(this);
        }
    }

    public void pauseDownload(int requstId) {
        if (fetch != null) {
            fetch.pause(requstId);
        }
    }

    public void resumeDownload(int requstId) {
        if (fetch != null) {
            fetch.resume(requstId);
        }
    }
}

