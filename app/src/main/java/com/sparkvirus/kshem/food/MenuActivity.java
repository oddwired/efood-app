package com.sparkvirus.kshem.food;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparkvirus.kshem.food.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    Menu mainMenu=null;
    public static ListView order_list, pending_order;
    public static ExpandableListView menu_list;
    public static Button confirmOrder;
    Button sendOrder, editOrder, cancelOrder, pendingOrders, diag_ok;

    //ArrayList<MenuData> menuList;
    HashMap<String, List<HashMap<String, String>>> menuList;
    public static ArrayList<OrderData> orderList, pendingList;
    ListViewAdapter menuAdapter;
    DialogViewAdapter orderAdapter, pendingAdapter;
    ArrayList<String> categories = new ArrayList<String>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mFirebaseRef, mFirebaseRefOrder;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                    finish();
                }else{
                    userId = mAuth.getCurrentUser().getUid();
                }
            }
        };



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        menu_list = (ExpandableListView) findViewById(R.id.menulist);
        menuList = new HashMap<String, List<HashMap<String,String>>>();
        orderList = new ArrayList<OrderData>();
        pendingList = new ArrayList<OrderData>();

        confirmOrder = (Button) findViewById(R.id.btn_confirmorder);
        confirmOrder.setEnabled(false);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRef = mFirebaseInstance.getReference("menu");
        mFirebaseRef.keepSynced(true);

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> menu_items = (HashMap<String, Object>) dataSnapshot.getValue();

                menuList.clear();
                ArrayList<HashMap<String, String>> menu = new ArrayList<HashMap<String, String>>();
                try{
                    for(Object menu_item : menu_items.values()) {
                        HashMap<String, Object> menuMap = (HashMap<String, Object>) menu_item;
                        String name = (String) menuMap.remove("name");
                        String price = (String) menuMap.remove("price");
                        String menu_category = (String) menuMap.remove("category");
                        String qty = (String) menuMap.remove("qty");

                        HashMap<String, String> temp = new HashMap<String, String>();
                        temp.put(Constants.NAME, name);
                        temp.put(Constants.PRICE, price);
                        temp.put(Constants.CATEGORY, menu_category);

                        boolean add = true;
                        for(int i = 0; i < menu.size(); i++){
                            if(menu.get(i).get(Constants.NAME) == temp.get(Constants.NAME)){
                                add = false;
                                break;
                            }
                        }

                        if(add){
                            menu.add(temp);
                        }

                        boolean add_category = true;

                        if(categories == null) {
                            categories.add(menu_category);
                        }else{
                            for (String category : categories) {
                                if (category.equals(menu_category)) {
                                    add_category = false;
                                }
                            }

                            if(add_category){
                                categories.add(menu_category);
                            }
                        }

                        //HashMap<String, HashMap<String, String>> temp1 = new HashMap<String, HashMap<String, String>>();
                        //menu.put(menu_category, temp);

                        //menu.add(temp1);

                    }
                }catch (NullPointerException nullPointer){

                }


                for(String category : categories){

                    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    for(int i = 0; i < menu.size(); i++){
                        HashMap<String,String> temp = new HashMap<String, String>();

                        if(menu.get(i).get(Constants.CATEGORY).equals(category)){
                            temp.put(Constants.NAME, menu.get(i).remove(Constants.NAME));
                            temp.put(Constants.PRICE, menu.get(i).remove(Constants.PRICE));
                            temp.put(Constants.CATEGORY, category);
                            list.add(temp);
                        }
                    }
                    menuList.put(category, list);
                }

                menuAdapter = new ListViewAdapter(MenuActivity.this,categories, menuList);
                menu_list.setAdapter(menuAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pendingOrders = (Button) findViewById(R.id.btn_refresh);
        pendingOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.layout_pending);
                dialog.setTitle("Pending Orders");

                pending_order = (ListView) dialog.findViewById(R.id.pending_dialog_view);
                diag_ok = (Button) dialog.findViewById(R.id.ok_btn);

                diag_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mFirebaseInstance.getReference("orders").child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> pending_items = (HashMap<String, Object>) dataSnapshot.getValue();

                        try {
                            for (Object pending_item : pending_items.values()) {
                                HashMap<String, Object> menuMap = (HashMap<String, Object>) pending_item;
                                String name = (String) menuMap.remove("name");
                                String quantity = (String) menuMap.remove("quantity");
                                String totalAmount = (String) menuMap.remove("totalAmount");

                                String price = Integer.toString(Integer.parseInt(totalAmount) / Integer.parseInt(quantity));

                                HashMap<String, String> temp = new HashMap<String, String>();
                                temp.put(Constants.NAME, name);
                                temp.put(Constants.PRICE, price);

                                OrderData orderData = new OrderData();
                                orderData.orderMap = temp;
                                orderData.quantity = Integer.parseInt(quantity);
                                orderData.total_price = Integer.parseInt(totalAmount);

                                boolean add = true;
                                for (int i = 0; i < pendingList.size(); i++) {
                                    if (pendingList.get(i).orderMap.get(Constants.NAME) == temp.get(Constants.NAME)) {
                                        add = false;
                                        break;
                                    }
                                }

                                if (add) {
                                    pendingList.add(orderData);
                                }


                            }
                        }catch (NullPointerException nullPointer){

                        }

                        int sumTotal = 0;
                        for(OrderData order : pendingList){
                            sumTotal += order.total_price;
                        }
                        TextView totalAmount = (TextView) dialog.findViewById(R.id.total_amount);
                        totalAmount.setText("Ksh " + sumTotal);

                        pendingAdapter = new DialogViewAdapter(MenuActivity.this, pendingList);
                        pending_order.setAdapter(pendingAdapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                dialog.show();
            }
        });

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.layout_dialog);
                dialog.setTitle("Confirm Your Order");

                order_list = (ListView) dialog.findViewById(R.id.dialog_view);
                orderAdapter = new DialogViewAdapter(MenuActivity.this, orderList);
                order_list.setAdapter(orderAdapter);
                dialog.show();

                TextView totalAmount = (TextView) dialog.findViewById(R.id.total_amount);
                sendOrder = (Button) dialog.findViewById(R.id.sendorder);
                editOrder = (Button) dialog.findViewById(R.id.editorder);
                cancelOrder = (Button) dialog.findViewById(R.id.cancelorder);

                int sumTotal = 0;
                for(OrderData order : orderList){
                    sumTotal += order.total_price;
                }

                totalAmount.setText("Ksh " + Integer.toString(sumTotal));

                sendOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog pd = new ProgressDialog(dialog.getContext());
                        pd.setMessage("Please Wait");
                        pd.show();

                        submitData();

                        orderList.clear();
                        dialog.dismiss();
                        confirmOrder.setEnabled(false);
                        pd.hide();
                    }
                });

                editOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                cancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderList.clear();
                        dialog.cancel();
                    }
                });
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void submitData(){
        mFirebaseRefOrder = mFirebaseInstance.getReference("orders");

        for(OrderData order : orderList){

            HashMap<String, String> temp = order.orderMap;
            OrderSubmit data = new OrderSubmit(temp.remove(Constants.NAME), Integer.toString(order.total_price) ,Integer.toString(order.quantity));

            mFirebaseRefOrder.child(userId).push().setValue(data);

            //Toast.makeText(dialog.getContext(), "The loop iterator works", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        mainMenu=menu;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_MENU) {
            getMenuInflater();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                return true;

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
