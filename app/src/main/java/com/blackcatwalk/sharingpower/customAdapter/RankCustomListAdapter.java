package com.blackcatwalk.sharingpower.customAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;
import com.blackcatwalk.sharingpower.Rank;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RankCustomListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<Rank> mItems;

    public RankCustomListAdapter(Activity _activity, List<Rank> _items) {
        this.mActivity = _activity;
        this.mItems = _items;
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
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (_convertView == null) {
            _convertView = mInflater.inflate(R.layout.activity_row_rank, null);
        }

        TextView _name = (TextView) _convertView.findViewById(R.id.name);
        TextView _point = (TextView) _convertView.findViewById(R.id.point);
        TextView _amountTime = (TextView) _convertView.findViewById(R.id.amountTime);
        TextView _sequeneNumber = (TextView) _convertView.findViewById(R.id.sequeneNumber);
        CircleImageView _tempthumbnail = (CircleImageView) _convertView.findViewById(R.id.tempthumbnail);

        Rank _item = mItems.get(_position);

        _sequeneNumber.setText(_item.getmSequeneNumber() + ".");
        _name.setText(_item.getmName());
        _point.setText(_item.getmPoint() + " แต้ม");
        _amountTime.setText(_item.getmSumHour() + " ชั่วโมง");

       /* if(item.getPicture().length() > 0){
           Glide.with(activity).load(item.getPicture()).into(tempthumbnail);
        }*/

        if (_item.getmSex().equals("หญิง")) {
            _tempthumbnail.setImageResource(R.drawable.sex_female);
        } else {
            _tempthumbnail.setImageResource(R.drawable.sex_male);
        }

        return _convertView;
    }
}