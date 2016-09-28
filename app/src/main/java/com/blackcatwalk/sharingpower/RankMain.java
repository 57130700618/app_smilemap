package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.RankCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;

import java.util.ArrayList;
import java.util.List;

public class RankMain extends AppCompatActivity {

    public List<Rank> mRankList;
    public RankCustomListAdapter mAdapter;
    private String stausRank = "0";
    private ControlFile mControlFile;
    private ControlDatabase mControlDatabase;

    // ----------- User InterFace --------------//
    private ImageView mSettingsIm;
    private ImageView mBackIm;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        getSupportActionBar().hide();

        bindWidget();

        mControlFile = new ControlFile();
        mControlDatabase = new ControlDatabase(this);

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mSettingsIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSettingRank();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Rank item = mRankList.get(position);
                showDialogDetail(item.getmName(), item.getmNickName(), item.getmStaus(), position);
            }
        });

        stausRank = mControlFile.getFile(getApplicationContext(),"stausRank");
        setData();
    }

    public void setData() {
        mControlFile.setFile(getApplicationContext(), stausRank,"stausRank");
        mRankList = null;
        mRankList = new ArrayList<Rank>();
        mAdapter = new RankCustomListAdapter(RankMain.this, mRankList);
        mListView.setAdapter(mAdapter);

        mControlDatabase.getDatabaseRankMain();
    }

    private void showDialogSettingRank() {
        stausRank = mControlFile.getFile(getApplicationContext(),"stausRank");

        final Dialog _dialog = new Dialog(RankMain.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_rank_setting);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final Switch showRank = (Switch) _dialog.findViewById(R.id.showRank);
        showRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((Switch) v).isChecked();
                if (on) {
                    stausRank = "1";
                } else {
                    stausRank = "0";
                }
                mControlDatabase.setDatabaseRankMain(stausRank);
            }
        });

        if (stausRank.equals("1")) {
            showRank.setChecked(true);
        }
        _dialog.show();
    }

    public void showDialogDetail(String name, String nickName, String staus, int position) {
        final Dialog _dialog = new Dialog(RankMain.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_rank_detail);

        ImageView crown = (ImageView) _dialog.findViewById(R.id.crown);

        switch (position) {
            case 0:
                crown.setImageResource(R.drawable.icon_crown_gold);
                break;
            case 1:
                crown.setImageResource(R.drawable.icon_crown_sliver);
                break;
            case 2:
                crown.setImageResource(R.drawable.icon_crown_broze);
                break;
        }

        position++;
        TextView head = (TextView) _dialog.findViewById(R.id.head);
        head.setText("อันดับ " + position);

        TextView nametemp = (TextView) _dialog.findViewById(R.id.name);
        nametemp.setText(name);

        TextView nickNameTemp = (TextView) _dialog.findViewById(R.id.nickName);
        nickNameTemp.setText(nickName);

        if (staus.length() > 0) {
            TextView stausTemp = (TextView) _dialog.findViewById(R.id.staus);
            stausTemp.setVisibility(View.VISIBLE);
            stausTemp.setText(staus);
        }

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        _dialog.show();
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mSettingsIm = (ImageView) findViewById(R.id.settingsIm);
        mListView = (ListView) findViewById(R.id.listview);
    }
}
