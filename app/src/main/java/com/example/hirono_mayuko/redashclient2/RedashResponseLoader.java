package com.example.hirono_mayuko.redashclient2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;

import com.example.hirono_mayuko.redashclient2.model.Redash;
import com.example.hirono_mayuko.redashclient2.model.RedashResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class RedashResponseLoader extends AsyncTaskLoader {
    private String mUrl;
    private String mApiKey;
    private String mProxyDomain;
    private String mProxyPortNumber;
    private static final String REDASH_API = "%1$s/api/queries/my?api_key=%2$s";

    public RedashResponseLoader(Context c, String url, String apiKey, @Nullable String proxyDomain, @Nullable String proxyPortNumber) {
        super(c);
        mUrl = url;
        mApiKey = apiKey;
        mProxyDomain = proxyDomain;
        mProxyPortNumber = proxyPortNumber;
    }

    @Override
    public RedashResponse loadInBackground() {
        String api = String.format(REDASH_API, mUrl, mApiKey);
        System.out.println(api);
        Request req = new Request.Builder().url(api).build();
        RedashResponse redashResponse = new RedashResponse();
        Redash r = redashResponse.getRedash();
        r.setUrl(mUrl);
        r.setApiKey(mApiKey);
        OkHttpClient c;
        if(mProxyDomain != null){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(mProxyDomain, Integer.parseInt(mProxyPortNumber)));
            c = new OkHttpClient.Builder().proxy(proxy).build();
            r.setProxy(true);
            r.setProxyUrl(mProxyDomain);
            r.setProxyPortNumber(mProxyPortNumber);
        } else {
            c = new OkHttpClient();
            r.setProxy(false);
        }
        redashResponse.setErrorMessage(null);
        try {
            Response response = c.newCall(req).execute();
            redashResponse.setSuccessful(response.isSuccessful());
            String body = response.body().string();
            JSONObject json = null;
            json = new JSONObject(body);
            if(!redashResponse.isSuccessful()){
                redashResponse.setErrorMessage(json.getString("message"));
            }
            response.close();
        } catch (IOException | JSONException e) {
            redashResponse.setSuccessful(false);
            e.printStackTrace();
            if(e instanceof UnknownHostException){
                redashResponse.setUnknownHostException(true);
                redashResponse.setSocketTimeoutException(false);
            }
            if(e instanceof SocketTimeoutException){
                redashResponse.setUnknownHostException(false);
                redashResponse.setSocketTimeoutException(true);
            }
        }
        return redashResponse;
    }
}
