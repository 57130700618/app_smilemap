package com.blackcatwalk.sharingpower.customAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;


public class NearByPlaceCustomListAdapter extends BaseAdapter {

    Context mContext;
    String[] mName;
    int[] mPicture;

    public NearByPlaceCustomListAdapter(Context _context, String[] _name, int[] _picture) {
        this.mContext= _context;
        this.mName = _name;
        this.mPicture = _picture;
    }

    public int getCount() {
        return mName.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int _position, View _view, ViewGroup _parent) {
        LayoutInflater _mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(_view == null)
            _view = _mInflater.inflate(R.layout.activity_row_nearby_location, _parent, false);

        TextView name = (TextView) _view.findViewById(R.id.name);
        name.setText(mName[_position]);

        de.hdodenhof.circleimageview.CircleImageView tempthumbnail = (de.hdodenhof.circleimageview.CircleImageView) _view.findViewById(R.id.tempthumbnail);
        tempthumbnail.setImageResource(mPicture[_position]);

        return _view;
    }
}