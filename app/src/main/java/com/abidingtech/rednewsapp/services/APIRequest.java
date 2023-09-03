package com.abidingtech.rednewsapp.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;


import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ArrayCallback;
import com.abidingtech.rednewsapp.callback.BaseCallback;
import com.abidingtech.rednewsapp.callback.ObjectCallback;
import com.abidingtech.rednewsapp.jsonhelper.JsonParser;
import com.abidingtech.rednewsapp.model.ListPagination;
import com.abidingtech.rednewsapp.model.Pagination;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class APIRequest {

    private RequestQueue requestQueue;
    private boolean cached = true;
    private Context context;

    public APIRequest(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public <T> void postPutRequest(String url, final Object object, final ObjectCallback<T> callback, int req) {
        Log.e("postPutRequestUrl", url + "");
        Log.e("postPutObject => " + object.
                getClass().getName(), new Gson().toJson(object) + "");
        try {
            JSONObject jsonObject = null;

            if (req != Request.Method.GET && req != Request.Method.DELETE) {
                jsonObject = JsonParser.toJSON(object);
            }

            JsonObjectRequest request = new JsonObjectRequest(req, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Response", response + "");
                    if (callback != null) {

                        callback.onData((T) new Gson().fromJson(response.toString(), object.getClass()));


                    }
                }
            }, error -> {
                Log.e("ResponseERROR", error.getMessage() + "");

                if (callback != null) {
                    setErrorMsg(error, callback);

//                    callback.onError(error.toString());
//                    setError(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.e("headercall", url);

                    return getHeader();
                }
            };
            //store post
            //description on post
            addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void postPutRequestV1(String url, final Object object, Class clazz, final ObjectCallback<T> callback, int req) {
        Log.e("postPutRequestUrl", url + "");
        try {
            JSONObject jsonObject = null;

            if (req != Request.Method.GET) {
                jsonObject = JsonParser.toJSON(object);
            }

            JsonObjectRequest request = new JsonObjectRequest(req, url, jsonObject, response -> {
                Log.e("Response", response + "");
                if (callback != null) {

                    callback.onData((T) new Gson().fromJson(response.toString(), clazz));


                }
            }, error -> {
                Log.e("ResponseERROR", error.getMessage() + "");

                if (callback != null) {
                    setErrorMsg(error, callback);

//                    callback.onError(error.toString());
//                    setError(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.e("headercall", url);

                    return getHeader();
                }
            };
            //store post
            //description on post
            addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void getObjectRequest(String url, final Class clazz, final ObjectCallback<T> callback) {
        Log.e("getObjectRequestUrl", url + "");
        PrefHelper prefHelper = new PrefHelper(context);
        T data = prefHelper.getObject(url,clazz);
        if (data != null) {
            callback.onData(data);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Log.e("Response", response + "");
            T res = JsonParser.toObject(response.toString(), clazz);
            prefHelper.saveValue(url, res);
            if (callback != null) {
                callback.onData(res);
            }
        }, error -> {
            Log.e("ResponseERROR", error.getMessage() + "");

            if (callback != null) {
                setErrorMsg(error, callback);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return getHeader();
            }
        };
        addRequest(request);
    }

    public <T> void makeRequest(String url, final Object object, Class clazz, final ObjectCallback<T> callback, int req) {
        Log.e("makeRequestUrl", url + "");

        try {
            JSONObject jsonObject = null;
            if (req != Request.Method.GET) {
                jsonObject = JsonParser.toJSON(object);
            }
            Request request = new JsonObjectRequest(req, url, jsonObject, response -> {
                Log.e("Response", response + "" + clazz.getSimpleName());
                if (callback != null) {
                    callback.onData((T) new Gson().fromJson(response.toString(), clazz));
                }
            }, error -> {
                Log.e("ResponseERROR", error.getMessage() + "");

                if (callback != null) {
//                    callback.onError(error.toString());
                    setErrorMsg(error, callback);
                }

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    return getHeader();
                }
            };
            addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void getArrayRequest(String url, final Class clazz, final ArrayCallback<T> callback) {
        Log.e("getArrayRequestUrl: ", url);
        PrefHelper prefHelper = new PrefHelper(context);
        List list = prefHelper.getArray(url, clazz);
        if (list != null) {
            callback.onData(list);
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            if (callback != null) {
                Log.e("Response", "" + response.toString());
                prefHelper.saveValue(url, response.toString());
                callback.onData(JsonParser.toList(response.toString(), clazz));
            }
        }, error -> {

            if (callback != null) {
                setErrorMsg(error, callback);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();

            }
        };
        addRequest(request);
    }

    ////////////////////////////////////// String Requests ////////////////////////////////////

    public <T> void stringObjectReq(String url, Class clazz, final ObjectCallback<T> callback, int Request) {
        Log.e("url->>", url);
        try {
            StringRequest request = new StringRequest(Request, url, response -> {
                Log.e("Response", response + "" + clazz.getSimpleName());

                if (callback != null) {
                    callback.onData((T) new Gson().fromJson(response.toString(), clazz));

                }
            }, error -> {
                if (callback != null) {
                    setErrorMsg(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Log.e("headercall", "true");
                    return getHeader();
                }
            };

//            RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
//                    0,
//                    0,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void stringDeleteReq(String url, final ObjectCallback<T> callback) {
        Log.e("url->>", url);
        try {
            StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {

                if (callback != null) {
                    callback.onData((T) response.toString());

                }
            }, error -> {
                if (callback != null) {
                    setErrorMsg(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getHeader();
                }
            };
            addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void stringArrayReq(String url, Class clazz, final ArrayCallback<T> callback) {
        Log.e("url", url);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                Log.e("Response", response + "" + clazz.getSimpleName());
                if (callback != null) {
                    Log.e("Response", "" + response.toString());
                    callback.onData(JsonParser.toList(response.toString(), clazz));
                }
            }, error -> {
                if (callback != null) {
                    setErrorMsg(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getHeader();
                }

            };

            addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    public <T> void stringReqPagination(String url, Class clazz, final ObjectCallback<T> callback) {
        Log.e("url", url);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, url, response -> {

                Log.e("Response", response + "" + clazz.getSimpleName());
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    Pagination pagination = new Pagination();
                    pagination.next_page_url = jsonObject.getString("next_page_url");
                    pagination.prev_page_url = jsonObject.getString("prev_page_url");
                    pagination.data = (T) new Gson().fromJson(jsonObject.getString("data"), clazz);
                    callback.onData((T) pagination);
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                    Log.e("Response", "onResponse: " + e.getMessage());
                    e.printStackTrace();
                }
            }, error -> {
                if (callback != null) {
//                    callback.onError(error.toString());
                    setErrorMsg(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return getHeader();
                }

            };

            addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    private <T> ListPagination makePaginationObject(String response, Class clazz) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        List list = JsonParser.toList(jsonObject.getString("data"), clazz);
        ListPagination pagination = new ListPagination();
        pagination.next_page_url = jsonObject.getString("next_page_url");
        pagination.prev_page_url = jsonObject.getString("prev_page_url");
        pagination.data = list;
        return pagination;
    }

    public <T> void stringReqListPagination(String url, Class clazz, final ObjectCallback<T> callback) {
        Log.e("url", ""+url);

        if(true){
            ListPagination pagination = new ListPagination();
            pagination.next_page_url = "sdsd";//jsonObject.getString("next_page_url");
            pagination.data = new ArrayList();
            callback.onData((T) pagination);
            return;
        }


        try {
            StringRequest request = new StringRequest(Request.Method.GET, url, response -> {

                Log.e("Response", response + "" + clazz.getSimpleName());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    List<T> list = JsonParser.toList(jsonObject.getString("data"), clazz);

                    ListPagination pagination = new ListPagination();
                    pagination.next_page_url = jsonObject.getString("next_page_url");
                    pagination.data = list;
                    callback.onData((T) pagination);
                } catch (JSONException e) {
                    callback.onError(e.getMessage());
                    Log.e("Response", "onResponse: " + e.getMessage());
                    e.printStackTrace();
                }
            }, error -> {
                if (callback != null) {
                    setErrorMsg(error, callback);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getHeader();
                }

            };

            addRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }



    @SuppressLint("HardwareIds")
    private Map<String, String> getHeader() {
        Map<String, String> map = new HashMap<>();
        Log.e("Header",  "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsdW1lbi1qd3QiLCJpZCI6MywiaWF0IjoxNTkxMjcwNTQ4LCJleHAiOjE1OTM4NjI1NDh9.wVvtzRQ1t0V2lMAvSOuUruk9dAIihFiQciy7WnBSAho");
        Log.e("Fingerprint", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "");
        map.put("Fingerprint", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        map.put("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsdW1lbi1qd3QiLCJpZCI6MywiaWF0IjoxNTkxMjcwNTQ4LCJleHAiOjE1OTM4NjI1NDh9.wVvtzRQ1t0V2lMAvSOuUruk9dAIihFiQciy7WnBSAho");
        return map;
    }


    private void setErrorMsg(VolleyError error, BaseCallback callback) {
        NetworkResponse response = error.networkResponse;

            if (response != null) {
                String errorMsg = new String(error.networkResponse.data).replaceAll("^\"|\"$", "");
                String codeMsg = new String(response.data) + "";

//            Log.e("setError", "code-> "+response.statusCode);
                Log.e("setError", codeMsg + "  ");
                switch (response.statusCode) {
                    case 401:
                    case 413:
                        callback.onError(errorMsg);
//                    refreshToken(msg);

                        break;
                    case 500:
                    case 404:
                    case 403:
                    case 422:
                        callback.onError(errorMsg);
                        break;
                    case 444:
                        break;

                    default:
                        callback.onError(errorMsg + "");
                        break;
                }
            } else {
                callback.onError("Something Went Wrong...");
            }
    }


    private void addRequest(Request request) {

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        request.setShouldCache(cached);
        requestQueue.add(request);
    }

}
