package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.rtchagas.pingplacepicker.PingPlacePicker;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.sliderAdapter.AutoScrollPagerAdapter;
import mobile.android.trip.planner.app.util.AutoScrollViewPager;


public class GetStartedActivity extends AppCompatActivity {
    Button btnLogin,btnGetStarted;
    TextView txtRegister;
    private  int REQUEST_PLACE_PICKER=10001;
    private static final int AUTO_SCROLL_THRESHOLD_IN_MILLI = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        btnLogin=findViewById(R.id.btnLogin);
        btnGetStarted=findViewById(R.id.btnGetStarted);
        txtRegister=findViewById(R.id.txtRegister);
        clickListener();
        changeStatusBarColor();
        setAutoScroll();
    }
    private void setAutoScroll()
    {
        AutoScrollPagerAdapter autoScrollPagerAdapter =
                new AutoScrollPagerAdapter(getSupportFragmentManager());
        AutoScrollViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(autoScrollPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // start auto scroll
        viewPager.startAutoScroll();
        // set auto scroll time in mili
        viewPager.setInterval(AUTO_SCROLL_THRESHOLD_IN_MILLI);
        // enable recycling using true
        viewPager.setCycle(true);
    }
    private void clickListener()
    {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GetStartedActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GetStartedActivity.this,RegisterActivity.class);
                intent.putExtra("title","Register");
                startActivity(intent);
            }
        });
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
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
        }
        catch (Exception ex) {
            // Google Play services is not available...
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == RESULT_OK)) {
            Place place = PingPlacePicker.getPlace(data);
            if (place.getLatLng() != null) {
                Intent intent = new Intent(GetStartedActivity.this, MapsActivity.class);
                intent.putExtra("POST_LATITUDE",String.valueOf (place.getLatLng().latitude));
                intent.putExtra("POST_LONGITUDE",String.valueOf(place.getLatLng().longitude));
                startActivity(intent);
                //Toast.makeText(this, "You selected the place: " + place.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
