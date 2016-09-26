package com.blackcatwalk.sharingpower.customAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;
import com.blackcatwalk.sharingpower.customAdapter.CustomSpinnerBusLocattionNerbyMenu;

public class NearbyCustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerBusLocattionNerbyMenu> {

    private Context mContext;
    private int mIitemLayoutId;
    private CustomSpinnerBusLocattionNerbyMenu[] mNearBy = null;
    private int mCount1;
    private int mCount2;
    private int mCheck;

    public NearbyCustomSpinnerAdapter(Context _context, int _itemLayoutId, CustomSpinnerBusLocattionNerbyMenu[] _nearBy) {
        super(_context, _itemLayoutId, _nearBy);
        this.mContext = _context;
        this.mIitemLayoutId = _itemLayoutId;
        this.mNearBy = _nearBy;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _inflater = ((Activity) mContext).getLayoutInflater();
        View _item = _inflater.inflate(mIitemLayoutId, parent, false);

        ImageView imgFlag = (ImageView) _item.findViewById(R.id.icon_image);
        TextView txtBusName = (TextView) _item.findViewById(R.id.bus_name);

        CustomSpinnerBusLocattionNerbyMenu _nearBy = this.mNearBy[position];
        imgFlag.setImageResource(_nearBy.getImage());

        txtBusName.setText(_nearBy.getName());

        mCount1++;
        mCount2++;

        switch (position) {
            case 0:
                if (mCheck == 0) {
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                }
                break;
            case 1:
                if (mCheck == 1) {
                    imgFlag.setImageResource(R.drawable.mini1);
                }
                break;
            case 2:
                if (mCheck == 2) {
                    imgFlag.setImageResource(R.drawable.mini2);
                }
                break;
            case 3:
                if (mCheck == 3) {
                    imgFlag.setImageResource(R.drawable.mini3);
                }
                break;
            case 4:
                if (mCheck == 4) {
                    imgFlag.setImageResource(R.drawable.mini4);
                }
                break;
            case 5:
                if (mCheck == 5) {
                    imgFlag.setImageResource(R.drawable.mini5);
                }
                break;
            case 6:
                if (mCheck == 6) {
                    imgFlag.setImageResource(R.drawable.mini6);
                }
                break;
            case 7:
                if (mCheck == 7) {
                    imgFlag.setImageResource(R.drawable.mini7);
                }
                break;
            case 8:
                if (mCheck == 8) {
                    imgFlag.setImageResource(R.drawable.mini8);
                }
                break;
            case 9:
                if (mCheck == 9) {
                    imgFlag.setImageResource(R.drawable.mini9);
                }
                break;
            case 10:
                if (mCheck == 10) {
                    imgFlag.setImageResource(R.drawable.mini10);
                }
                break;
            case 11:
                if (mCheck == 11) {
                    imgFlag.setImageResource(R.drawable.mini11);
                }
                break;
            case 12:
                if (mCheck == 12) {
                    imgFlag.setImageResource(R.drawable.mini12);
                }
                break;
            case 13:
                if (mCheck == 13) {
                    imgFlag.setImageResource(R.drawable.mini13);
                }
                break;
            case 14:
                if (mCheck == 14) {
                    imgFlag.setImageResource(R.drawable.mini14);
                }
                break;
            case 15:
                if (mCheck == 15) {
                    imgFlag.setImageResource(R.drawable.mini15);
                }
                break;
            case 16:
                if (mCheck == 16) {
                    imgFlag.setImageResource(R.drawable.mini16);
                }
                break;
            case 17:
                if (mCheck == 17) {
                    imgFlag.setImageResource(R.drawable.mini17);
                }
                break;
            case 18:
                if (mCheck == 18) {
                    imgFlag.setImageResource(R.drawable.mini18);
                }
                break;
            case 19:
                if (mCheck == 19) {
                    imgFlag.setImageResource(R.drawable.mini19);
                }
                break;
        }

        if (mCount1 >= 2 && mCount2 >= 17) {

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

            mCheck = position;
            mCount1 = 0;
            mCount2 = 17;
        }

        return _item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        mCount1 = 0;
        return getView(position, convertView, parent);
    }
}
