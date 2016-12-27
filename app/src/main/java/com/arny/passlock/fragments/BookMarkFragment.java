package com.arny.passlock.fragments;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arny.passlock.R;
import com.arny.passlock.helpers.Const;
import com.arny.passlock.model.BackPressedListener;
import com.arny.passlock.model.DatabaseHelper;
import com.arny.passlock.model.Items;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BookMarkFragment extends Fragment implements BackPressedListener {
    public static final String TAG = "LOG_TAG";
    private static final double FAB1_RM = 1.5;
    private static final double FAB1_BM = 0.5;
    private static final double FAB2_RM = 1.2;
    private static final double FAB2_BM = 1.5;
    DatabaseHelper databaseHelper;
    ListView itemsListView;
    ArrayList<Items> itemsArrayList;
    private Context context;
    private int ctxPos, parent, type, parentBefore;
    private String link, title, extraTitle, extraLink;
    MainListAdapter mainListAdapter;
    inputDelay inpDel;
    //    FloatingActionButton fab,fab1,fab2,fab3;
    RelativeLayout emptyLayout;
    EditText editSearch;
    Button btnBack;
    FloatingActionButton fab, fab1, fab2, fab3;
    LinearLayout fabLayout1, fabLayout2, fabLayout3;
    //    boolean isFABOpen=false;
    private boolean FAB_Status = false, isFABOpen = false;

    public BookMarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        this.context = container.getContext();
        Log.i(TAG, "onCreateView: parent = " + parent);
        databaseHelper = DatabaseHelper.getInstance(context);
        initUI(view);
        initListeners();
        initListView();
        initExtraLinks();
        return view;
    }

    private void initSearchEdit() {
        if (itemsArrayList.size() == 0 && parent == 0) {
            editSearch.setEnabled(false);
        }
    }

    private void initExtraLinks() {
        extraTitle = null;
        extraLink = null;
        Log.i(TAG, "initExtraLinks: getArguments() = " + getArguments());
        if (getArguments() != null) {
            try {
                extraTitle = getArguments().getString(Const.KEY_EXTRA_TITLE);
                extraLink = getArguments().getString(Const.KEY_EXTRA_LINK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "onCreateView: extraTitle = " + extraTitle);
        Log.i(TAG, "onCreateView: extraLink = " + extraLink);
        if (extraLink != null) {
            AddDialogBookmark();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.items_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_folder:
                extraTitle = null;
                AddDialogFolder();
                break;
            case R.id.action_add_bookmark:
                extraTitle = null;
                extraLink = null;
                AddDialogBookmark();
                break;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        Log.i(TAG, "fragment onBackPressed: ");
        Log.i(TAG, "onBackPressed: parent = " + parent);
        Log.i(TAG, "onBackPressed: parentBefore = " + parentBefore);
        return parent == 0;
    }

    @Override
    public void onBackPressedInitList() {
        Log.i(TAG, "onBackPressedInitList: ");
        Log.i(TAG, "onBackPressed: parent = " + parent);
        Log.i(TAG, "onBackPressed: parentBefore = " + parentBefore);
        parent = parentBefore;
        initListView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class inputDelay extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String param = params[0];
            try {
                TimeUnit.SECONDS.sleep(1);
                if (isCancelled()) return null;
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            return param;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            searchListView(result);
        }
    }

    private void initListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent = parentBefore;
                initListView();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                extraTitle = null;
                extraLink = null;
                AddDialogBookmark();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                extraTitle = null;
                AddDialogFolder();
            }
        });
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> prt, View view, int position, long id) {
                Log.i(TAG, "onItemClick: ");
                type = itemsArrayList.get(position).getType();
                switch (type) {
                    case Const.ITEM_TYPE_FOLDER:
                        parent = itemsArrayList.get(position).getID();
                        initListView();
                        break;
                    case Const.ITEM_TYPE_BOOKMARK:
                        link = itemsArrayList.get(position).getLink();
                        title = itemsArrayList.get(position).getTitle();
                        openDialog();
                        break;
                }
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged: editSearch length = " + editSearch.length());
                Log.i(TAG, "onTextChanged: s.toString().length() = " + s.toString().length());
                if (s.toString().length() > 0) {
                    try {
                        cancelDelay();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                    inpDel = new inputDelay();
                    try {
                        inpDel.execute(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    cancelDelay();
                    initListView();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initUI(View view) {
        itemsListView = (ListView) view.findViewById(R.id.bookmarkslistView);
        editSearch = (EditText) view.findViewById(R.id.editSearch);
        btnBack = (Button) view.findViewById(R.id.btnBack);
        fabLayout1 = (LinearLayout) view.findViewById(R.id.fabLayout1);
        fabLayout2 = (LinearLayout) view.findViewById(R.id.fabLayout2);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(135);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotationBy(-135);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void cancelDelay() {
        if (inpDel == null) return;
        inpDel.cancel(true);
    }

    private void EditDialogFolder(final int itemId) {
        Log.i(TAG, "EditDialogFolder: ");
        LayoutInflater li = LayoutInflater.from(context);
        View xmlView = li.inflate(R.layout.add_folder_layout, null);
        final EditText edtTitleInput = (EditText) xmlView.findViewById(R.id.edtFolderTitle);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(xmlView);
        alertDialogBuilder.setTitle(getString(R.string.str_edit_folder));
        if (extraTitle != null) {
            edtTitleInput.setText(extraTitle);
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = edtTitleInput.getText().toString();
                Items item = new Items();
                item.setID(itemId);
                item.setTitle(title);
                item.setType(Const.ITEM_TYPE_FOLDER);
                item.setParent(parent);
                item.setDatetime(Calendar.getInstance().getTimeInMillis());
                boolean update = databaseHelper.updateItem(item);
                if (update) {
                    initListView();
                } else {
                    Toast.makeText(context, "Не обновлено", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void EditDialogBookmark(final int itemId) {
        Log.i(TAG, "EditDialogBookmark:  = " + itemId);
        LayoutInflater li = LayoutInflater.from(context);
        View xmlView = li.inflate(R.layout.add_bookmark_layout, null);
        final EditText edtTitleInput = (EditText) xmlView.findViewById(R.id.edtBookmarkTitle);
        final EditText edtTypeInput = (EditText) xmlView.findViewById(R.id.edtLinkInput);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(xmlView);
        alertDialogBuilder.setTitle(getString(R.string.str_edit_bookmark));
        if (extraTitle != null) {
            edtTitleInput.setText(extraTitle);
        }
        if (extraLink != null) {
            edtTypeInput.setText(extraLink);
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String link = edtTypeInput.getText().toString();
                String title = edtTitleInput.getText().toString();
                if (link.length() != 0) {
                    if (title.length() == 0) {
                        String[] splitted = link.split("\\//");
                        Log.i(TAG, "onClick: splitted = " + splitted);
                        try {
                            if (splitted.length > 10) {
                                title = splitted[1].substring(0, 10) + "...";
                            } else {
                                title = splitted[0].substring(0, splitted.length);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Items item = new Items();
                    item.setID(itemId);
                    item.setTitle(title);
                    item.setLink(link);
                    item.setType(Const.ITEM_TYPE_BOOKMARK);
                    item.setDatetime(Calendar.getInstance().getTimeInMillis());
                    item.setParent(parent);
                    if (databaseHelper.updateItem(item)) {
                        initListView();
                    }
                } else {
                    SimpleAlertDialog(context, getResources().getString(R.string.str_empty_link));
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void AddDialogBookmark() {
        Log.i(TAG, "AddDialogBookmark: ");
        LayoutInflater li = LayoutInflater.from(context);
        View xmlView = li.inflate(R.layout.add_bookmark_layout, null);
        final EditText edtTitleInput = (EditText) xmlView.findViewById(R.id.edtBookmarkTitle);
        final EditText edtTypeInput = (EditText) xmlView.findViewById(R.id.edtLinkInput);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(xmlView);
        alertDialogBuilder.setTitle(getString(R.string.str_add_bookmark));
        if (extraTitle != null) {
            edtTitleInput.setText(extraTitle);
        }
        if (extraLink != null) {
            edtTypeInput.setText(extraLink);
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String link = edtTypeInput.getText().toString();
                String title = edtTitleInput.getText().toString();
                if (link.length() != 0) {
                    if (title.length() == 0) {
                        String[] splitted = link.split("\\//");
                        Log.i(TAG, "onClick: splitted = " + splitted);
                        try {
                            if (splitted.length > 10) {
                                title = splitted[1].substring(0, 10) + "...";
                            } else {
                                title = splitted[0].substring(0, splitted.length);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Items item = new Items();
                    item.setTitle(title);
                    item.setLink(link);
                    item.setType(Const.ITEM_TYPE_BOOKMARK);
                    item.setParent(parent);
                    databaseHelper.addItem(item);
                    initListView();
                } else {
                    SimpleAlertDialog(context, getResources().getString(R.string.str_empty_link));
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void AddDialogFolder() {
        Log.i(TAG, "AddDialogFolder: ");
        LayoutInflater li = LayoutInflater.from(context);
        View xmlView = li.inflate(R.layout.add_folder_layout, null);
        final EditText edtTitleInput = (EditText) xmlView.findViewById(R.id.edtFolderTitle);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(xmlView);
        alertDialogBuilder.setTitle(getString(R.string.str_add_folder));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = edtTitleInput.getText().toString();
                if (title.length() == 0) {
                    Toast.makeText(context, getResources().getString(R.string.str_empty_title), Toast.LENGTH_SHORT).show();
                    return;
                }
                Items item = new Items();
                item.setTitle(title);
                item.setType(Const.ITEM_TYPE_FOLDER);
                item.setParent(parent);
                databaseHelper.addItem(item);
                initListView();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void searchListView(String search) {
        itemsArrayList = databaseHelper.getItemsByText(search);
        Log.i(TAG, "initListView: itemsArrayList = " + itemsArrayList);
        mainListAdapter = new MainListAdapter(context, itemsArrayList);
        Log.i(TAG, "initListView: bookMarkAdapter = " + mainListAdapter);
        itemsListView.setAdapter(mainListAdapter);
    }

    private void initListView() {
        String foldertitle;
        Log.i(TAG, "initListView: parent = " + parent);
        if (parent == 0) {
            btnBack.setVisibility(View.GONE);
            foldertitle = context.getString(R.string.str_parent_folder);
        } else {
            btnBack.setVisibility(View.VISIBLE);
            parentBefore = databaseHelper.getItemById(parent).get(0).getParent();
            foldertitle = databaseHelper.getItemById(parent).get(0).getTitle();
            String parenttitle;
            if (parentBefore == 0) {
                parenttitle = context.getString(R.string.str_parent_folder);
            } else {
                parenttitle = databaseHelper.getItemById(parentBefore).get(0).getTitle();
            }
            Log.i(TAG, "initListView: parenttitle = " + parenttitle);
            btnBack.setText(parenttitle);
        }
        Log.i(TAG, "initListView: foldertitle = " + foldertitle);
        try {
            getActivity().setTitle(foldertitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        itemsArrayList = databaseHelper.getItemsByParent(parent);
        Log.i(TAG, "initListView: itemsArrayList = " + itemsArrayList);
        mainListAdapter = new MainListAdapter(context, itemsArrayList);
        itemsListView.setAdapter(mainListAdapter);
        initSearchEdit();
    }

    public void SimpleAlertDialog(Context context, String title) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        // alertDialog.setMessage("Here is android alert dialog message");
        // Alert dialog button
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.str_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();// use dismiss to cancel alert dialog
                    }
                });
        alertDialog.show();
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(context, "Невозможно открыть в браузере " + link, Toast.LENGTH_SHORT).show();
        }
    }

    private void showFolderDialog(final int pos) {
        Log.i(TAG, "showFolderDialog: pos = " + pos);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String contextMenuText[] = {getString(R.string.str_open), getString(R.string.str_edit), getString(R.string.str_delete)};
        ctxPos = pos;//кидаем в глобальную переменную чтобы все видели
        title = itemsArrayList.get(pos).getTitle();
        parent = itemsArrayList.get(pos).getParent();
        alert.setItems(contextMenuText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        parent = itemsArrayList.get(pos).getID();
                        initListView();
                        break;
                    case 1:
                        extraTitle = title;
                        EditDialogFolder(itemsArrayList.get(pos).getID());
                        break;
                    case 2:
                        deleteDialog();
                        break;
                }
            }
        });
        alert.show();
    }

    private void showItemDialog(final int pos) {
        Log.i(TAG, "showItemDialog: pos = " + pos);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String contextMenuText[] = {getString(R.string.str_open), getString(R.string.str_edit), getString(R.string.str_delete)};
        ctxPos = pos;//кидаем в глобальную переменную чтобы все видели
        link = itemsArrayList.get(pos).getLink();
        title = itemsArrayList.get(pos).getTitle();
        type = itemsArrayList.get(pos).getType();
        alert.setItems(contextMenuText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (type) {
                    case Const.ITEM_TYPE_FOLDER:
                        switch (which) {
                            case 0:
                                parent = itemsArrayList.get(pos).getID();
                                initListView();
                                break;
                            case 1:
                                title = extraTitle;
                                EditDialogFolder(itemsArrayList.get(pos).getID());
                                break;
                            case 2:
                                deleteDialog();
                                break;
                        }
                        break;
                    case Const.ITEM_TYPE_BOOKMARK:
                        switch (which) {
                            case 0:
                                openDialog();
                                break;
                            case 1:
                                extraTitle = title;
                                extraLink = link;
                                EditDialogBookmark(itemsArrayList.get(pos).getID());
                                break;
                            case 2:
                                deleteDialog();
                                break;
                        }
                        break;
                }
            }
        });
        alert.show();
    }

    private void openDialog() {
        Log.i(TAG, "openDialog: link = " + link);
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "http://" + link;
        }
        AlertDialog.Builder openDialog = new AlertDialog.Builder(context);
        openDialog.setTitle(getString(R.string.str_open) + " " + title + " ?");
        openDialog.setNegativeButton(getString(R.string.str_cancel), null);
        openDialog.setPositiveButton(getString(R.string.str_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            openWebPage(link);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        AlertDialog alrt = openDialog.create();
        alrt.show();
    }

    private void deleteDialog() {
        AlertDialog.Builder delDialog = new AlertDialog.Builder(context);
        type = itemsArrayList.get(ctxPos).getType();
        int remoVeItem = type == Const.ITEM_TYPE_FOLDER ? R.string.str_delete_folder : R.string.str_delete_bookmark;
        delDialog.setTitle(getString(remoVeItem) + " " + title + " ?");
        delDialog.setNegativeButton(getString(R.string.str_cancel), null);
        delDialog.setPositiveButton(getString(R.string.str_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        parent = itemsArrayList.get(ctxPos).getParent();
                        databaseHelper.removeBookmark(itemsArrayList.get(ctxPos).getID());
                        initListView();
                    }
                });
        AlertDialog alert = delDialog.create();
        alert.show();
    }

    private class MainListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Items> itemsList;

        public MainListAdapter(Context context, ArrayList<Items> itemsList) {
            this.itemsList = itemsList;
            Log.i(TAG, "MainListAdapter: itemsList = " + itemsList);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(int position) {
            return itemsList.get(position).getType();
        }

        @Override
        public int getCount() {
            return itemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemsList.get(position).getID();
        }

        @Override
        public long getItemId(int position) {
            return itemsList.get(position).getID();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyViewHolderBookmark viewHolderBookmark;
            MyViewHolderFolder viewHolderFolder;
            switch (getItemViewType(position)) {
                case Const.ITEM_TYPE_BOOKMARK:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.bookmark_list_item, parent, false);
                        viewHolderBookmark = new MyViewHolderBookmark(convertView);
                        convertView.setTag(viewHolderBookmark);
                    } else {
                        viewHolderBookmark = (MyViewHolderBookmark) convertView.getTag();
                    }
                    viewHolderBookmark.tvTitle.setText(itemsList.get(position).getTitle());
                    viewHolderBookmark.tvLink.setText(itemsList.get(position).getLink());
                    viewHolderBookmark.imgEditItemBookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            extraTitle = itemsArrayList.get(position).getTitle();
                            extraLink = itemsArrayList.get(position).getLink();
                            EditDialogBookmark(itemsArrayList.get(position).getID());
                        }
                    });
                    viewHolderBookmark.imgRemoveItemBookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ctxPos = position;
                            title = itemsArrayList.get(position).getTitle();
                            deleteDialog();
                        }
                    });
                    return convertView;
                case Const.ITEM_TYPE_FOLDER:
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.folder_list_item, parent, false);
                        viewHolderFolder = new MyViewHolderFolder(convertView);
                        convertView.setTag(viewHolderFolder);
                    } else {
                        viewHolderFolder = (MyViewHolderFolder) convertView.getTag();
                    }
                    viewHolderFolder.tvTitle.setText(itemsList.get(position).getTitle());
                    viewHolderFolder.imgEditItemFolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            title = itemsArrayList.get(position).getTitle();
                            extraTitle = title;
                            EditDialogFolder(itemsArrayList.get(position).getID());
                        }
                    });
                    viewHolderFolder.imgRemoveItemFolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            title = itemsArrayList.get(position).getTitle();
                            ctxPos = position;
                            deleteDialog();
                        }
                    });
                    return convertView;
            }
            return null;
        }

        private class MyViewHolderBookmark {
            TextView tvTitle, tvLink;
            ImageView imgEditItemBookmark, imgRemoveItemBookmark;

            public MyViewHolderBookmark(View item) {
                tvTitle = (TextView) item.findViewById(R.id.bookmark_title);
                tvLink = (TextView) item.findViewById(R.id.bookmark_link);
                imgEditItemBookmark = (ImageView) item.findViewById(R.id.imgEditItemBookmark);
                imgRemoveItemBookmark = (ImageView) item.findViewById(R.id.imgRemoveItemBookmark);
            }
        }

        private class MyViewHolderFolder {
            TextView tvTitle;
            ImageView imgEditItemFolder, imgRemoveItemFolder;

            public MyViewHolderFolder(View item) {
                tvTitle = (TextView) item.findViewById(R.id.folder_title);
                imgEditItemFolder = (ImageView) item.findViewById(R.id.imgEditItemFolder);
                imgRemoveItemFolder = (ImageView) item.findViewById(R.id.imgRemoveItemFolder);
            }
        }
    }
}
