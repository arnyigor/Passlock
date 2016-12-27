package com.arny.passlock.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arny.passlock.R;
import com.arny.passlock.helpers.Functions;
import com.arny.passlock.helpers.ParseJSON;

import java.util.Arrays;
import java.util.Date;

public class SyncFragment extends Fragment {

    private final String URL="http://develop.passlock.ru/time";
    private TextView tvSyncText;
    private Context context;
    boolean connected;
    private static final String TAG = "LOG_TAG";
    RequestQueue requestQueue;
    String resultData;
    Functions func = Functions.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        setRetainInstance(true);
        if(savedInstanceState!=null){
            Log.i("STATE",savedInstanceState.toString());
        }
        View view = inflater.inflate(R.layout.sync_layout, container, false);
        this.context = container.getContext();
        initUI(view);
        connected = isNetworkEnable();
        Log.i(TAG, "onResume: isNetworkEnable ? =  " + connected);
        if (connected){
            Log.i(TAG, "onCreateView: initRequest!!!");
            initRequest(URL);
        }else{
            Toast.makeText(context, R.string.str_network_not_connect, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void initRequest(String s) {
        Log.i(TAG, "initRequest: s = " + s);
        requestQueue = Volley.newRequestQueue(context);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                s, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                showTime(response);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley","Error");
                volleyError.printStackTrace();
            }
        });
       /* JsonObjectRequest jor = new JsonObjectRequest(Request.Method.HEAD, s, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: response = " + response.toString());
                        showTime(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley","Error");
                        error.printStackTrace();
                    }
                }
        );*/
        //Adding request to the queue
        requestQueue.add(strReq);

    }

    private void showTime(String s) {
        Log.i(TAG, "showTime: s = " + s);
        long curTime = (System.currentTimeMillis() / 1000L);
        Log.i(TAG, "showTime: curTime = " + curTime);
        long serverTime = Long.parseLong(s);
        Log.i(TAG, "showTime: serverTime = "  +serverTime);
        long serverAppTime = Math.abs(curTime - serverTime);
        Log.i(TAG, "showTime: serverAppTime = " + serverAppTime);
        Date servTime=new Date(serverTime*1000);
        Date appTime=new Date(curTime*1000);
        Log.i(TAG, "showTime: servTime = " + servTime);
        Log.i(TAG, "showTime: appTime = " + appTime);
        Log.i(TAG, "showTime: serverApp = " + serverAppTime);
        resultData = "Server sec = " + s + "; Server time = " + servTime + ";\n App sec =" + curTime + "; AppTime = " + appTime + ";\n serverAppDiff = " + serverAppTime;
        tvSyncText.setText(resultData);
    }

    private void showJSON(String json){
        Log.i(TAG, "showJSON: json = " + json);
        ParseJSON pj = new ParseJSON(json);
        pj.parseJSON();
        Log.i(TAG, "showJSON: ParseJSON.ids = " + Arrays.toString(ParseJSON.ids));
        Log.i(TAG, "showJSON: ParseJSON.names = " + Arrays.toString(ParseJSON.names));
        Log.i(TAG, "showJSON: ParseJSON.emails = " + Arrays.toString(ParseJSON.emails));
        resultData = "";
        resultData = Arrays.toString(ParseJSON.ids) + Arrays.toString(ParseJSON.names) + Arrays.toString(ParseJSON.emails);
        tvSyncText.setText(resultData);

    }

    private boolean isNetworkEnable() {
        Log.i(TAG, "isNetworkEnable: ");
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.i(TAG, "isNetworkEnable: networkInfo = " + networkInfo);
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    private void initUI(View view) {
        tvSyncText = (TextView) view.findViewById(R.id.tvSyncText);
        tvSyncText.setText("LoadText");
    }
}