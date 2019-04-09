package com.example.declan.qwiktings.BodyActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.Models.QwikShop;
import com.example.declan.qwiktings.R;
import com.example.declan.qwiktings.ViewHolder.QwikShopViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class QwikShop_List extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference items;
    private String categoryid="";
    private FirebaseRecyclerAdapter<QwikShop,QwikShopViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwikshop_list);

        //firebase database
        database = FirebaseDatabase.getInstance();
        items = database.getReference("Items");

        //load items
        recyclerView = findViewById(R.id.recycler_items);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent here
        if (getIntent() != null)
            categoryid = getIntent().getStringExtra("CategoryId");
        if (!categoryid.isEmpty() && categoryid != null)
        {

            adapter = new FirebaseRecyclerAdapter<QwikShop, QwikShopViewHolder>(QwikShop.class, R.layout.qwikshop_items, QwikShopViewHolder.class, items.orderByChild("menuID")) {

                @Override
                protected void populateViewHolder(QwikShopViewHolder viewHolder, QwikShop model, int position)
                {
                    viewHolder.itemname.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.itemimage);

                    final QwikShop local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View v, int position, boolean isLongClick) {
                            //start new activity
                            Intent details = new Intent(QwikShop_List.this, ItemDetails.class);
                            details.putExtra("MenuId", adapter.getRef(position).getKey());
                            startActivity(details);
                        }
                    });
                }
            };
            recyclerView.setAdapter(adapter);
    }

    }
}
