/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.BodyActivities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.declan.qwiktings.Models.Common;
import com.example.declan.qwiktings.Models.DirectionJSONParser;
import com.example.declan.qwiktings.R;
import com.example.declan.qwiktings.Remote.IGeoCordinates;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private Location lastLocation;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private static int UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 5000, DISPLACEMENT = 10;
    private IGeoCordinates mService;
    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        mService = Common.getGeoService();

        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestRuntimePermission();
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleAPIClient();
                createLocationRequest();
            }
        }
        displayLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        { requestRuntimePermission(); }
        else
        {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            if(lastLocation != null)
            {
                double latitude = lastLocation.getLatitude();
                double longitude = lastLocation.getLongitude();

                //Add marker to your location and change camera.
                LatLng yourLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location."));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                //Add Marker for the order and Draw Route.
                drawRoute(yourLocation, Common.current_requests.getAddress());
            }
            else {
                //Toast.makeText(getApplicationContext(), "Couldn't find your location.", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Couldn't find your location."); }
        }
    }

    private void drawRoute(final LatLng yourLocation, String address) {
        mService.getGeoCode(address).enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response.body());

                    /*
                    JSONArray arraylat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("geometry")
                            .getJSONObject(0).getJSONArray("lat");
                    JSONArray arraylng = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("geometry")
                            .getJSONObject(0).getJSONArray("lng");
                    lat = String.valueOf(arraylat);
                    lng = String.valueOf(arraylng);
                    */

                    String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lat").toString();
                    String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.move_box);
                    bitmap = Common.scaleBitmap(bitmap, 70, 70);

                    MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Order of "+Common.current_requests.getEmail()).position(orderLocation);
                    mMap.addMarker(marker);

                    //Draw Route
                    mService.getDirections(yourLocation.latitude + "," + yourLocation.longitude,
                            orderLocation.latitude + "," + orderLocation.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    new ParserTask().execute(response.body());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "There was an error. "+e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleAPIClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    private boolean checkPlayServices() {
        int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultcode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultcode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultcode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Device is not supported.", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleAPIClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { mMap = googleMap; }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) { client.connect(); }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        displayLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(client != null)
            client.connect();
    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String, String>>>> {
        ProgressDialog mDialog = new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please Wait...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(strings [0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions lineOptions = null;

            for(int i=0; i<lists.size(); i++)
            {
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);

                for(int j=0; j<path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);
                    double lat1 = Double.parseDouble(point.get("lat"));
                    double lng1 = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat1, lng1);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }
            mMap.addPolyline(lineOptions);
        }
    }
}
