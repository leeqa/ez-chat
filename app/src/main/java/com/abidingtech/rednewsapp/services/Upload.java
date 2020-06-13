package com.abidingtech.rednewsapp.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.abidingtech.rednewsapp.callback.ProgressCallback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Upload {

    private String filePath;
    File sourceFile;
    private String uploadUrl;
    Context context;


    public Upload(Context context, String filePath, String uploadUrl) {
        if (filePath == null)
            throw new NullPointerException("filePath cannot be null");

        this.context = context;
        this.filePath = filePath;
        sourceFile = new File(filePath);
        this.uploadUrl = uploadUrl;
    }

    public Upload(Context context, File file, String uploadUrl) {
        if (file == null || !file.exists())
            throw new NullPointerException("filePath cannot be null");

        this.context = context;
        this.sourceFile = file;
        this.uploadUrl = uploadUrl;
    }

//    public static final String UPLOAD_URL = "http://192.168.10.53/loyal_pure_api/public/upload/video";

    private int serverResponseCode;

    public void multipartFileUpload(ProgressCallback<String> callback) {
        class UploadVideo extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                upload(callback);
                return null;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    @SuppressLint("HardwareIds")
    private void upload(ProgressCallback<String> callback) {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

//        File sourceFile = new File(filePath);


        if (!sourceFile.isFile()) {
            callback.onError(filePath + " not found");
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(uploadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsdW1lbi1qd3QiLCJpZCI6MywiaWF0IjoxNTkxMjcwNTQ4LCJleHAiOjE1OTM4NjI1NDh9.wVvtzRQ1t0V2lMAvSOuUruk9dAIihFiQciy7WnBSAho");
            conn.setRequestProperty("Fingerprint", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", filePath);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + filePath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            int totalBytes = bytesAvailable;
            Log.e("uploadVideoBytes", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            int totalRead = bytesRead;

            while (bytesRead > 0) {
//                Log.e("uploadVideoBytes: ",bytesRead+"" );
                dos.write(buffer, 0, bufferSize);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                totalRead += bytesRead;
                callback.onProgress(((double) totalRead / totalBytes * 35));
                Thread.sleep(50);
            }
            final int[] progressDone = {40};

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            callback.onProgress(progressDone[0]);

/*            new Thread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProgress(++progressDone[0]);
                            if (progressDone[0] < 100) {
                                handler.postDelayed(this, 1000);
                            }
                        }
                    }, 1000);
                }
            }).start();*/


            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
            progressDone[0] = 100;
            callback.onProgress(progressDone[0]);

        } catch (Exception ex) {
            callback.onError(ex.getMessage());
            ex.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();

            } catch (IOException ex) {
                callback.onError(ex.getMessage());
            }
            Log.e("fileresponse", sb.toString());
            callback.onCompleted(sb.toString());
        } else {
            StringBuilder sb = new StringBuilder();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getErrorStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                callback.onError("not uploaded, code-> " + serverResponseCode + "\n" + sb.toString());
            } catch (Exception ex) {
                callback.onError("error stream error, code-> " + serverResponseCode + "\n" + ex.getMessage());

            }

        }
    }
}
