/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.declan.qwiktings.Models.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper
{
    private static final String DB_Name = "Order_DB.sqlite";
    private static final int DB_Ver = 4;

    public Database(Context context) {

        super(context, DB_Name, null, DB_Ver);
    }

    public List<Order> getCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlselect ={"ProductID", "ProductName", "Quantity", "Price"};
        String sqltable = "OrderDetails";

        qb.setTables(sqltable);
        Cursor c = qb.query(db, sqlselect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do
                { result.add(new Order(c.getString(c.getColumnIndex("ProductID")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price"))));
                } while (c.moveToNext());
        }

        return result;
    }

    public void addtoCart (Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetails(ProductID, ProductName, Quantity, Price) VALUES('%s', '%s', '%s', '%s');",
                order.getProductID(), order.getProductName(), order.getQuantity(), order.getPrice());
        db.execSQL(query);
    }

    public void clearCart (Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetails");
        db.execSQL(query);
    }
}
