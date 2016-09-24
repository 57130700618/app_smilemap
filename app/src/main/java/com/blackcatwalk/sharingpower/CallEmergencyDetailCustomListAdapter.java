package com.blackcatwalk.sharingpower;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class CallEmergencyDetailCustomListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> nameList;
    private List<String> telList;

    public CallEmergencyDetailCustomListAdapter(Context context, List<String> strName, List<String> strTel) {
        this.mContext= context;
        this.nameList = strName;
        this.telList = strTel;
    }

    public int getCount() {
        return nameList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
            view = mInflater.inflate(R.layout.activity_row_call_emergency, parent, false);

        TextView name = (TextView) view.findViewById(R.id.name);

        if(nameList.get(position).length() >= 25 && telList.get(position).length() > 5){
            name.setText(nameList.get(position).substring(0,24) + "...");
        }else{
            name.setText(nameList.get(position));
        }

        TextView tel = (TextView) view.findViewById(R.id.tel);
        tel.setText(telList.get(position));

        return view;
    }
}