package com.blackcatwalk.sharingpower.customAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.blackcatwalk.sharingpower.R.id.tempthumbnail;


public class NearByPlaceCustomListAdapter extends BaseAdapter {

    Context mContext;
    String[] mName;
    String[] mPicture;

    public NearByPlaceCustomListAdapter(Context _context, String[] _name, String[] _picture) {
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

        CircleImageView _tempthumbnail = (CircleImageView) _view.findViewById(tempthumbnail);

        Glide.with(mContext).load(mPicture[_position]).asBitmap().placeholder(R.drawable.loading)
                .error(R.drawable.error).centerCrop().into(_tempthumbnail);
        return _view;
    }
}