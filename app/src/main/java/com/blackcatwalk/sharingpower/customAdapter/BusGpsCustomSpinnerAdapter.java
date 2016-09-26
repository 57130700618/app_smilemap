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

public class BusGpsCustomSpinnerAdapter extends ArrayAdapter<CustomSpinnerBusLocattionNerbyMenu> {

    private Context mContext;
    private int mItemLayoutId;
    private CustomSpinnerBusLocattionNerbyMenu[] mBus = null;
    private int mCount1;
    private int mCount2;
    private int mCheck;

    public BusGpsCustomSpinnerAdapter(Context _context, int _itemLayoutId, CustomSpinnerBusLocattionNerbyMenu[] _bus) {
        super(_context, _itemLayoutId, _bus);
        this.mContext = _context;
        this.mItemLayoutId = _itemLayoutId;
        this.mBus = _bus;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {

        LayoutInflater _inflater = ((Activity) mContext).getLayoutInflater();
        View _item = _inflater.inflate(mItemLayoutId, _parent, false);

        ImageView _imgFlag = (ImageView) _item.findViewById(R.id.icon_image);
        TextView _txtBusName = (TextView) _item.findViewById(R.id.bus_name);

        CustomSpinnerBusLocattionNerbyMenu _bus = this.mBus[_position];
        _imgFlag.setImageResource(_bus.getImage());

        _txtBusName.setText(_bus.getName());

        mCount1++;
        mCount2++;
        switch (_position) {
            case 0:
                if (mCheck == 0) {
                    _imgFlag.setImageResource(R.drawable.spinner_search_blue);
                }
                break;
            case 1:
                if (mCheck == 1) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_bus_blue);
                }
                break;
            case 2:
                if (mCheck == 2) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_bts_blue);
                }
                break;
            case 3:
                if (mCheck == 3) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_brt_blue);
                }
                break;
            case 4:
                if (mCheck == 4) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_van_blue);
                }
                break;
            case 5:
                if (mCheck == 5) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_public_blue);
                }
                break;
            case 6:
                if (mCheck == 6) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_boat_blue);
                }
                break;
            case 7:
                if (mCheck == 7) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_accident_blue);
                }
                break;
            case 8:
                if (mCheck == 8) {
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_checkpoint_blue);
                }
                break;
        }

        if (mCount1 >= 2 && mCount2 >= 6) {
            switch (_position) {
                case 0:
                    _imgFlag.setImageResource(R.drawable.spinner_search_blue);
                    break;
                case 1:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_bus_blue);
                    break;
                case 2:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_bts_blue);
                    break;
                case 3:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_brt_blue);
                    break;
                case 4:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_van_blue);
                    break;
                case 5:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_public_blue);
                    break;
                case 6:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_boat_blue);
                    break;
                case 7:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_accident_blue);
                    break;
                case 8:
                    _imgFlag.setImageResource(R.drawable.traffic_spinner_checkpoint_blue);
                    break;
            }
            mCount1 = 0;
            mCount2 = 6;
            mCheck = _position;
        }
        return _item;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        mCount1 = 0;
        return getView(position, convertView, parent);
    }

}
