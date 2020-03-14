package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.model.TripBean;


public class PlanTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private DatabaseReference databaseReference;
    TextInputEditText edtTriptitle, edtLocation, edtStartDate, edtEndDate;
    Button btnRegister;
    private int REQUEST_PLACE_PICKER = 10001;
    double latitude = 0, longitude = 0;
    private String location, childKey;
    private boolean isStartDate = false;
    private boolean isEndDate = false;
    ArrayList<TripBean> plannedList = new ArrayList<>();

    // Initialize the AutocompleteSupportFragment.
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null)
            childKey = getIntent().getStringExtra("key");
        setContentView(R.layout.activity_plan_trip);
        edtTriptitle = findViewById(R.id.edtUsername);
        edtLocation = findViewById(R.id.edtLocation);
        edtStartDate = findViewById(R.id.edtPass);
        edtEndDate = findViewById(R.id.edtConfPass);
        btnRegister = findViewById(R.id.btnRegister);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        //todo : change the color
        autocompleteFragment.getView().setBackgroundColor(Color.parseColor("#00000000"));
        autocompleteFragment.setHint("Destination");
        autocompleteFragment.getContext().getTheme().applyStyle(R.style.PreferenceScreen, true);

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                edtLocation.setText(place.getName());
                Log.i("Data", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Data", "An error occurred: " + status);
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey);
        if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip") != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {


                        TripBean tripBean = new TripBean();
                        tripBean.setTripTitle(noteDataSnapshot.child("tripTitle").getValue().toString());
                        tripBean.setEndDate(noteDataSnapshot.child("endDate").getValue().toString());
                        tripBean.setStartDate(noteDataSnapshot.child("startDate").getValue().toString());
                        tripBean.setTripDestination(noteDataSnapshot.child("tripDestination").getValue().toString());
                        //TripBean tripBean = noteDataSnapshot.getValue(TripBean.class);
                        plannedList.add(tripBean);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(PlanTripActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                }
            });

        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(PlanTripActivity.this, SearchLocationActivity.class);
                intent.putExtra("key", childKey);
                startActivityForResult(intent, 101);*/
            }
        });
        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDate = true;
                openCalender();
            }
        });
        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEndDate = true;
                openCalender();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidate()) {
                    String desLocation = edtLocation.getText().toString().trim();

                    String startDate = edtStartDate.getText().toString().trim();
                    String endDate = edtEndDate.getText().toString().trim();

                    String tripTitle = edtTriptitle.getText().toString().trim();
                    if (plannedList.size() > 0) {
                        for (int i = 0; i < plannedList.size(); i++) {
                            if (plannedList.get(i).getTripTitle().equals(tripTitle)) {
                                Toast.makeText(PlanTripActivity.this, "This trip is already added", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }


                    Intent intent = new Intent(PlanTripActivity.this, DestinatioLocationActivity.class);
                    intent.putExtra("key", childKey);
                    intent.putExtra("desLocation", desLocation);
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    intent.putExtra("tripTitle", tripTitle);
                    startActivity(intent);
                    finish();
                   /* TripBean tripBean = new TripBean(tripTitle, desLocation, startDate, endDate);
                    AddTripDetail(tripBean);
                    Toast.makeText(PlanTripActivity.this, "Trip plan successfully", Toast.LENGTH_SHORT).show();
                    finish();*/


                }

            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void AddTripDetail(TripBean tripBean) {
        databaseReference.child("planned_trip").push().setValue(tripBean);
    }

    private boolean isValidate() {
        if (edtTriptitle.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter trip tittle", Toast.LENGTH_SHORT).show();
            edtTriptitle.requestFocus();
            return false;
        } else if (edtStartDate.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter start date", Toast.LENGTH_SHORT).show();
            edtStartDate.requestFocus();
            return false;

        } else if (edtEndDate.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter end date", Toast.LENGTH_SHORT).show();
            edtEndDate.requestFocus();
            return false;

        } else if (edtLocation.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter destination location", Toast.LENGTH_SHORT).show();
            edtLocation.requestFocus();
            return false;

        } else if (!isDateValidDate(edtStartDate.getText().toString().trim(), edtEndDate.getText().toString().trim())) {
            Toast.makeText(this, "End date should be greater than start date", Toast.LENGTH_SHORT).show();
            edtLocation.requestFocus();
            return false;
        }
        return true;
    }

    private void showPlacePicker() {
        PingPlacePicker.IntentBuilder builder = new PingPlacePicker.IntentBuilder();
        builder.setAndroidApiKey(getString(R.string.google_api_key))
                .setMapsApiKey(getString(R.string.key_google_apis_android));

        // If you want to set a initial location rather then the current device location.
        // NOTE: enable_nearby_search MUST be true.
        // builder.setLatLng(new LatLng(37.4219999, -122.0862462))

        try {
            Intent placeIntent = builder.build(this);
            startActivityForResult(placeIntent, REQUEST_PLACE_PICKER);
        } catch (Exception ex) {
            // Google Play services is not available...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);

            if (place != null) {
                if (place.getLatLng() != null) {
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                }
                location = place.getName();
                edtLocation.setText(location);
                //Toast.makeText(this, "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 101) {
            if (data != null) {
                // Result desdata= (Result) data.getSerializableExtra("PlaceDes");
                edtLocation.setText(data.getStringExtra("PlaceDes"));
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        if (isStartDate) {
            edtStartDate.setText(date);
            isStartDate = false;
        }
        if (isEndDate) {
            isEndDate = false;
            edtEndDate.setText(date);
        }
    }

    private void openCalender() {


        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PlanTripActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
// If you're calling this from a support Fragment
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);


    }

    public boolean isDateValidDate(String startDate, String endDate) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
        try {
            if (startDate != null && endDate != null) {
                Date startdate = simpleDateFormat.parse(startDate);
                Date enddate = simpleDateFormat.parse(endDate);
                if (enddate.after(startdate) || enddate.equals(startdate)) {
                    return true;
                } else {
                    return false;
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
