package com.sparkvirus.kshem.food;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sparkvirus.kshem.food.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kshem on 4/3/17.
 */

public class ListViewAdapter extends BaseExpandableListAdapter {

    TextView txtName, headerLabel,txtOrderName;
    TextView txtPrice;
    Button btnOrder, btnCancelOrder, btnDiagOrder;
    EditText edtQty;

    OrderData orderData;

    private Context _context;
    private ArrayList<String> _listDataHeader;
    private HashMap<String, List<HashMap<String, String>>> _listDataChild = new HashMap<>();
    public ListViewAdapter(Context context, ArrayList<String> listDataHeader, HashMap<String, List<HashMap<String,String>>> listChildData){
        this._context = context;
        this._listDataChild = listChildData;
        this._listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition){
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){

        //TODO
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_listview, null);

        }

        txtName=(TextView) convertView.findViewById(R.id.name);
        txtPrice=(TextView) convertView.findViewById(R.id.price);
        btnOrder = (Button) convertView.findViewById(R.id.btn_order);

        btnOrder.setText("Order");

        final HashMap<String, String> map = (HashMap<String, String>) getChild(groupPosition, childPosition);
        final String name = map.get(Constants.NAME);
        final String price = map.get(Constants.PRICE);
        txtName.setText(name);
        txtPrice.setText("Price: Ksh" + price);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final HashMap<String, String> temp = new HashMap<String, String>();
                temp.put(Constants.NAME, name);
                temp.put(Constants.PRICE, price);

                final Dialog dialog = new Dialog(btnOrder.getContext());
                dialog.setContentView(R.layout.order_layout);
                dialog.setTitle("Enter Your Order");

                txtOrderName = (TextView) dialog.findViewById(R.id.order_name);
                edtQty = (EditText) dialog.findViewById(R.id.enter_qty);
                btnCancelOrder = (Button) dialog.findViewById(R.id.cancel_order);
                btnDiagOrder = (Button) dialog.findViewById(R.id.place_order);

                dialog.show();

                orderData = new OrderData();
                orderData.orderMap = temp;

                for(int i = 0; i < MenuActivity.orderList.size(); i++){
                    if(MenuActivity.orderList.get(i).orderMap.get(Constants.NAME) == orderData.orderMap.get(Constants.NAME)){
                        orderData = MenuActivity.orderList.get(i);
                        MenuActivity.orderList.remove(i);
                        edtQty.setText(Integer.toString(orderData.quantity));
                        break;
                    }
                }

                btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btnOrder.setText("Order");
                        dialog.cancel();
                    }
                });

                btnDiagOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        orderData.quantity = Integer.parseInt(edtQty.getText().toString());

                        orderData.total_price = orderData.quantity * Integer.parseInt(price);
                        MenuActivity.orderList.add(orderData);
                        MenuActivity.confirmOrder.setEnabled(true);
                        dialog.cancel();
                    }
                });



                //MenuActivity.orderList.add(temp);
                //MenuActivity.confirmOrder.setEnabled(true);
            }
        });


                return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_listgroup, null);
        }

        headerLabel = (TextView) convertView.findViewById(R.id.header_label);
        headerLabel.setTypeface(null, Typeface.BOLD);
        headerLabel.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
