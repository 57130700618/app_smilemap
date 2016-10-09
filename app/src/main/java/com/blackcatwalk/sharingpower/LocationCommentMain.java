package com.blackcatwalk.sharingpower;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.LocationCommentCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlKeyboard;
import com.blackcatwalk.sharingpower.utility.ControlProgress;

import java.util.ArrayList;
import java.util.List;

public class LocationCommentMain extends AppCompatActivity {

    public List<LocationComment> mLocationComment;
    private String mType;
    private Double mLat;
    private Double mLng;
    private ControlDatabase mControlDatabase;
    private final String[] mListsManage = {"แก้ไข", "ลบ", "ยกเลิก"};
    private final String[] mListsReport = {"รายงาน", "ยกเลิก"};
    private int mIdUser;
    private int mPositionY;

    // ----------- User Interface  --------------//
    private ImageView mBackIm;
    public ListView mListView;
    public LocationCommentCustomListAdapter mAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mCommentEt;
    private ImageView mSendBtn;
    private ImageView mRefreshIm;
    private ControlKeyboard mControlKeyboard;


    public void setmIdUser(int mIdUser) {
        this.mIdUser = mIdUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);
        getSupportActionBar().hide(); // hide ActionBar

        bindWidget();

        getBundle();

        mControlKeyboard = new ControlKeyboard();
        mControlDatabase = new ControlDatabase(this);

        mControlKeyboard.hideKeyboardWhenShowViewPage(this);

        setSwipeRefreshLayout();
        setListView();
        setCommentEt();

        mRefreshIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatabase();
            }
        });

        fetchData();
        mControlDatabase.getDatabaseLocationCommentMainIdUser();
    }

    private void setCommentEt() {
        mCommentEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mCommentEt.getText().toString().length() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        mCommentEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

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

    }

    private void setListView() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogMenu(position);
                return false;
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPositionY = (int)event.getY();
                return false;
            }
        });

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

    private void getBundle() {
        Bundle _bundle = getIntent().getExtras();
        mType = _bundle.getString("type");
        mLat = _bundle.getDouble("lat");
        mLng = _bundle.getDouble("lng");
    }

    private void getDatabase() {
        mControlDatabase.getDatabaeeLocationCommentMain("comment_location&lat=" + mLat + "&lng=" + mLng + "&type=" + mType);
    }

    private void setDatabase() {
        mControlDatabase.setDatabaeeLocationCommentMain(String.valueOf(mLat), String.valueOf(mLng)
                , mCommentEt.getText().toString(), mType);
    }

    public void fetchData() {
        mCommentEt.setText("");
        mControlKeyboard.hideKeyboardWhenFocusEdittext(this);
        mLocationComment = null;
        mLocationComment = new ArrayList<LocationComment>();

        mAdapter = new LocationCommentCustomListAdapter(LocationCommentMain.this, mLocationComment);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setRefreshing(true);
        getDatabase();
    }


    private void showDialogMenu(final int _position) {

        final LocationComment _item = mLocationComment.get(_position);

        Dialog _alertDialog;

        if (_item.getmId() == mIdUser) {
            _alertDialog = new AlertDialog.Builder(this).setItems(mListsManage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:
                            sentEdit(_item.getmComment(), _item.getmUpdateDate());
                            break;
                        case 1:
                            sentDelete(_item.getmUpdateDate());
                            break;
                    }
                    dialog.cancel();
                }
            }).show();

        } else {
            _alertDialog = new AlertDialog.Builder(this).setItems(mListsReport, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:
                            sentReport(_item.getmId());
                            break;
                    }
                    dialog.cancel();
                }
            }).show();
        }
        _alertDialog.getWindow().setLayout(500, 550);

        Window window = _alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP | Gravity.LEFT;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.x = 10;   //x position
        wlp.y = mPositionY;   //y position
        window.setAttributes(wlp);
    }

    private void sentEdit(final String _detailComment, final String _dateComment) {
        final Dialog _dialog = new Dialog(this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_comment);

        final EditText _editDetailName = (EditText) _dialog.findViewById(R.id.editDetailName);
        _editDetailName.setText(_detailComment);
        _editDetailName.setSelection(_editDetailName.getText().length());

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Button _save = (Button) _dialog.findViewById(R.id.btnSave);
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_editDetailName.getText().toString().length() > 1) {
                 mControlDatabase.setDatabaeeLocationCommentMainEdit( String.valueOf(mIdUser)
                         ,_editDetailName.getText().toString(),String.valueOf(mLat),
                         String.valueOf(mLng), mType, _dateComment);
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    private void sentDelete(final String _updateDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ท่านต้องการลบความคิดเห็นหรือไม่");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new ControlDatabase(LocationCommentMain.this).setDatabaeeLocationCommentMainDelete(
                        String.valueOf(mIdUser), _updateDate);
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

    private void sentReport(final int _userIdComment) {
        final Dialog _dialog = new Dialog(this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_report_comment);

        mBackIm = (ImageView) _dialog.findViewById(R.id.btnClose);
        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final EditText _editText = (EditText) _dialog.findViewById(R.id.editText);

        Button _btnSend = (Button) _dialog.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(_editText.getText().length() > 1){
                    new ControlDatabase(LocationCommentMain.this).reportComment( String.valueOf(mIdUser),
                            String.valueOf(_userIdComment) ,String.valueOf(mLat), String.valueOf(mLng),
                            mType, _editText.getText().toString());
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    private void bindWidget() {
        mRefreshIm = (ImageView) findViewById(R.id.refreshIm);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mListView = (ListView) findViewById(R.id.listview);
        mCommentEt = (EditText) findViewById(R.id.commentEt);
        mSendBtn = (ImageView) findViewById(R.id.sendBtn);
    }

}
