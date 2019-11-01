package com.sparkvirus.kshem.food;

import java.util.HashMap;

/**
 * Created by kshem on 4/4/17.
 */

public class OrderData {
    public HashMap<String, String> orderMap;
    public int quantity;
    public int total_price;

    public OrderData(){

    }

    public OrderData(HashMap<String, String> orderMap, int quantity, int total_price){
        this.orderMap = orderMap;
        this.quantity = quantity;
        this.total_price = total_price;
    }
}
