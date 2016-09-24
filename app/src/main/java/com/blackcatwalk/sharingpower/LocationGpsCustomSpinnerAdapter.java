package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationGpsCustomSpinnerAdapter extends ArrayAdapter<CustomSpinner> {

    private Context context;
    private int itemLayoutId;
    private CustomSpinner[] location = null;
    private int count1;
    private int count2;
    private int check;

    public LocationGpsCustomSpinnerAdapter(Context context, int itemLayoutId, CustomSpinner[] location) {
        super(context, itemLayoutId, location);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.location = location;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(itemLayoutId, parent, false);

        ImageView imgFlag = (ImageView) item.findViewById(R.id.icon_image);
        TextView txtBusName = (TextView) item.findViewById(R.id.bus_name);

        CustomSpinner location = this.location[position];
        imgFlag.setImageResource(location.getImageId());
        txtBusName.setText(location.getName());

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
                    imgFlag.setImageResource(R.drawable.location_spinner_restroom_blue);
                }
                break;
            case 2:
                if (check == 2) {
                    imgFlag.setImageResource(R.drawable.location_spinner_medical_blue);
                }
                break;
            case 3:
                if (check == 3) {
                    imgFlag.setImageResource(R.drawable.location_spinner_animal_blue);
                }
                break;
            case 4:
                if (check == 4) {
                    imgFlag.setImageResource(R.drawable.location_spinner_relax_blue);
                }
                break;
            case 5:
                if (check == 5) {
                    imgFlag.setImageResource(R.drawable.location_spinner_home_blue);
                }
                break;
            case 6:
                if (check == 6) {
                    imgFlag.setImageResource(R.drawable.location_spinner_garage_blue);
                }
                break;
        }

        if (count1 >= 2 && count2 >= 4) {
            switch (position) {
                case 0:
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                    break;
                case 1:
                    imgFlag.setImageResource(R.drawable.location_spinner_restroom_blue);
                    break;
                case 2:
                    imgFlag.setImageResource(R.drawable.location_spinner_medical_blue);
                    break;
                case 3:
                    imgFlag.setImageResource(R.drawable.location_spinner_animal_blue);
                    break;
                case 4:
                    imgFlag.setImageResource(R.drawable.location_spinner_relax_blue);
                    break;
                case 5:
                    imgFlag.setImageResource(R.drawable.location_spinner_home_blue);
                    break;
                case 6:
                    imgFlag.setImageResource(R.drawable.location_spinner_garage_blue);
                    break;
            }
            count1 = 0;
            count2 = 4;
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
