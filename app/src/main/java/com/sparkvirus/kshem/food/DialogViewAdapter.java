package com.sparkvirus.kshem.food;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sparkvirus.kshem.food.R;

import java.util.ArrayList;

/**
 * Created by kshem on 4/4/17.
 */

public class DialogViewAdapter extends BaseAdapter {
    public ArrayList<OrderData> list;
    Activity activity;
    TextView txtName;
    TextView txtPrice;
    TextView txtQty;
    TextView txtTotal;

    public DialogViewAdapter(Activity activity,ArrayList<OrderData> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.dialog_listview, null);

            txtName=(TextView) convertView.findViewById(R.id.confirm_name);
            txtPrice=(TextView) convertView.findViewById(R.id.confirm_price);
            txtQty = (TextView) convertView.findViewById(R.id.confirm_qty);
            txtTotal = (TextView) convertView.findViewById(R.id.totalPrice);

        }

        OrderData map=list.get(position);
        final String name = map.orderMap.get(Constants.NAME);
        final String price = map.orderMap.get(Constants.PRICE);
        txtName.setText(name);
        txtPrice.setText(price);
        txtQty.setText(Integer.toString(map.quantity));
        txtTotal.setText(Integer.toString(map.total_price));

        return convertView;
    }
}
