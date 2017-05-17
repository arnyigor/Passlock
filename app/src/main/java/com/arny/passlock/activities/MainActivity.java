package com.arny.passlock.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.arny.passlock.R;
import com.arny.passlock.fragments.BookMarkFragment;
import com.arny.passlock.fragments.ImportFragment;
import com.arny.passlock.fragments.SyncFragment;
import com.arny.passlock.helpers.Const;
import com.arny.passlock.helpers.TitleExtractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LOG_TAG";
    // =============Variables start================
    Context context = this;
    private String[] mScreenTitles;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String action, type, sharedLink;
    private Intent intent;
    ImageView user_image;
    private static final int PICKFILE_RESULT_CODE = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 110;
    private static final int SAVE_FILE_RESULT_CODE = 111;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Intent fileintent;
    Element container;

    // ====================onCreate start=========================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ---------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScreenTitles = getResources().getStringArray(R.array.drawer_items);
        initDrawer(savedInstanceState);
        intent = getIntent();
        action = intent.getAction();
        type = intent.getType();
        Log.i(TAG, "onCreate: action = " + action);
        Log.i(TAG, "onCreate: type = " + type);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
//        initQuitDialog();
    }// ============onCreate end====================

   /* private void initQuitDialog() {
        boolean extraBack = false;
        Log.i(TAG, "initQuitDialog: intent.getExtras() = " + intent.getExtras());
        try{
            extraBack = intent.getExtras().getBoolean(Const.KEY_EXTRA_BACK);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.i(TAG, "onCreate: extraBack = " + extraBack);
    }*/

    void handleSendText(Intent intent) {
        sharedLink = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedLink != null) {
            GetTitleThread getTitle = new GetTitleThread();
            getTitle.start();
        }
    }

    private class GetTitleThread extends Thread {
        @Override
        public void run() {
            String title;
            try {
                title = TitleExtractor.getPageTitle(sharedLink);
                Log.i(TAG, "handleSendText: title = " + title);
                Log.i(TAG, "handleSendText: sharedText = " + sharedLink);
                sendSharedToBookmarks(title, sharedLink);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSharedToBookmarks(String title, String sharedLink) {
        Fragment fragment = getFragment(0);
        // Insert the fragment by replacing any existing fragment
        try {
            if (fragment != null) {
                Bundle args = new Bundle();
                args.putString(Const.KEY_EXTRA_TITLE, title);
                args.putString(Const.KEY_EXTRA_LINK, sharedLink);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            } else {
                // Error
                Log.e(this.getClass().getName(), "Error. Fragment is not created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawer(mDrawerLinearLayout);
            showDialog();
            Toast.makeText(context, "Image clicked", Toast.LENGTH_SHORT).show();
        }
    };

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle("Вы хотите загрузить новую аватарку");
        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                })
                .setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void initDrawer(Bundle savedInstanceState) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinearLayout = (LinearLayout) findViewById(R.id.navdrawer);
        user_image = (ImageView) findViewById(R.id.account_image);
        try {
            user_image.setOnClickListener(imgClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDrawerList = (ListView) findViewById(R.id.list_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mScreenTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_launcher);
            mTitle = mDrawerTitle = getTitle();
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START); // установка тени к NavDrawer
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // Update the main content by replacing fragments
        Fragment fragment = getFragment(position);
        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(mScreenTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerLinearLayout);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        try {
            getSupportActionBar().setTitle(mTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private Fragment getFragment(int position) {
        Log.i(TAG, "getFragment: position = " + position);
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new BookMarkFragment();
                break;
            case 1:
                fragment = new SyncFragment();
                break;
            case 2:
                fragment = new ImportFragment();
                break;
            default:
                fragment = new BookMarkFragment();
                break;
        }
        return fragment;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(context, "action_settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_import:
                showImportDialogSD();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void showImportDialogSD() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(getString(R.string.alert_import_title));
        alert.setMessage(getString(R.string.alert_import_message));
        alert.setNegativeButton(getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    boolean mlolipop = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
                    boolean permissionGranded;
                    Log.i(TAG, "onClick: mlolipop = " + mlolipop);
                    if (mlolipop){
                        permissionGranded = verifyStoragePermissions(MainActivity.this);// Do something for lollipop and above versions
                        Log.i(TAG, "onClick: permissionGranded = " + permissionGranded);
                        if (!permissionGranded){
                            Toast.makeText(MainActivity.this, getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
                        }else{
                            sendOpenTypeFile();
                        }
                    }else{
                        sendOpenTypeFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getString(R.string.error_fileopen), Toast.LENGTH_SHORT).show();
                }

            }
        });
        alert.show();
    }

    private void sendOpenTypeFile() {
        Log.i(TAG, "sendOpenTypeFile: sendOpenTypeFile");
        fileintent = new Intent();
        fileintent.setAction(Intent.ACTION_GET_CONTENT);
        fileintent.addCategory(Intent.CATEGORY_OPENABLE);
        fileintent.setType("*/*");
        startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    Log.i(TAG, "onActivityResult: FilePath = " + FilePath);
                    importFile(getBaseContext(),FilePath);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.error_fileopen), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Element parseBookmarkFolders(Element element){
        Elements folders = element.select("dl");
//        Log.i(TAG, "parseBookmarkFolders: folders.size() = " + folders.size());
        if  (folders.size()>0){
            for (Element el:folders ) {
                Log.i(TAG, "parseBookmarkFolders: !!!!!!folder!!!!!!");
                Elements dts = el.select("dt");
                Elements ddel = el.select("dd");
                for (Element fldr :ddel ) {
                    String fldrTitle = fldr.select("h3").first().html();
                    Log.i(TAG, "parseBookmarkFolders: fldrTitle = " + fldrTitle);
                    System.out.println(fldr.select("dl").get(0));

//                    if (innerFldr.size()>0){
//                        Log.i(TAG, " !!!!!!!innerFolder!!!!!!!!!");
//                        parseBookmarkFolders(fldr.select("dl").get(0));
//                    }
                }
                for (Element dt:dts ) {
                    Log.i(TAG, "parseBookmarkFolders: link = " + dt.select("a").toString());
                }
            }
        }
        return null;
    }

    class ParseBookmarks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                parseBookmarkFolders(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    private void importFile(Context context, String filename) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File importfile = new File("", filename);
            Document doc = Jsoup.parse(importfile, "UTF-8");
            container = doc.select("dl").get(1);
            ParseBookmarks parseBookmarks = new ParseBookmarks();
            parseBookmarks.execute();

//            Elements elements = contEl.select("DL");
//            for (Element el :elements ) {
//                Element folderEl = el.select("h3").first();
//                String folder = folderEl.html();
//                Log.i(TAG, "importFile: folder =" + folder);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.error_import), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onResume() {
        super.onResume();
        Log.i("LOG_TAG", "----------onResume-------------");
    }

    protected void onPause() {
        super.onPause();
        Log.i("LOG_TAG", "----------onPause-------------");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i("LOG_TAG", "----------onDestroy-------------");
    }


    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: ");
        if (mDrawerLayout.isDrawerOpen(mDrawerLinearLayout)) {
            mDrawerLayout.closeDrawer(mDrawerLinearLayout);
        } else {
            boolean quit;
            List fragments = getSupportFragmentManager().getFragments();
            Log.i(TAG, "onBackPressed: fragments = " + fragments);
            BookMarkFragment bookMarkFragment;
            Log.i(TAG, "onBackPressed: fragments = " + fragments);
            try {
                bookMarkFragment = (BookMarkFragment) fragments.get(0);
                Log.i(TAG, "onBackPressed: bookMarkFragment = " + bookMarkFragment);
                quit = bookMarkFragment.onBackPressed();
                Log.i(TAG, "onBackPressed activity: quit = " + quit);
                if (quit){
                    openQuitDialog();
                }else{
                    bookMarkFragment.onBackPressedInitList();
                }
            }catch (Exception e){
                e.printStackTrace();
                openQuitDialog();
            }
        }
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                MainActivity.this);
        quitDialog.setTitle(getString(R.string.str_exit));
        quitDialog.setNegativeButton(getString(R.string.str_cancel), null);
        quitDialog.setPositiveButton(getString(R.string.str_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = quitDialog.create();
        alert.show();
    }
}
