package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.FavoriteCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlKeyboard;

import java.util.ArrayList;
import java.util.List;


public class FavoriteMain extends AppCompatActivity {

    private String mTempUrl;
    private ControlDatabase mControlDatabase;
    private ControlKeyboard mControlKeyBoard;
    public List<Favorite> mFavoriteList = new ArrayList<Favorite>();
    public Favorite mItem;

    // ----------- User Interface  --------------//
    private ListView mListView;
    private ImageView mRefreshIm;
    private ImageView mSortIm;
    public FavoriteCustomListAdapter mAdapter;
    public TextView mFavotiteTv;
    public ImageView mFavotiteIm;

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
        mControlKeyBoard = new ControlKeyboard();

        mFavotiteTv.setVisibility(View.INVISIBLE);
        mFavotiteIm.setVisibility(View.INVISIBLE);

        mAdapter = new FavoriteCustomListAdapter(this, mFavoriteList);
        mListView.setAdapter(mAdapter);

        mRefreshIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        mSortIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSort();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogMenu(position);
            }
        });

        mTempUrl = "&job=all";
        getDatabase();
    }

    private void getDatabase() {
        mControlDatabase.getDatabaseFavoriteMain();
    }

    private void showDialogSort() {
        final String[] _lists = {"สถานที่", "วันที่"};
        new AlertDialog.Builder(FavoriteMain.this).setTitle("เรียงลำดับ").setItems(_lists, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mTempUrl = null;

                switch (which) {
                    case 0:
                        mTempUrl = "&job=location";
                        break;
                    case 1:
                        mTempUrl = "&job=date";
                        break;
                }

                refreshData();
                dialog.cancel();
            }
        }).show();
    }

    public void refreshData() {
        mFavoriteList = null;
        mFavoriteList = new ArrayList<Favorite>();

        mAdapter = new FavoriteCustomListAdapter(FavoriteMain.this, mFavoriteList);
        mListView.setAdapter(mAdapter);

        getDatabase();
    }

    private void showDialogMenu(int _position) {

        mItem = mFavoriteList.get(_position);

        final String[] lists = {"ดูแผนที่", "แก้ไข", "ลบรายการ",};
        new AlertDialog.Builder(FavoriteMain.this).setTitle("รายการโปรด").setItems(lists, new DialogInterface.OnClickListener() {
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

        ControlKeyboard.showKeyboard(FavoriteMain.this);

        final EditText editDetailName = (EditText) _dialog.findViewById(R.id.editDetailName);
        editDetailName.setText(mItem.getDetail());
        editDetailName.setSelection(editDetailName.getText().length());

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlKeyBoard.hideKeyboard(FavoriteMain.this);
                _dialog.cancel();
            }
        });

        Button save = (Button) _dialog.findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlDatabase.setFavoriteMain(editDetailName.getText().toString(), "updatefavorite");
                _dialog.cancel();
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
        mSortIm = (ImageView) findViewById(R.id.sortIm);
        mListView = (ListView) findViewById(R.id.listview);
        mFavotiteTv = (TextView) findViewById(R.id.favoriteTv);
        mFavotiteIm = (ImageView) findViewById(R.id.favoriteIm);
    }
}


