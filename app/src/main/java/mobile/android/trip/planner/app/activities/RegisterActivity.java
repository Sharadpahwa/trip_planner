package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.rtchagas.pingplacepicker.PingPlacePicker;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.model.UserBean;


public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    TextInputEditText edtUsername, edtName, edtEmail, edtPhone, edtLocation, edtCountry, edtState, edtPass, edtConfPass;
    Button btnRegister;
    private int REQUEST_PLACE_PICKER = 10001;
    double latitude = 0, longitude = 0;
    private String location, title, childKey;
    MKLoader mkLoader;
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";


    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null)
            title = getIntent().getStringExtra("title");
        setContentView(R.layout.activity_register);
        edtUsername = findViewById(R.id.edtUsername);
        mkLoader = findViewById(R.id.mkloader);
        edtName = findViewById(R.id.edtName);
        titleTv = findViewById(R.id.titleTv);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtLocation = findViewById(R.id.edtLocation);
        //edtCountry = findViewById(R.id.edtCountry);
        //edtState = findViewById(R.id.edtState);
        edtPass = findViewById(R.id.edtPass);
        edtConfPass = findViewById(R.id.edtConfPass);
        btnRegister = findViewById(R.id.btnRegister);
        titleTv.setText(title);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
            }
        });
        if (title.equalsIgnoreCase("Edit Profile")) {
            btnRegister.setText("Save");
            if (getIntent() != null)
                childKey = getIntent().getStringExtra("key");
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    edtUsername.setText(dataSnapshot.child("username").getValue().toString());
                    edtName.setText(dataSnapshot.child("name").getValue().toString());
                    edtEmail.setText(dataSnapshot.child("emailId").getValue().toString());
                    edtPass.setText(dataSnapshot.child("password").getValue().toString());
                    edtConfPass.setText(dataSnapshot.child("password").getValue().toString());
                    edtLocation.setText(dataSnapshot.child("location").getValue().toString());
                    edtPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    latitude=Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
                    longitude=Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            btnRegister.setText("Register");
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data");
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidate()) {
                    mkLoader.setVisibility(View.VISIBLE);
                    final boolean[] isTrue = {false};
                    final boolean[] isTrueEmial = {false};
                    // String country = edtCountry.getText().toString().trim();
                    String emailId = edtEmail.getText().toString().trim();
                    String location = edtLocation.getText().toString().trim();
                    String name = edtName.getText().toString().trim();
                    String password = edtPass.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    //String state = edtState.getText().toString().trim();
                    String username = edtUsername.getText().toString().trim();
                    if (title.equalsIgnoreCase("Register")) {
                       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot childInner : dataSnapshot.getChildren()) {

                                    if (childInner.child("username").getValue().equals(username)) {
                                        isTrue[0] = true;

                                    }
                                     if (childInner.child("emailId").getValue().equals(emailId)) {
                                         isTrueEmial[0] = true;


                                    }



                                }
                                if(isTrue[0] && isTrueEmial[0])
                                {
                                    mkLoader.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Username and email already exist", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if (isTrue[0]) {
                                        mkLoader.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Username is already exist", Toast.LENGTH_SHORT).show();
                                    }
                                    if (isTrueEmial[0]) {
                                        mkLoader.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Email is already exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                               if(!isTrue[0] && !isTrueEmial[0]) {
                                    mkLoader.setVisibility(View.INVISIBLE);
                                    if (latitude != 0 && longitude != 0) {
                                        UserBean userBean = new UserBean(latitude, emailId, location, name, password, phone, longitude, username);
                                        registerUser(userBean);
                                        Toast.makeText(RegisterActivity.this, "User register successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        mkLoader.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Please select another location", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                mkLoader.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } else {
                        if (latitude != 0 && longitude != 0) {
                            Map<String, Object> updates = new HashMap<String, Object>();
                            updates.put("emailId", emailId);
                            updates.put("location", location);
                            updates.put("name", name);
                            updates.put("password", password);
                            updates.put("phone", phone);
                            updates.put("username", username);
                            updates.put("latitude", latitude);
                            updates.put("longitude", longitude);
                            databaseReference.updateChildren(updates);
                            mkLoader.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "User profile edited successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mkLoader.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Please select another location", Toast.LENGTH_SHORT).show();
                        }
                    }


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

    private void registerUser(UserBean userBean) {
        databaseReference.child(userBean.getEmailId().replace(".", "_")).setValue(userBean);
    }

    private boolean isValidate() {
        if (edtUsername.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            edtUsername.requestFocus();
            return false;
        } else if (edtName.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            edtName.requestFocus();
            return false;

        } else if (edtEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            edtEmail.requestFocus();
            return false;

        }

        else if (!isValidEmail(edtEmail.getText().toString().trim())) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            edtEmail.requestFocus();
            return false;

        }else if (edtPhone.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show();
            edtPhone.requestFocus();
            return false;

        }

        else if (edtPhone.getText().toString().trim().length()<10) {
            Toast.makeText(this, "Please enter valid phone", Toast.LENGTH_SHORT).show();
            edtPhone.requestFocus();
            return false;

        }else if (edtLocation.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show();
            edtLocation.requestFocus();
            return false;

        } /*else if (edtCountry.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter country", Toast.LENGTH_SHORT).show();
            edtCountry.requestFocus();
            return false;

        } else if (edtState.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter state", Toast.LENGTH_SHORT).show();
            edtState.requestFocus();
            return false;

        }*/ else if (edtPass.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            edtPass.requestFocus();
            return false;

        } else if (edtConfPass.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
            edtConfPass.requestFocus();
            return false;

        } else if (!edtPass.getText().toString().trim().equals(edtConfPass.getText().toString().trim())) {
            Toast.makeText(this, "Password and confirm password should be same", Toast.LENGTH_SHORT).show();
            edtPass.requestFocus();
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
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
