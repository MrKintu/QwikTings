/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseURL)
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(baseURL).addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
