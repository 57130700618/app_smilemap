package com.blackcatwalk.sharingpower.customAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.Favorite;
import com.blackcatwalk.sharingpower.R;

import java.util.List;

public class FavoriteCustomListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<Favorite> mItems;

    public FavoriteCustomListAdapter(Activity _activity, List<Favorite> _items) {
        this.mActivity = _activity;
        this.mItems = _items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (_convertView == null) {
            _convertView = mInflater.inflate(R.layout.activity_row_favorite, null);
        }

        ImageView _logo = (ImageView) _convertView.findViewById(R.id.logo);
        TextView _detail = (TextView) _convertView.findViewById(R.id.textDetail);
        TextView _time = (TextView) _convertView.findViewById(R.id.time);

        Favorite _item = mItems.get(_position);

        switch (_item.getType()) {
            case "atm":
                _logo.setImageResource(R.drawable.logo_atm);
                break;
            case "bank":
                _logo.setImageResource(R.drawable.logo_bank);
                break;
            case "bus_station":
                _logo.setImageResource(R.drawable.logo_bus_station);
                break;
            case "doctor":
                _logo.setImageResource(R.drawable.logo_doctor);
                break;
            case "police":
                _logo.setImageResource(R.drawable.logo_police);
                break;
            case "hospital":
                _logo.setImageResource(R.drawable.logo_hospital);
                break;
            case "restaurant":
                _logo.setImageResource(R.drawable.logo_restaurant);
                break;
            case "cafe":
                _logo.setImageResource(R.drawable.logo_cafe);
                break;
            case "department_store":
                _logo.setImageResource(R.drawable.logo_department_store);
                break;
            case "shopping_mall":
                _logo.setImageResource(R.drawable.logo_shopping_mall);
                break;
            case "grocery_or_supermarket":
                _logo.setImageResource(R.drawable.logo_grocery_or_supermarket);
                break;
            case "beauty_salon":
                _logo.setImageResource(R.drawable.logo_beauty_salon);
                break;
            case "gym":
                _logo.setImageResource(R.drawable.logo_gym);
                break;
            case "post_office":
                _logo.setImageResource(R.drawable.logo_post_office);
                break;
            case "school":
                _logo.setImageResource(R.drawable.logo_school);
                break;
            case "university":
                _logo.setImageResource(R.drawable.logo_university);
                break;
            case "gas_station":
                _logo.setImageResource(R.drawable.logo_gas_station);
                break;
            case "parking":
                _logo.setImageResource(R.drawable.logo_parking);
                break;
            case "car_repair":
                _logo.setImageResource(R.drawable.logo_car_repair);
                break;
            case "restroom":
                _logo.setImageResource(R.drawable.logo_toilet);
                break;
            case "pharmacy":
                _logo.setImageResource(R.drawable.logo_medicine);
                break;
            case "clinic":
                _logo.setImageResource(R.drawable.logo_clinic);
                break;
            case "veterinary_clinic":
                _logo.setImageResource(R.drawable.logo_dog);
                break;
            case "daily_home":
                _logo.setImageResource(R.drawable.logo_home);
                break;
            case "officer":
                _logo.setImageResource(R.drawable.logo_relax);
                break;
            case "garage":
                _logo.setImageResource(R.drawable.logo_tires);
                break;
        }

        if(_item.getDetail().length() > 60){
            _detail.setText(_item.getDetail().substring(0,50) + "...");
        }else{
            _detail.setText(_item.getDetail());
        }
        _time.setText(_item.getTime());

        return _convertView;
    }
}
