package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FavoriteCustomListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity activity;
    private List<Favorite> items;

    public FavoriteCustomListAdapter(Activity activity, List<Favorite> items) {
        this.activity = activity;
        this.items = items;
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
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_row_favorite, null);
        }

        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
        TextView detail = (TextView) convertView.findViewById(R.id.textDetail);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        //getting data for row
        Favorite item = items.get(position);

        switch (item.getType()) {
            case "atm":
                logo.setImageResource(R.drawable.logo_atm);
                break;
            case "bank":
                logo.setImageResource(R.drawable.logo_bank);
                break;
            case "bus_station":
                logo.setImageResource(R.drawable.logo_bus_station);
                break;
            case "doctor":
                logo.setImageResource(R.drawable.logo_doctor);
                break;
            case "police":
                logo.setImageResource(R.drawable.logo_police);
                break;
            case "hospital":
                logo.setImageResource(R.drawable.logo_hospital);
                break;
            case "restaurant":
                logo.setImageResource(R.drawable.logo_restaurant);
                break;
            case "cafe":
                logo.setImageResource(R.drawable.logo_cafe);
                break;
            case "department_store":
                logo.setImageResource(R.drawable.logo_department_store);
                break;
            case "shopping_mall":
                logo.setImageResource(R.drawable.logo_shopping_mall);
                break;
            case "grocery_or_supermarket":
                logo.setImageResource(R.drawable.logo_grocery_or_supermarket);
                break;
            case "beauty_salon":
                logo.setImageResource(R.drawable.logo_beauty_salon);
                break;
            case "gym":
                logo.setImageResource(R.drawable.logo_gym);
                break;
            case "post_office":
                logo.setImageResource(R.drawable.logo_post_office);
                break;
            case "school":
                logo.setImageResource(R.drawable.logo_school);
                break;
            case "university":
                logo.setImageResource(R.drawable.logo_university);
                break;
            case "gas_station":
                logo.setImageResource(R.drawable.logo_gas_station);
                break;
            case "parking":
                logo.setImageResource(R.drawable.logo_parking);
                break;
            case "car_repair":
                logo.setImageResource(R.drawable.logo_car_repair);
                break;
            case "restroom":
                logo.setImageResource(R.drawable.logo_toilet);
                break;
            case "pharmacy":
                logo.setImageResource(R.drawable.logo_medicine);
                break;
            case "clinic":
                logo.setImageResource(R.drawable.logo_clinic);
                break;
            case "veterinary_clinic":
                logo.setImageResource(R.drawable.logo_dog);
                break;
            case "daily_home":
                logo.setImageResource(R.drawable.logo_home);
                break;
            case "officer":
                logo.setImageResource(R.drawable.logo_relax);
                break;
            case "garage":
                logo.setImageResource(R.drawable.logo_tires);
                break;
        }

        if(item.getDetail().length() > 60){
            detail.setText(item.getDetail().substring(0,50) + "...");
        }else{
            detail.setText(item.getDetail());
        }
        time.setText(item.getTime());

        return convertView;
    }

}
