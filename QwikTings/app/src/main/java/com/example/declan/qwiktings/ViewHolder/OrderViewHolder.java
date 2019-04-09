package com.example.declan.qwiktings.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
{
    private TextView order_id,order_status, order_email, order_address;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView)
    {
        super(itemView);
        order_id = itemView.findViewById(R.id.order_id);
        order_email = itemView.findViewById(R.id.order_email);
        order_status = itemView.findViewById(R.id.order_status);
        order_address = itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public boolean onLongClick(View v)
    {
        itemClickListener.onClick(v, getAdapterPosition(), true);
        return true;
    }
}
