package com.blackcatwalk.sharingpower;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.LocationCommentCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlKeyboard;

import java.util.ArrayList;
import java.util.List;

public class LocationCommentMain extends AppCompatActivity {

    public List<LocationComment> mLocationComment;
    private String mType;
    private Double mLat;
    private Double mLng;
    private ControlDatabase mControlDatabase;


    // ----------- User Interface  --------------//
    private ImageView mBackIm;
    public ListView mListView;
    public LocationCommentCustomListAdapter mAdapter;
    private EditText mCommentEt;
    private ImageView mSendBtn;
    private ControlKeyboard mControlKeyBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);
        getSupportActionBar().hide(); // hide ActionBar

        bindWidget();

        mControlDatabase = new ControlDatabase(this);
        mControlKeyBoard = new ControlKeyboard();

        mControlKeyBoard.hideKeyboard(this);

        Bundle _bundle = getIntent().getExtras();
        mType = _bundle.getString("mType");
        mLat = _bundle.getDouble("mLat");
        mLng = _bundle.getDouble("mLng");

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        mCommentEt.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mCommentEt.getText().toString().length() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        mCommentEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mSendBtn.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCommentEt.getText().toString().length() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        mCommentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mSendBtn.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatabase();
            }
        });

        fetchData();
    }

    private void getDatabase() {
        mControlDatabase.getDatabaeeLocationCommentMain("comment_location&mLat=" + mLat + "&mLng=" + mLng + "&mType=" + mType);
    }

    private void setDatabase() {
        mControlDatabase.setDatabaeeLocationCommentMain(String.valueOf(mLat),String.valueOf(mLng)
                , mCommentEt.getText().toString(), mType);
    }

    public void fetchData(){
        mCommentEt.setText("");
        mLocationComment = null;
        mLocationComment = new ArrayList<LocationComment>();

        mAdapter = new LocationCommentCustomListAdapter(LocationCommentMain.this, mLocationComment);
        mListView.setAdapter(mAdapter);

        getDatabase();
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mListView = (ListView) findViewById(R.id.listview);
        mCommentEt = (EditText) findViewById(R.id.commentEt);
        mSendBtn = (ImageView) findViewById(R.id.sendBtn);
    }
}
