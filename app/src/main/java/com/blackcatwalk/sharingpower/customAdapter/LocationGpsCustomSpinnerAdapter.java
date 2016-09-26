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

public class LocationGpsCustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerBusLocattionNerbyMenu> {

    private Context mContext;
    private int mItemLayoutId;
    private CustomSpinnerBusLocattionNerbyMenu[] mLocation = null;
    private int mCount1;
    private int mCount2;
    private int mCheck;

    public LocationGpsCustomSpinnerAdapter(Context _context, int _itemLayoutId, CustomSpinnerBusLocattionNerbyMenu[] _location) {
        super(_context, _itemLayoutId, _location);
        this.mContext = _context;
        this.mItemLayoutId = _itemLayoutId;
        this.mLocation = _location;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {

        LayoutInflater _inflater = ((Activity) mContext).getLayoutInflater();
        View _virw = _inflater.inflate(mItemLayoutId, _parent, false);

        ImageView imgFlag = (ImageView) _virw.findViewById(R.id.icon_image);
        TextView txtBusName = (TextView) _virw.findViewById(R.id.bus_name);

        CustomSpinnerBusLocattionNerbyMenu location = this.mLocation[_position];
        imgFlag.setImageResource(location.getImage());
        txtBusName.setText(location.getName());

        mCount1++;
        mCount2++;
        switch (_position) {
            case 0:
                if (mCheck == 0) {
                    imgFlag.setImageResource(R.drawable.spinner_search_blue);
                }
                break;
            case 1:
                if (mCheck == 1) {
                    imgFlag.setImageResource(R.drawable.location_spinner_restroom_blue);
                }
                break;
            case 2:
                if (mCheck == 2) {
                    imgFlag.setImageResource(R.drawable.location_spinner_medical_blue);
                }
                break;
            case 3:
                if (mCheck == 3) {
                    imgFlag.setImageResource(R.drawable.location_spinner_animal_blue);
                }
                break;
            case 4:
                if (mCheck == 4) {
                    imgFlag.setImageResource(R.drawable.location_spinner_relax_blue);
                }
                break;
            case 5:
                if (mCheck == 5) {
                    imgFlag.setImageResource(R.drawable.location_spinner_home_blue);
                }
                break;
            case 6:
                if (mCheck == 6) {
                    imgFlag.setImageResource(R.drawable.location_spinner_garage_blue);
                }
                break;
        }

        if (mCount1 >= 2 && mCount2 >= 4) {
            switch (_position) {
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
            mCount1 = 0;
            mCount2 = 4;
            mCheck = _position;
        }
        return _virw;
    }

    @Override
    public View getDropDownView(int _position, View _convertView, ViewGroup _parent) {
        mCount1 = 0;
        return getView(_position, _convertView, _parent);
    }
}
