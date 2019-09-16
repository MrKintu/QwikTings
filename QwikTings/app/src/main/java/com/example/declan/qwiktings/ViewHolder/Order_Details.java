/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.ViewHolder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.declan.qwiktings.Models.Common;
import com.example.declan.qwiktings.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Order_Details extends AppCompatActivity
{
    private TextView order_id, order_email, order_address, order_total;
    private String order_id_value = "";
    private RecyclerView listOrders;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__details);

        //Get Firebase auth instance
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        order_id = findViewById(R.id.order_id);
        order_email = findViewById(R.id.order_email);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_price);

        listOrders = findViewById(R.id.listOrders);
        listOrders.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOrders.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderID");

        order_id.setText(order_id_value);
        order_email.setText(user.getEmail());
        order_address.setText(Common.current_requests.getAddress());
        order_total.setText(Common.current_requests.getTotal());

        OrderDetailsAdapter adapter = new OrderDetailsAdapter(Common.current_requests.getList());
        adapter.notifyDataSetChanged();
        listOrders.setAdapter(adapter);
    }
}
