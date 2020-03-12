package mobile.android.trip.planner.app.util;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mobile.android.trip.planner.app.R;


public class GetLocation extends Service implements LocationListener {
    boolean isGpsEnabled = false;
    boolean isNetworkEnabled = false;
    LocationManager locationManager;
    boolean cangetLocation = false;
    Context mContext;
    Location location;
    String addresstext = "";
    double latitude, longitude,accuracy;
    public static final long minimum_distance_for_location_update = 10;
    public static final long minimum_timeinterval_for_location_update = 10000;//1 * 60 * 1000;5000

    public GetLocation() {

    }

    public GetLocation(Context mContext) {
        this.mContext = mContext;
        getUserLocation();
    }

    public Location getUserLocation() {
        if (mContext == null) {
            return null;
        } else {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGpsEnabled && !isNetworkEnabled) {
                //showSettingsAlert();
                //Toast.makeText(mContext, "No provider is enabled", Toast.LENGTH_LONG).show();
            } else {
                cangetLocation = true;

                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimum_timeinterval_for_location_update
                                , minimum_distance_for_location_update, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    } catch (SecurityException se) {
                        Log.e("NETWORK EXP", "" + se.getMessage());
                    }
                }

                if (isGpsEnabled) {
                    try {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minimum_timeinterval_for_location_update
                                    , minimum_distance_for_location_update, this);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }

                    } catch (SecurityException se) {
                        Log.e("GPS EXP", "" + se.getMessage());
                    }
                }
            }
            return location;
        }
    }

    public String getAddressFromLocation(double lat, double lng) {
        List<Address> addressList = new ArrayList<>();

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(lat, lng, 3);
        } catch (IOException e) {
        }
        if (addressList.size() > 0) {
            Address address = addressList.get(0);
            addresstext = address.getAddressLine(2);
            addresstext = address.getAddressLine(0) + "," + address.getAddressLine(1) + "," + address.getAddressLine(2);
        }

        return addresstext;
    }

    public Address getAddressLocation(double lat, double lng) {
        Address address = null;

        List<Address> addressList = new ArrayList<>();

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(lat, lng, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size() > 0) {
            address = addressList.get(0);
        }

        return address;
    }

    public boolean enabledLocation() {
        return cangetLocation;
    }

    public double getUserLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getUserLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getAccuracy() {
        if (location != null) {
            accuracy = location.getAccuracy();
        }
        return accuracy;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ON LOCATION", "" + location + ", " + location.getLatitude() + ", " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void showSettingsAlert(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_gps_network, null, false);
        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);


        TextView tv_titleText = (TextView) dialog.findViewById(R.id.tv_titleText);
        tv_titleText.setText("GPS SETTINGS");

        TextView tv_detailMessage = (TextView) dialog.findViewById(R.id.tv_detailMessage);
        tv_detailMessage.setText("GPS is not enabled. Do you want to go to settings menu?");

        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        TextView tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
        tv_ok.setText("SETTINGS");

        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        if (!(dialog.isShowing())) {
            dialog.show();
        }
    }
}

