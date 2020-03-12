package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.adapter.AttractionPlacesAdapter;
import mobile.android.trip.planner.app.adapter.AutoCompleteAdapter;
import mobile.android.trip.planner.app.beans.AddressData;
import mobile.android.trip.planner.app.beans.placesbeans.AttractionPlacesResultBean;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlacesClickListener;
import mobile.android.trip.planner.app.util.MyApplication;
import mobile.android.trip.planner.app.util.VolleyJSONRequest;

public class SearchLocationActivity extends AppCompatActivity implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String>, OnPlacesClickListener {
    TextInputEditText edtLocation;
    private Handler handler;
    private VolleyJSONRequest request;
    PlacesClient placesClient;
    private String placeId;
    private String GETPLACESHIT = "places_hit";
    private ArrayList<AddressData> addressDataArrayList;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private AttractionPlacesAdapter attractionPlacesAdapter;
    private AttractionPlacesResultBean attractionPlacesResultBean;
    private RecyclerView searchResultLV;
    private DatabaseReference databaseReference;
    private String location, childKey;
    private String startDate, endDate, Title;
    int count, countShow;
    TextView counttv;
    private ArrayList<Result> destinalionList = new ArrayList<Result>();
    private ArrayList<Result> wishList = new ArrayList<Result>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        if (getIntent() != null) {
            childKey = getIntent().getStringExtra("key");
            Title = getIntent().getStringExtra("tripTitle");
            startDate = getIntent().getStringExtra("startDate");
            endDate = getIntent().getStringExtra("endDate");
            count = Integer.valueOf(getIntent().getStringExtra("count"));
            countShow = getIntent().getIntExtra("countShow", 0);

        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey);

        if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip") != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinalionList.clear();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        Result tripBean = noteDataSnapshot.getValue(Result.class);
                        destinalionList.add(tripBean);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(SearchLocationActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                }
            });

        }
        if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/wish_list") != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/wish_list");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {

                        Log.e("Wish Trip", String.valueOf(noteDataSnapshot.getValue()));
                        Result result = noteDataSnapshot.getValue(Result.class);
                        wishList.add(result);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(SearchLocationActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                }
            });
        }
        edtLocation = findViewById(R.id.edtLocation);
        searchResultLV = findViewById(R.id.searchResultLV);
        counttv = findViewById(R.id.count);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchResultLV.setLayoutManager(layoutManager1);

        placesClient = com.google.android.libraries.places.api.Places.createClient(SearchLocationActivity.this);
        checkEditTextChange();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        Log.d("PLACES RESULT:::", response);
        Gson gson = new Gson();
        attractionPlacesResultBean = gson.fromJson(response, AttractionPlacesResultBean.class);
        if (attractionPlacesResultBean != null) {
            if (mAutoCompleteAdapter == null) {
                attractionPlacesAdapter = new AttractionPlacesAdapter(SearchLocationActivity.this, attractionPlacesResultBean.getResults(), SearchLocationActivity.this);
                searchResultLV.setAdapter(attractionPlacesAdapter);
            } else {
                attractionPlacesAdapter.notifyDataSetChanged();
                searchResultLV.invalidate();
            }
        } else {
            Toast.makeText(SearchLocationActivity.this, "No places find !! Please search again", Toast.LENGTH_SHORT).show();
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
                        request = new VolleyJSONRequest(Request.Method.GET, getPlaceAutoCompleteUrl(address), null, null, SearchLocationActivity.this, SearchLocationActivity.this);

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

    public String getPlaceAutoCompleteUrl(String input) {

        // https://maps.googleapis.com/maps/api/place/textsearch/json?query=punjab+city+point+of+interest&language=en&key=AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/textsearch/json");
        urlString.append("?query=");
        try {
            urlString.append(URLEncoder.encode(input + "+city+point+of+interest", "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&key=" + "AIzaSyDcPuQFfTUeIRyDr1SeQKz-OUwQCaRGOxM");
        Log.e("place_autocomplete_url", urlString.toString());
        return urlString.toString();
    }

    @Override
    public void goToClickListener(Result result) {
        Intent intent = new Intent(SearchLocationActivity.this, MapsActivity.class);
        intent.putExtra("POST_LATITUDE", result.getGeometry().getLocation().getLat().toString());
        intent.putExtra("POST_LONGITUDE", result.getGeometry().getLocation().getLng().toString());
        startActivity(intent);
    }

    @Override
    public void addToList(Result result) {
        AddWishList(result);

    }

    private void AddWishList(Result result) {
        if (wishList.size() > 0) {
            for (int j = 0; j < wishList.size(); j++) {
                Result result1 = wishList.get(j);
                if (result1.getName().equals(result.getName())) {
                    Toast.makeText(SearchLocationActivity.this, "Location already added", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            wishList.add(result);
            databaseReference.child("wish_list").push().setValue(result);
            Toast.makeText(SearchLocationActivity.this, "Successfully added to wish list", Toast.LENGTH_SHORT).show();
        } else {
            wishList.add(result);
            databaseReference.child("wish_list").push().setValue(result);
            Toast.makeText(SearchLocationActivity.this, "Successfully added to wish list", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addTodestination(Result result, int pos) {
        if (destinalionList.size() > 0) {
            for (int i = 0; i < destinalionList.size(); i++) {
                Result result1 = destinalionList.get(i);
                if (result1.getName().equals(result.getName())) {
                    Toast.makeText(SearchLocationActivity.this, "Destination already added", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            destinalionList.add(result);
            count++;
            countShow++;
            counttv.setText(String.valueOf(countShow));
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
            databaseReference.child(String.valueOf(count)).setValue(result);
            Toast.makeText(SearchLocationActivity.this, "Add to destination successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            destinalionList.add(result);
            count++;
            countShow++;
            counttv.setText(String.valueOf(countShow));
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
            databaseReference.child(String.valueOf(count)).setValue(result);
            Toast.makeText(SearchLocationActivity.this, "Add to destination successfully", Toast.LENGTH_SHORT).show();
        }

    }


}
