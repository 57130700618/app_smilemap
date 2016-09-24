package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class InboxCustomListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Activity activity;
    private List<Inbox> items;

    public InboxCustomListAdapter(Activity activity, List<Inbox> items){
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
            convertView=inflater.inflate(R.layout.activity_row_inbox,null);
        }

        TextView title= (TextView) convertView.findViewById(R.id.textName);
        TextView detail= (TextView) convertView.findViewById(R.id.textDetail);

        //getting data for row
        Inbox item = items.get(position);

        title.setText(item.getTitle());
        detail.setText(item.getDetail());

        return convertView;
    }
}
