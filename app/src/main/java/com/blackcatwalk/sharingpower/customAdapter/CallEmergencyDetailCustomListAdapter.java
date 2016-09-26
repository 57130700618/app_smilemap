package com.blackcatwalk.sharingpower.customAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;

import java.util.List;


public class CallEmergencyDetailCustomListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mNameList;
    private List<String> mTelList;

    public CallEmergencyDetailCustomListAdapter(Context _context, List<String> _strName, List<String> _strTel) {
        this.mContext= _context;
        this.mNameList = _strName;
        this.mTelList = _strTel;
    }

    public int getCount() {
        return mNameList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int _position, View _view, ViewGroup _parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(_view == null)
            _view = mInflater.inflate(R.layout.activity_row_call_emergency, _parent, false);

        TextView _name = (TextView) _view.findViewById(R.id.name);

        if(mNameList.get(_position).length() >= 25 && mTelList.get(_position).length() > 5){
            _name.setText(mNameList.get(_position).substring(0,24) + "...");
        }else{
            _name.setText(mNameList.get(_position));
        }

        TextView _tel = (TextView) _view.findViewById(R.id.tel);
        _tel.setText(mTelList.get(_position));

        return _view;
    }
}