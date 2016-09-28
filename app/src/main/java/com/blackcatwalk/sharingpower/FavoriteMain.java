package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.FavoriteCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FavoriteMain extends AppCompatActivity {

    private String mTempUrl;
    private ControlDatabase mControlDatabase;
    public List<Favorite> mFavoriteList = new ArrayList<Favorite>();
    public Favorite mItem;
    private final String[] mLstsSort = {"สถานที่", "วันที่ (น้อย->มาก)", "วันที่ (มาก->น้อย)"};
    private final String[] mListsManage = {"ดูแผนที่", "แก้ไข", "ลบรายการ",};

    // ----------- User Interface  --------------//
    private ListView mListView;
    private ImageView mRefreshIm;
    private ImageView mSortIm;
    public FavoriteCustomListAdapter mAdapter;
    public TextView mFavotiteTv;
    public ImageView mFavotiteIm;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    public String getmTempUrl() {
        return mTempUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().hide();

        bindWidget();

        mControlDatabase = new ControlDatabase(this);

        setSwipeRefreshLayout();
        setListView();

        mFavotiteTv.setVisibility(View.INVISIBLE);
        mFavotiteIm.setVisibility(View.INVISIBLE);

        mAdapter = new FavoriteCustomListAdapter(this, mFavoriteList);
        mListView.setAdapter(mAdapter);

        mRefreshIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

        mSortIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSort();
            }
        });

        mTempUrl = "&job=all";
        getDatabase();
    }

    private void setListView() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (mListView == null || mListView.getChildCount() == 0) ?
                                0 : mListView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogMenu(position);
            }
        });

    }

    private void setSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(new ControlProgress().getColorSchemeSwipeRefresh());
        mSwipeRefreshLayout.setClickable(false);
    }

    private void getDatabase() {
      mControlDatabase.getDatabaseFavoriteMain();
    }

    private void showDialogSort() {

        new AlertDialog.Builder(FavoriteMain.this).setTitle("เรียงลำดับ").setItems(mLstsSort, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Collections.sort(mFavoriteList, new SortTypeComparator());
                        break;
                    case 1:
                        Collections.sort(mFavoriteList, new SortDateComparator());
                        break;
                    case 2:
                        Collections.sort(mFavoriteList, new SortDateComparator());
                        Collections.reverse(mFavoriteList);
                        break;
                }
                sortData();
                dialog.cancel();
            }
        }).show();
    }

    public void fetchData() {
        mFavoriteList = null;
        mFavoriteList = new ArrayList<Favorite>();

        mAdapter = new FavoriteCustomListAdapter(this, mFavoriteList);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setRefreshing(true);
        getDatabase();
    }

    public void sortData() {

        ArrayList<Favorite> _favoriteList = new ArrayList<Favorite>();

        for(int i = 0; i < mFavoriteList.size(); i++){
            Favorite item = new Favorite();
            item.setDetail(mFavoriteList.get(i).getDetail());
            item.setTime(mFavoriteList.get(i).getTime());
            item.setType(mFavoriteList.get(i).getType());
            _favoriteList.add(item);
        }

        mAdapter = new FavoriteCustomListAdapter(this, _favoriteList);
        mListView.setAdapter(mAdapter);
    }

    public class SortTypeComparator implements Comparator<Favorite> {
        @Override
        public int compare(Favorite o1, Favorite o2) {
            return o1.getType().compareTo(o2.getType());
            //return Double.compare(o1.getPercentChange(), o2.getPercentChange());
            // return Integer.compare(o1.age, o2.age);
        }
    }

    public class SortDateComparator implements Comparator<Favorite> {
        @Override
        public int compare(Favorite o1, Favorite o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    }

    private void showDialogMenu(int _position) {

        mItem = mFavoriteList.get(_position);

        new AlertDialog.Builder(FavoriteMain.this).setTitle("รายการโปรด").setItems(mListsManage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), DetailMap.class)
                                .putExtra("latFavorite", mItem.getLat()).putExtra("lngFavorite", mItem.getLng())
                                .putExtra("typeFavorite", mItem.getType()).putExtra("typeFavorite", mItem.getType())
                                .putExtra("detailFavorite", mItem.getDetail()).putExtra("typeMoveMap", 9));
                        break;
                    case 1:
                        showDialogEdit();
                        break;
                    case 2:
                        showDialogDelete();
                        break;
                }
                dialog.cancel();
            }
        }).show();
    }

    private void showDialogEdit() {
        final Dialog _dialog = new Dialog(this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_favorite_detail);

        final EditText editDetailName = (EditText) _dialog.findViewById(R.id.editDetailName);
        editDetailName.setText(mItem.getDetail());
        editDetailName.setSelection(editDetailName.getText().length());

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Button save = (Button) _dialog.findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editDetailName.getText().toString().length() > 1) {
                    mControlDatabase.setFavoriteMain(editDetailName.getText().toString(), "updatefavorite");
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteMain.this);
        builder.setMessage("ลบรายการนี้หรือไม่");
        builder.setPositiveButton("ลบรายการ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mControlDatabase.setFavoriteMain("", "deletefavorite");
                dialog.cancel();
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#147cce"));
        nbutton.setTypeface(null, Typeface.BOLD);
    }

    private void bindWidget() {
        mRefreshIm = (ImageView) findViewById(R.id.refreshIm);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSortIm = (ImageView) findViewById(R.id.sortIm);
        mListView = (ListView) findViewById(R.id.listview);
        mFavotiteTv = (TextView) findViewById(R.id.favoriteTv);
        mFavotiteIm = (ImageView) findViewById(R.id.favoriteIm);
    }
}


