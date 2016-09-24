package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Location_comment_CustomListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Activity activity;
    private List<Location_comment> items;

    public Location_comment_CustomListAdapter(Activity activity, List<Location_comment> items){
        this.activity=activity;
        this.items=items;
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
        if(inflater==null){
            inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView ==null){
            convertView=inflater.inflate(R.layout.activity_row_location_comment,null);
        }

        TextView users= (TextView) convertView.findViewById(R.id.users);
        TextView comment= (TextView) convertView.findViewById(R.id.comment);
        TextView time= (TextView) convertView.findViewById(R.id.time);
        de.hdodenhof.circleimageview.CircleImageView tempthumbnail = (de.hdodenhof.circleimageview.CircleImageView) convertView.findViewById(R.id.tempthumbnail);

        //getting data for row
        Location_comment item = items.get(position);

        users.setText(item.getUsers());
        comment.setText(item.getComment());
        time.setText(item.getUpdate_date());

        if (item.getSex().equals("หญิง")) {
            tempthumbnail.setImageResource(R.drawable.sex_female);
        } else {
            tempthumbnail.setImageResource(R.drawable.sex_male);
        }

        return convertView;
    }
}
