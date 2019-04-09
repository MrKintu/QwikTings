package com.example.declan.qwiktings.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.declan.qwiktings.BodyActivities.Cart;
import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.Models.Order;
import com.example.declan.qwiktings.Models.QwikShop;
import com.example.declan.qwiktings.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView cart_name, price;
    public ImageView cart_count;
    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView, TextView cart_name, TextView price, ImageView cart_count, ItemClickListener itemClickListener) {
        super(itemView);
        this.cart_name = cart_name;
        this.price = price;
        this.cart_count = cart_count;
        this.itemClickListener = itemClickListener;
    }

    public void setCart_name(TextView cart_name) {
        this.cart_name = cart_name;
    }


    public CartViewHolder(View itemView) {
        super(itemView);
        cart_name = itemView.findViewById(R.id.cart_item_name);
        price = itemView.findViewById(R.id.cart_item_price);
        cart_count = itemView.findViewById(R.id.cart_item_count);
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public void setCart_count(ImageView cart_count) {
        this.cart_count = cart_count;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) { itemClickListener.onClick(v, getAdapterPosition(), false); }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>
{
    private List<Order> listdata;
    private Context context;

    public CartAdapter() {
    }

    public CartAdapter(List<Order> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemsView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemsView);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position)
    {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+listdata.get(position).getQuantity(), Color.RED);
        holder.cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int cartprice = (Integer.valueOf(listdata.get(position).getPrice()))*(Integer.valueOf(listdata.get(position).getQuantity()));
        holder.price.setText(fmt.format(cartprice));
        holder.cart_name.setText(listdata.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }
}
