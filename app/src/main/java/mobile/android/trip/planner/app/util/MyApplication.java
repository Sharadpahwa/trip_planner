package mobile.android.trip.planner.app.util;

import android.app.Application;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import mobile.android.trip.planner.app.R;

public class MyApplication extends Application {
    public static VolleySingleton volleyQueueInstance;
    private static MyApplication mInstance;

    public static final String TAG = MyApplication.class.getSimpleName();
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // Initialize the SDK


// Create a new Places client instance

        //Places.initialize(getApplicationContext(), "AIzaSyB2W44JSmsMvL175yDHJpKaMF6iHFJS48Y");
        Places.initialize(getApplicationContext(), "AIzaSyCAZ8yVNydainVOD0fhYYd0Hd67s4xxx_s");
        PlacesClient placesClient = Places.createClient(this);
        instantiateVolleyQueue();
    }
    public void instantiateVolleyQueue() {
        volleyQueueInstance = VolleySingleton.getInstance(getApplicationContext());
    }
}
