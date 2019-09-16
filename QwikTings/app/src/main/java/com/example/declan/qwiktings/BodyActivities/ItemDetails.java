/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.BodyActivities;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import com.example.declan.qwiktings.Databases.Database;
import com.example.declan.qwiktings.Models.Order;
import com.example.declan.qwiktings.Models.QwikShop;
import com.example.declan.qwiktings.R;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

public class ItemDetails extends AppCompatActivity {

    private TextView item_name, item_price, item_description;
    private ImageView item_image;
    private CollapsingToolbarLayout collapsing;
    private FloatingActionButton btncart;
    private ElegantNumberButton numberButton;
    private String itemId = "";
    private FirebaseDatabase database;
    private DatabaseReference items;
    private QwikShop qwikShop;
    private Database data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        //Firebase
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        items = database.getReference("Items");

        //Initiate View
        numberButton = findViewById(R.id.number_button);
        btncart = findViewById(R.id.btncart);
        item_description = findViewById(R.id.item_description);
        item_name = findViewById(R.id.item_name);
        item_price = findViewById(R.id.item_price);
        item_image = findViewById(R.id.img_item);
        collapsing = findViewById(R.id.collapsing);

        collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getApplicationContext()).addtoCart(new Order(
                        qwikShop.getMenuID(), qwikShop.getName(), numberButton.getNumber(), qwikShop.getPrice()));
                Toast.makeText(getApplicationContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
            }
        });

        //Get Item ID
        if(getIntent() != null) {
            itemId = getIntent().getStringExtra("MenuId");
        }
        if(!itemId.isEmpty())
        {
            getDetails(itemId);
        }
    }

    private void getDetails(String itemId)
    {
        items.child(itemId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                qwikShop = dataSnapshot.getValue(QwikShop.class);

                //set image
                Picasso.with(getBaseContext()).load(qwikShop.getImage()).into(item_image);

                collapsing.setTitle(qwikShop.getName());
                item_price.setText(String.valueOf(qwikShop.getPrice()));
                item_name.setText(qwikShop.getName());
                item_description.setText(qwikShop.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
