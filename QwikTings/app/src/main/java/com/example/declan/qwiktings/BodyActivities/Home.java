/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.BodyActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.Models.Category;
import com.example.declan.qwiktings.R;
import com.example.declan.qwiktings.Services.ListenOrder;
import com.example.declan.qwiktings.StartActivities.Login_Activity;
import com.example.declan.qwiktings.ViewHolder.MenuViewHolder;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView full_name, email;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference Category;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartintent = new Intent(Home.this, Cart.class);
                startActivity(cartintent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get firebase auth instance
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        //set name for user
        View headerview = navigationView.getHeaderView(0);
        full_name = headerview.findViewById(R.id.full_name);
        full_name.setText(user.getDisplayName());

        //get current user
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Home.this, Login_Activity.class));
                    finish();
                }
            }
        };

        //initiate database
        database = FirebaseDatabase.getInstance();
        Category = database.getReference("Category");

        //load menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        loadmenu();
        recycler_menu.setAdapter(adapter);

        //Call Service
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);
    }

    private void loadmenu()
    {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(com.example.declan.qwiktings.Models.Category.class, R.layout.menu_items, MenuViewHolder.class, Category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.menuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get categoryid & send to new activity
                        Intent qwiklist = new Intent(Home.this, QwikShop_List.class);
                        qwiklist.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(qwiklist);
                    }
                });
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shop)
        {
            Intent qwikshop = new Intent(Home.this, QwikShop_List.class);
            startActivity(qwikshop);
        }

        else if (id == R.id.nav_cart)
        {
            Intent cartintent = new Intent(Home.this, Cart.class);
            startActivity(cartintent);
        }

        else if (id == R.id.nav_orders)
        {
            Intent orderintent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderintent);
        }

        else if (id == R.id.nav_logout) { auth.signOut(); }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(Home.this, Login_Activity.class));
                finish();
            } else {
                email.setText("User Email: " + user.getEmail());

            }
        }


    };

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
