/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCordinates
{
    @GET ("maps/api/geocode/json")
    Call<String> getGeoCode (@Query("address") String address);

    @GET ("maps/api/directions/json")
    Call<String> getDirections (@Query("origin") String origin, @Query("destination") String destination);
}
