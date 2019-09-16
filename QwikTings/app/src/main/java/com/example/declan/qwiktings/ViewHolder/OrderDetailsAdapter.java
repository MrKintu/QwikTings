/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.declan.qwiktings.Models.Order;
import com.example.declan.qwiktings.R;

import java.util.List;

class myViewHolder extends RecyclerView.ViewHolder
{
    public TextView name, quantity, price;

    public myViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
    }
}

public class OrderDetailsAdapter extends RecyclerView.Adapter<myViewHolder>
{
    List<Order> myOrders;

    public OrderDetailsAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_layout, parent, false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.name.setText(String.format("Name: %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantity: %s", order.getQuantity()));
        holder.price.setText(String.format("Price: %s", order.getPrice()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
