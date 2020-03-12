package mobile.android.trip.planner.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.adapter.NearByRestaurantsAdapter;
import mobile.android.trip.planner.app.beans.placesbeans.AttractionPlacesResultBean;
import mobile.android.trip.planner.app.util.GetLocation;
import mobile.android.trip.planner.app.util.MyApplication;
import mobile.android.trip.planner.app.util.VolleyJSONRequest;

public class NearByRestaurantsActivity extends AppCompatActivity implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String> {
    TextInputEditText edtLocation;
    private Handler handler;
    private VolleyJSONRequest request;
    PlacesClient placesClient;
    private String GETPLACESHIT = "places_hit";
    private NearByRestaurantsAdapter attractionPlacesAdapter;
    private AttractionPlacesResultBean attractionPlacesResultBean;
    private RecyclerView searchResultLV;
    private GetLocation getLocation;
    double currentLat, curentLng;
    MKLoader mkLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_restaurants);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getLocation = new GetLocation(NearByRestaurantsActivity.this);
        getCurrentLocation();
        edtLocation = findViewById(R.id.edtLocation);
        mkLoader = findViewById(R.id.mkloader);
        searchResultLV = findViewById(R.id.searchResultLV);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchResultLV.setLayoutManager(layoutManager1);

        placesClient = com.google.android.libraries.places.api.Places.createClient(NearByRestaurantsActivity.this);
        mkLoader.setVisibility(View.VISIBLE);
        checkEditTextChange(currentLat, curentLng);
        checkEditTextChange();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        Log.d("PLACES RESULT:::", response);
        mkLoader.setVisibility(View.GONE);
        Gson gson = new Gson();
        attractionPlacesResultBean = gson.fromJson(response, AttractionPlacesResultBean.class);
        if (attractionPlacesResultBean != null) {

            attractionPlacesAdapter = new NearByRestaurantsAdapter(NearByRestaurantsActivity.this, attractionPlacesResultBean.getResults());
            searchResultLV.setAdapter(attractionPlacesAdapter);
        }


    }

    private void checkEditTextChange(double lat, double lng) {

        Runnable run = new Runnable() {


            @Override
            public void run() {
                // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                MyApplication.volleyQueueInstance.cancelRequestInQueue(GETPLACESHIT);

                //build Get url of Place Autocomplete and hit the url to fetch result.
                request = new VolleyJSONRequest(Request.Method.GET, getPlaceAutoCompleteUrl(lat, lng), null, null, NearByRestaurantsActivity.this, NearByRestaurantsActivity.this);

                //Give a tag to your request so that you can use this tag to cancle request later.
                request.setTag(GETPLACESHIT);

                MyApplication.volleyQueueInstance.addToRequestQueue(request);

            }

        };

        // only canceling the network calls will not help, you need to remove all callbacks as well
        // otherwise the pending callbacks and messages will again invoke the handler and will send the request
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        } else {
            handler = new Handler();
        }
        handler.postDelayed(run, 1000);
    }

    public String getPlaceAutoCompleteUrl(double lat, double lng) {

        //https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants&location=42.3675294,-71.186966&radius=10000&key=AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/textsearch/json");
        urlString.append("?query=restaurants&location=");
        try {
            urlString.append(URLEncoder.encode(lat + "," + lng, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&radius=" + "10000");
        urlString.append("&key=" + "AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM");
        Log.e("place_autocomplete_url", urlString.toString());
        return urlString.toString();
    }

    public LatLng getCurrentLocation() {
        if (getLocation.enabledLocation()) {
            getLocation.getUserLocation();
            currentLat = getLocation.getUserLatitude();
            curentLng = getLocation.getUserLongitude();

            return new LatLng(currentLat, curentLng);
        } else {
            getLocation.showSettingsAlert(NearByRestaurantsActivity.this);
            return null;
        }
    }


    private void checkEditTextChange() {
        edtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                Log.e("llBelowLayout", "onTextChange    Gone");
                final String address = edtLocation.getText().toString();//+ "Montreal, QC, Canada"
                Runnable run = new Runnable() {


                    @Override
                    public void run() {
                        // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                        MyApplication.volleyQueueInstance.cancelRequestInQueue(GETPLACESHIT);

                        //build Get url of Place Autocomplete and hit the url to fetch result.
                        request = new VolleyJSONRequest(Request.Method.GET, getPlaceAutoCompleteUrlNew(address), null, null, NearByRestaurantsActivity.this, NearByRestaurantsActivity.this);

                        //Give a tag to your request so that you can use this tag to cancle request later.
                        request.setTag(GETPLACESHIT);

                        MyApplication.volleyQueueInstance.addToRequestQueue(request);

                    }

                };

                // only canceling the network calls will not help, you need to remove all callbacks as well
                // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                } else {
                    handler = new Handler();
                }
                handler.postDelayed(run, 1000);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public String getPlaceAutoCompleteUrlNew(String input) {

        // https://maps.googleapis.com/maps/api/place/textsearch/json?query=punjab+city+point+of+interest&language=en&key=AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/textsearch/json");
        urlString.append("?query=");
        try {
            urlString.append(URLEncoder.encode("restaurants+in+" + input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&key=" + "AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM");
        Log.e("newURL", urlString.toString());
        return urlString.toString();
    }
}
