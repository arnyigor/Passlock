package com.arny.passlock.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static com.arny.passlock.helpers.Const.ITEM_TYPE_FOLDER;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "LOG_TAG";
    // Database Info
    private static final String DATABASE_NAME = "Passlock";
    private static final int DATABASE_VERSION = 3;
    // Table Names
    private static final String TABLE_BOOKMARKS = "bookmarks";
    // Table Columns
    private static final String KEY_ID = "_id";
    private static final String KEY_LINK = "link";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PARENT = "parent";
    private static final String KEY_DATETIME = "datetime";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        Log.i(TAG, "onConfigure: ");
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: db");
        String create_bookmark_table = "CREATE TABLE " + TABLE_BOOKMARKS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY"// Define a primary key
                + " , " + KEY_LINK + " TEXT"
                + " , " + KEY_TITLE + " TEXT"
                + " , " + KEY_PARENT + " INTEGER"
                + " , " + KEY_TYPE + " INTEGER"
                + " , " + KEY_DATETIME + " TIMESTAMP"// Define a foreign key
                + ")";
        Log.i(TAG, "onCreate: CREATE_TODO_TABLE = " + create_bookmark_table);
        db.execSQL(create_bookmark_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
            onCreate(db);
        }
    }

    // update
    public boolean updateItem(Items item) {
        Log.i(TAG, "updateItem: item = " + item.toString());
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, item.getTitle());
            values.put(KEY_LINK, item.getLink());
            values.put(KEY_DATETIME, item.getDatetime());
            String where = String.format("%s=%s",KEY_ID,item.getID());
            Log.i(TAG, "updateItem: where = " +  where);
            db.beginTransaction();
            int updCount = 0;
            try {
                updCount = db.update(TABLE_BOOKMARKS, values, where, null);
                Log.i(TAG, "updateItem: updates rows count = " +  updCount);
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
        return updCount > 0;
    }

    //Delete
    public boolean removeItems(int type) {
        String condition = KEY_TYPE + "=" + type;
        Log.i(TAG, "removeItems: condition = " + condition);
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        int delCount = 0;
        try {
            delCount = db.delete(TABLE_BOOKMARKS, condition, null);
            Log.i(TAG, "removeItems: delCount  = " +  delCount);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return delCount > 0;
    }

    //Delete
    public boolean removeBookmark(int id) {
        String condition = KEY_ID + "=" + id;
        Log.i(TAG, "removeBookmark: condition = " + condition);
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_BOOKMARKS, condition, null) > 0;
    }

    // Insert into the database
    public long addItem(Items item) {
        Log.i(TAG, "addItem: item = " + item.toString());
        if (item.getDatetime() == 0) {
            item.setDatetime(Calendar.getInstance().getTimeInMillis());
        }
        long bookarkId = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, item.getTitle());
            values.put(KEY_LINK, item.getLink());
            values.put(KEY_PARENT, item.getParent());
            values.put(KEY_TYPE, item.getType());
            values.put(KEY_DATETIME, item.getDatetime());
            bookarkId = db.insertOrThrow(TABLE_BOOKMARKS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error addFolder");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return bookarkId;
    }

    //Get
    public ArrayList<Items> getItemsList() {
        Log.i(TAG, "getItemsList: ");
        String selectQuery = String.format("SELECT * FROM %s ORDER BY %s ASC", TABLE_BOOKMARKS, KEY_TYPE);
        Log.i(TAG, "getItemsList: selectQuery = " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Items> itemList = getItemsLists(cursor);
        Log.i(TAG, "getFoldersList: itemList size= " + itemList.size());
        cursor.close();
        return itemList;
    }

    //Get
    public ArrayList<Items> getItemsByText(String text) {
        String selectQuery = "SELECT * FROM " + TABLE_BOOKMARKS + " WHERE (" + KEY_TITLE + " LIKE  '%" + text + "%' OR " + KEY_LINK + " LIKE '%" + text + "%') AND "+KEY_TYPE+"<>"+ITEM_TYPE_FOLDER;
        Log.i(TAG, "getItemsByText: selectQuery = " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Items> itemList = getItemsLists(cursor);
        Log.i(TAG, "getItemsByText: itemList size = " + itemList.size());
        cursor.close();
        return itemList;
    }

    //Get
    public ArrayList<Items> getItemById(int id) {
        String selectQuery = String.format("SELECT * FROM %s WHERE %s=%s", TABLE_BOOKMARKS, KEY_ID, id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Items> itemList = getItemsLists(cursor);
        Log.i(TAG, "getFoldersList: itemList size= " + itemList.size());
        cursor.close();
        return itemList;
    }

    //Get
    public ArrayList<Items> getItemsByParent(int parent) {
        Log.i(TAG, "getItemsByParent: ");
        String selectQuery = String.format("SELECT * FROM %s WHERE %s=%s  ORDER BY %s ASC", TABLE_BOOKMARKS, KEY_PARENT, parent, KEY_TYPE);
        Log.i(TAG, "getItemsByParent: selectQuery = " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Items> itemList = getItemsLists(cursor);
        Log.i(TAG, "getItemsByParent: itemList size= " + itemList.size());
        cursor.close();
        return itemList;
    }

    private ArrayList<Items> getItemsLists(Cursor cursor) {
        Log.i(TAG, "getItemsLists: ");
        ArrayList<Items> itemList = new ArrayList<>();
        Log.i(TAG, "getItemsLists: cursor.getCount() = " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Items items = new Items();
                items.setID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                items.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                items.setLink(cursor.getString(cursor.getColumnIndex(KEY_LINK)));
                items.setParent(cursor.getInt(cursor.getColumnIndex(KEY_PARENT)));
                items.setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
                items.setDatetime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_DATETIME))));
                Log.i(TAG, "getItemsLists: list = " + items.toString());
                itemList.add(items);
            } while (cursor.moveToNext());
        }
        return itemList;
    }

}

