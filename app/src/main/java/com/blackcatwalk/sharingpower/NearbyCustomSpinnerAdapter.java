package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyCustomSpinnerAdapter extends ArrayAdapter<CustomSpinner> {

    private Context context;
    private int itemLayoutId;
    private CustomSpinner[] nearBy = null;
    private int count1;
    private int count2;
    private int check;

    public NearbyCustomSpinnerAdapter(Context context, int itemLayoutId, CustomSpinner[] nearBy) {
        super(context, itemLayoutId, nearBy);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.nearBy = nearBy;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(itemLayoutId, parent, false);

        ImageView imgFlag = (ImageView) item.findViewById(R.id.icon_image);
        TextView txtBusName = (TextView) item.findViewById(R.id.bus_name);

        CustomSpinner nearBy = this.nearBy[position];
        imgFlag.setImageResource(nearBy.getImageId());

        txtBusName.setText(nearBy.getName());

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
                    imgFlag.setImageResource(R.drawable.mini1);
                }
                break;
            case 2:
                if (check == 2) {
                    imgFlag.setImageResource(R.drawable.mini2);
                }
                break;
            case 3:
                if (check == 3) {
                    imgFlag.setImageResource(R.drawable.mini3);
                }
                break;
            case 4:
                if (check == 4) {
                    imgFlag.setImageResource(R.drawable.mini4);
                }
                break;
            case 5:
                if (check == 5) {
                    imgFlag.setImageResource(R.drawable.mini5);
                }
                break;
            case 6:
                if (check == 6) {
                    imgFlag.setImageResource(R.drawable.mini6);
                }
                break;
            case 7:
                if (check == 7) {
                    imgFlag.setImageResource(R.drawable.mini7);
                }
                break;
            case 8:
                if (check == 8) {
                    imgFlag.setImageResource(R.drawable.mini8);
                }
                break;
            case 9:
                if (check == 9) {
                    imgFlag.setImageResource(R.drawable.mini9);
                }
                break;
            case 10:
                if (check == 10) {
                    imgFlag.setImageResource(R.drawable.mini10);
                }
                break;
            case 11:
                if (check == 11) {
                    imgFlag.setImageResource(R.drawable.mini11);
                }
                break;
            case 12:
                if (check == 12) {
                    imgFlag.setImageResource(R.drawable.mini12);
                }
                break;
            case 13:
                if (check == 13) {
                    imgFlag.setImageResource(R.drawable.mini13);
                }
                break;
            case 14:
                if (check == 14) {
                    imgFlag.setImageResource(R.drawable.mini14);
                }
                break;
            case 15:
                if (check == 15) {
                    imgFlag.setImageResource(R.drawable.mini15);
                }
                break;
            case 16:
                if (check == 16) {
                    imgFlag.setImageResource(R.drawable.mini16);
                }
                break;
            case 17:
                if (check == 17) {
                    imgFlag.setImageResource(R.drawable.mini17);
                }
                break;
            case 18:
                if (check == 18) {
                    imgFlag.setImageResource(R.drawable.mini18);
                }
                break;
            case 19:
                if (check == 19) {
                    imgFlag.setImageResource(R.drawable.mini19);
                }
                break;
        }

        if (count1 >= 2 && count2 >= 17) {

            switch (position) {
                case 0:
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                    break;
                case 1:
                    imgFlag.setImageResource(R.drawable.mini1);
                    break;
                case 2:
                    imgFlag.setImageResource(R.drawable.mini2);
                    break;
                case 3:
                    imgFlag.setImageResource(R.drawable.mini3);
                    break;
                case 4:
                    imgFlag.setImageResource(R.drawable.mini4);
                    break;
                case 5:
                    imgFlag.setImageResource(R.drawable.mini5);
                    break;
                case 6:
                    imgFlag.setImageResource(R.drawable.mini6);
                    break;
                case 7:
                    imgFlag.setImageResource(R.drawable.mini7);
                    break;
                case 8:
                    imgFlag.setImageResource(R.drawable.mini8);
                    break;
                case 9:
                    imgFlag.setImageResource(R.drawable.mini9);
                    break;
                case 10:
                    imgFlag.setImageResource(R.drawable.mini10);
                    break;
                case 11:
                    imgFlag.setImageResource(R.drawable.mini11);
                    break;
                case 12:
                    imgFlag.setImageResource(R.drawable.mini12);
                    break;
                case 13:
                    imgFlag.setImageResource(R.drawable.mini13);
                    break;
                case 14:
                    imgFlag.setImageResource(R.drawable.mini14);
                    break;
                case 15:
                    imgFlag.setImageResource(R.drawable.mini15);
                    break;
                case 16:
                    imgFlag.setImageResource(R.drawable.mini16);
                    break;
                case 17:
                    imgFlag.setImageResource(R.drawable.mini17);
                    break;
                case 18:
                    imgFlag.setImageResource(R.drawable.mini18);
                    break;
                case 19:
                    imgFlag.setImageResource(R.drawable.mini19);
                    break;
            }

            check = position;
            count1 = 0;
            count2 = 17;
        }

        return item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        count1 = 0;
        return getView(position, convertView, parent);
    }
}
