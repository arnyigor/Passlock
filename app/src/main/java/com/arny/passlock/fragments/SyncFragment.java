package com.arny.passlock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.arny.passlock.R;
import com.arny.passlock.helpers.ParseJSON;

import java.util.Arrays;
import java.util.Date;

public class SyncFragment extends Fragment {

    private TextView tvSyncText;
    private Context context;
    private static final String TAG = "LOG_TAG";
    RequestQueue requestQueue;
    String resultData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        if(savedInstanceState!=null){
            Log.i("STATE",savedInstanceState.toString());
        }
        View view = inflater.inflate(R.layout.sync_layout, container, false);
        this.context = container.getContext();
        initUI(view);
        return view;
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