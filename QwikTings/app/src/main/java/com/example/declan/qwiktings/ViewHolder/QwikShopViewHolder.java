package com.example.declan.qwiktings.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
