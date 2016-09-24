package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RankCustomListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity activity;
    private List<Rank> items;

    public RankCustomListAdapter(Activity activity, List<Rank> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_row_rank, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView point = (TextView) convertView.findViewById(R.id.point);
        TextView amountTime = (TextView) convertView.findViewById(R.id.amountTime);
        TextView sequeneNumber = (TextView) convertView.findViewById(R.id.sequeneNumber);
        de.hdodenhof.circleimageview.CircleImageView tempthumbnail = (de.hdodenhof.circleimageview.CircleImageView) convertView.findViewById(R.id.tempthumbnail);

        //getting data for row
        Rank item = items.get(position);

        sequeneNumber.setText(item.getSequeneNumber() + ".");
        name.setText(item.getName());
        point.setText(item.getPoint() + " แต้ม");
        amountTime.setText(item.getSumHour() + " ชั่วโมง");

       /* if(item.getPicture().length() > 0){
           Glide.with(activity).load(item.getPicture()).into(tempthumbnail);
        }*/

        if (item.getSex().equals("หญิง")) {
            tempthumbnail.setImageResource(R.drawable.sex_female);
        } else {
            tempthumbnail.setImageResource(R.drawable.sex_male);
        }

        return convertView;
    }
}