package com.blackcatwalk.sharingpower.customAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.LocationComment;
import com.blackcatwalk.sharingpower.R;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class LocationCommentCustomListAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<LocationComment> mItems;

    public LocationCommentCustomListAdapter(Activity _activity, List<LocationComment> _items){
        this.mActivity =_activity;
        this.mItems =_items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(mInflater ==null){
            mInflater =(LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView ==null){
            convertView= mInflater.inflate(R.layout.activity_row_location_comment,null);
        }

        TextView _users = (TextView) convertView.findViewById(R.id.users);
        TextView _comment = (TextView) convertView.findViewById(R.id.comment);
        TextView _time = (TextView) convertView.findViewById(R.id.time);
        CircleImageView _tempthumbnail = (CircleImageView) convertView.findViewById(R.id.tempthumbnail);

        LocationComment _item = mItems.get(position);

        _users.setText(_item.getmUsers());
        _comment.setText(_item.getmComment());
        _time.setText(_item.getmUpdateDate());

        if (_item.getmSex().equals("หญิง")) {
            _tempthumbnail.setImageResource(R.drawable.sex_female);
        } else {
            _tempthumbnail.setImageResource(R.drawable.sex_male);
        }

        return convertView;
    }
}
