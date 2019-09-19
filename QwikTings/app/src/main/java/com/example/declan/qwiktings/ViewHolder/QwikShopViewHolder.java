/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.declan.qwiktings.Interface.ItemClickListener;
import com.example.declan.qwiktings.R;

public class QwikShopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView itemname;
    public ImageView itemimage;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public QwikShopViewHolder(@NonNull View itemView) {
        super(itemView);

        itemname = itemView.findViewById(R.id.item_name);
        itemimage = itemView.findViewById(R.id.item_image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

}
