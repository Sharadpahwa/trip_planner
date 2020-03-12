package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.pref.PreferenceManager;

public class MainActivity extends AppCompatActivity {
RelativeLayout locationSearch,bookFlight,findRestaurants,findPlannerTrip,findWishList;
ImageView logout;
private PreferenceManager prefManager;
String childKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }*/

        changeStatusBarColor();
        prefManager = new PreferenceManager(this);
        childKey=prefManager.getUserKey();
        locationSearch=findViewById(R.id.locationSearch);
       // logout=findViewById(R.id.logout);
        findRestaurants=findViewById(R.id.findRestaurants);
        findPlannerTrip=findViewById(R.id.findPlannerTrip);
        findWishList=findViewById(R.id.findWishList);
        bookFlight=findViewById(R.id.bookFlight);
        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlanTripActivity.class);
                intent.putExtra("key",childKey);
                startActivity(intent);
            }
        });
        findRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NearByRestaurantsActivity.class);
                startActivity(intent);
            }
        });
        findPlannerTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlannedTripActivity.class);
                intent.putExtra("key",childKey);
                startActivity(intent);
            }
        });
        findWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WishListActivity.class);
                intent.putExtra("key",childKey);
                intent.putExtra("isGone",true);
                startActivity(intent);
            }
        });
        bookFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://www.makemytrip.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
       /* logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              finish();
              prefManager.setFirstTimeLaunch(true);
              prefManager.setUserKey(null);


            }
        });*/
    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item1:
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                intent.putExtra("title","Edit Profile");
                intent.putExtra("key",childKey);
                startActivity(intent);
                return true;
            case R.id.item2:
                finish();
                prefManager.setFirstTimeLaunch(true);
                prefManager.setUserKey(null);
                Toast.makeText(getApplicationContext(),"Logout successfully",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
