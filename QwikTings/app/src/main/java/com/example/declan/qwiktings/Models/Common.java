/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.Models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.declan.qwiktings.Remote.IGeoCordinates;
import com.example.declan.qwiktings.Remote.RetrofitClient;

public class Common
{
    public static Requests current_requests;
    public static final String UPDATE = "Update", DELETE = "Delete";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String baseURL = "https://maps.googleapis.com";

    public static String convertCodeToStatus (String code)
    {
        if(code.equals("0"))
            return "Placed.";
        else if(code.equals("1"))
            return "On my Way!";
        else
            return "Shipped.";
    }

    public static IGeoCordinates getGeoService()
    {
        return RetrofitClient.getClient(baseURL).create(IGeoCordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scalematrix = new Matrix();
        scalematrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scalematrix);
        canvas.drawBitmap(bitmap,0,0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
}
