package com.blackcatwalk.sharingpower.customAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.CallEmergency;
import com.blackcatwalk.sharingpower.R;

import java.util.List;


public class CallEmergencyDetailCustomListAdapter extends BaseAdapter {

    private Context mContext;
    List<CallEmergency> mItems;

    public CallEmergencyDetailCustomListAdapter(Context _context, List<CallEmergency> _items) {
        this.mContext= _context;
        this.mItems = _items;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int _position, View _view, ViewGroup _parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(_view == null)
            _view = mInflater.inflate(R.layout.activity_row_call_emergency, _parent, false);

        TextView _name = (TextView) _view.findViewById(R.id.name);

        CallEmergency _item = mItems.get(_position);

        if(_item.getmName().length() >= 25 && _item.getmTel().length() > 5){
            _name.setText(_item.getmName().substring(0,22) + "...");
        }else{
            _name.setText(_item.getmName());
        }

        TextView _tel = (TextView) _view.findViewById(R.id.tel);
        _tel.setText(_item.getmTel());

        if(_item.getmDistance() != null){
            TextView _distanceTv = (TextView) _view.findViewById(R.id.distanceTv);
            _distanceTv.setVisibility(View.VISIBLE);
            _distanceTv.setText(_item.getmDistance());
        }

        return _view;
    }
}