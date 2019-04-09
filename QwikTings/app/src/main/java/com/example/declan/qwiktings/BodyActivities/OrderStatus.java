package com.example.declan.qwiktings.BodyActivities;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.Models.Common;
import com.example.declan.qwiktings.Models.Requests;
import com.example.declan.qwiktings.R;
import com.example.declan.qwiktings.ViewHolder.OrderViewHolder;
import com.example.declan.qwiktings.ViewHolder.Order_Details;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity
{
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<Requests, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase Connection.
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Requests");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        recyclerView = findViewById(R.id.order_status_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(user.getEmail());
    }

    private void loadOrders(String email)
    {
        adapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(
                Requests.class, R.layout.order_layout, OrderViewHolder.class, reference.orderByChild("email").equalTo(email)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Requests model, int position)
            {
                TextView order_id = viewHolder.itemView.findViewById(R.id.order_id);
                order_id.setText(adapter.getRef(position).getKey());
                TextView order_status = viewHolder.itemView.findViewById(R.id.order_status);
                final String status = "0";
                order_status.setText(convertCodeToStatus(status));
                TextView order_email = viewHolder.itemView.findViewById(R.id.order_email);
                order_email.setText(user.getEmail());
                TextView order_address = viewHolder.itemView.findViewById(R.id.order_address);
                order_address.setText(model.getAddress());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!isLongClick)
                        {
                            Intent tracking = new Intent(OrderStatus.this, TrackingOrder.class);
                            Common.current_requests = model;
                            startActivity(tracking);
                        }
                        else
                        {
                            Intent orderdetails = new Intent(OrderStatus.this, Order_Details.class);
                            Common.current_requests = model;
                            orderdetails.putExtra("OrderID", adapter.getRef(position).getKey());
                            startActivity(orderdetails);
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public String convertCodeToStatus(String status)
    {
        if(status.equals("0"))
            return "Placed";

        else if(status.equals("1"))
            return "On the way!";

        else
            return "Arrived!";
    }

}
