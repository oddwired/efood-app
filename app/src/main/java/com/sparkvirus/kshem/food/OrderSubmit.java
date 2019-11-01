package com.sparkvirus.kshem.food;

/**
 * Created by kshem on 4/5/17.
 */

public class OrderSubmit {
    public String name;
    public String totalAmount;
    public String quantity;

    public OrderSubmit(){

    }

    public OrderSubmit(String name, String totalAmount, String quantity){
        this.name = name;
        this.totalAmount = totalAmount;
        this.quantity = quantity;
    }
}
