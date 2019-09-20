/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.BodyActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.declan.qwiktings.Databases.Database;
import com.example.declan.qwiktings.Models.Order;
import com.example.declan.qwiktings.Models.Requests;
import com.example.declan.qwiktings.R;
import com.example.declan.qwiktings.ViewHolder.CartAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView total_price;
    private FButton btnPlace;
    private List<Order> cart = new ArrayList<>();
    private CartAdapter adapter;
    private Order order = new Order();
    private Place shipToAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Requests");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        total_price = findViewById(R.id.total);
        btnPlace = findViewById(R.id.PlaceOrder);
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrder();

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog()
    {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Cart.this);
        alertdialog.setTitle("One more Step!");
        alertdialog.setMessage("Enter Address and Pay for Goods");

        final String status = "0";

        LayoutInflater inflater = this.getLayoutInflater();
        View cart_address = inflater.inflate(R.layout.cart_address_google_places, null);

        final PlaceAutocompleteFragment address = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        address.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText) address.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");
        ((EditText) address.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);
        ((EditText) address.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextColor(Color.BLACK);

        //Get Address from Autocomplete Fragment
        address.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shipToAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR", status.getStatusMessage());
            }
        });

        alertdialog.setView(cart_address);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Requests request =  new Requests(
                        String.valueOf(user.getEmail()), String.valueOf(shipToAddress.getAddress()),
                        String.valueOf(order.getProductName()), String.valueOf(total_price.getText()), status,
                        cart, shipToAddress.getLatLng().latitude, shipToAddress.getLatLng().longitude);

                //Send To Firebase
                reference.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                //Delete Cart
                new Database(getBaseContext()).clearCart(order);

                //Remove Fragment
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment))
                        .commit();

                //Launch Pay Activity
                Intent pay = new Intent(getApplicationContext(), PayActivity.class);
                pay.putExtra("Price", String.valueOf(total_price.getText()));
                startActivity(pay);

                //Confirm Order
                Toast.makeText(Cart.this, "Your Order Has Been Placed. Thank You", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Remove Fragment
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment))
                        .commit();

                dialog.dismiss();
            }
        });

        alertdialog.show();
    }

    private void loadOrder()
    {
        cart = new Database(getApplicationContext()).getCart();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        //Calculate Total Price
        int total = 0;
        for(Order order:cart)
            total += (Integer.valueOf(order.getPrice())) * (Integer.valueOf(order.getQuantity()));

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        total_price.setText(fmt.format(total));
    }
}
