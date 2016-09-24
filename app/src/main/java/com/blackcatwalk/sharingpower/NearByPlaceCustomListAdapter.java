package com.blackcatwalk.sharingpower;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NearByPlaceCustomListAdapter extends BaseAdapter {

    Context mContext;
    String[] strName;
    int[] resId;

    public NearByPlaceCustomListAdapter(Context context, String[] strName, int[] resId) {
        this.mContext= context;
        this.strName = strName;
        this.resId = resId;
    }

    public int getCount() {
        return strName.length;
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
            view = mInflater.inflate(R.layout.activity_row_nearby_location, parent, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(strName[position]);

        de.hdodenhof.circleimageview.CircleImageView tempthumbnail = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.tempthumbnail);
        tempthumbnail.setImageResource(resId[position]);

        return view;
    }
}