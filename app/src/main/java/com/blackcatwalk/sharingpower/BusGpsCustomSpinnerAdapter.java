package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BusGpsCustomSpinnerAdapter extends ArrayAdapter<CustomSpinner> {

    private Context context;
    private int itemLayoutId;
    private CustomSpinner[] bus = null;
    private int count1;
    private int count2;
    private int check;

    public BusGpsCustomSpinnerAdapter(Context context, int itemLayoutId, CustomSpinner[] bus) {
        super(context, itemLayoutId, bus);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.bus = bus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(itemLayoutId, parent, false);

        ImageView imgFlag = (ImageView) item.findViewById(R.id.icon_image);
        TextView txtBusName = (TextView) item.findViewById(R.id.bus_name);

        CustomSpinner bus = this.bus[position];
        imgFlag.setImageResource(bus.getImageId());

        txtBusName.setText(bus.getName());

        count1++;
        count2++;
        switch (position) {
            case 0:
                if (check == 0) {
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                }
                break;
            case 1:
                if (check == 1) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_bus_blue);
                }
                break;
            case 2:
                if (check == 2) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_bts_blue);
                }
                break;
            case 3:
                if (check == 3) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_brt_blue);
                }
                break;
            case 4:
                if (check == 4) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_van_blue);
                }
                break;
            case 5:
                if (check == 5) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_public_blue);
                }
                break;
            case 6:
                if (check == 6) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_boat_blue);
                }
                break;
            case 7:
                if (check == 7) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_accident_blue);
                }
                break;
            case 8:
                if (check == 8) {
                    imgFlag.setImageResource(R.drawable.traffic_spinner_checkpoint_blue);
                }
                break;
        }

        if (count1 >= 2 && count2 >= 6) {
            switch (position) {
                case 0:
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                    break;
                case 1:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_bus_blue);
                    break;
                case 2:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_bts_blue);
                    break;
                case 3:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_brt_blue);
                    break;
                case 4:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_van_blue);
                    break;
                case 5:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_public_blue);
                    break;
                case 6:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_boat_blue);
                    break;
                case 7:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_accident_blue);
                    break;
                case 8:
                    imgFlag.setImageResource(R.drawable.traffic_spinner_checkpoint_blue);
                    break;
            }
            count1 = 0;
            count2 = 6;
            check = position;
        }
        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        count1 = 0;
        return getView(position, convertView, parent);
    }

}
