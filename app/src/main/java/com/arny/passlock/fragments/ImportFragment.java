package com.arny.passlock.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arny.passlock.R;

import static com.arny.passlock.fragments.BookMarkFragment.TAG;

public class ImportFragment extends Fragment {
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.import_layout, container, false);
        this.context = container.getContext();
        getbookmarks();
        return view;
    }


    private void getbookmarks() {

       try{
           Log.i(TAG, "getbookmarks: ");
//        final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
//        Log.i(Const.TAG, "getbookmarks: BOOKMARKS_URI = " + );


//           final Uri uri = Uri.parse("content://com.android.chrome.browser/bookmarks");
           final Uri uri = Uri.parse("content://browser/bookmarks");
//           final Uri uri = Uri.parse("content://com.yandex.browser/bookmarks");
           Log.i(TAG, "getbookmarks: uri = " + uri);


           Thread myThread = new Thread( // создаём новый поток
                   new Runnable() { // описываем объект Runnable в конструкторе
                       public void run() {
                           try {
                               Cursor c = context.getContentResolver().query(uri, null, null, null, null);
                               Log.i(TAG, "getbookmarks: c = " + c);
                               Log.i(TAG, "run: c =" + c);
                               if (c!=null){
                                   Log.i(TAG, "run: c.getColumnCount() = " + c.getColumnCount());
                               }
                               if (c!=null) {
                                   Log.i(TAG, "run: c 1= " + c.getColumnName(1));
                                   Log.i(TAG, "run: c 2= " + c.getColumnName(2));
                                   Log.i(TAG, "run: c 3= " + c.getColumnName(3));
                                   Log.i(TAG, "run: c 4= " + c.getColumnName(4));
                                   Log.i(TAG, "run: c 5= " + c.getColumnName(5));
                                   Log.i(TAG, "run: c 6= " + c.getColumnName(6));
                                   for (int i = 0; i < c.getColumnCount(); i++) {
                                       Log.i(TAG, "run: c.getString(1); = " + c.getString(i));
                                   }
                               }
                               Log.i(TAG, "doInBackground: end");
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                   }
           );
           myThread.start();
           Log.i(TAG, "getbookmarks: myThread.getState() = " + myThread.getState().toString());

       }catch (Exception e){
           Log.e(TAG, "getbookmarks: error", e);
       }
    }
}