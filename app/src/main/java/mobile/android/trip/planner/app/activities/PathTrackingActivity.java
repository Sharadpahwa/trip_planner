package mobile.android.trip.planner.app.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import mobile.android.trip.planner.app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathTrackingActivity extends AppCompatActivity implements PermissionsListener, LocationEngineListener, OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    FloatingActionButton btnNavigate;

    //1
    int REQUEST_CHECK_SETTINGS = 1;
    SettingsClient settingsClient = null;

    //2

    PermissionsManager permissionManager;
    Location originLocation;

    LocationEngine locationEngine;
    LocationComponent locationComponent;

    NavigationMapRoute navigationMapRoute;
    DirectionsRoute currentRoute;
    //Double currentLat = 0.0;
    Double currentLat = 30.7055;
   // Double currentLng = 0.0;
    Double currentLng =  76.8013;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getResources().getString(R.string.mapbox_key));
        setContentView(R.layout.activity_path_tracking);
        mapView = findViewById(R.id.mapbox);
        btnNavigate = findViewById(R.id.btnNavigate);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        settingsClient = LocationServices.getSettingsClient(this);

        btnNavigate.setEnabled(false);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationLauncherOptions navigationLauncherOptions = NavigationLauncherOptions.builder() //1
                        .directionsRoute(currentRoute) //2
                        .shouldSimulateRoute(true) //3
                        .build();

                NavigationLauncher.startNavigation(PathTrackingActivity.this, navigationLauncherOptions);//4
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                enableLocation();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }

    //1
    void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationComponent();
            initializeLocationEngine();
            mapboxMap.addOnMapClickListener(this);
        } else {
            permissionManager = new PermissionsManager(PathTrackingActivity.this);
            permissionManager.requestLocationPermissions(this);
        }
    }

    //2
    @SuppressWarnings("MissingPermission")
    void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        locationEngine.addLocationEngineListener(this);

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    void initializeLocationComponent() {
        if(mapboxMap.getLocationComponent()!=null) {
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
        }
    }

    //3
    void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),30));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PermissionsManager.areLocationPermissionsGranted(PathTrackingActivity.this)) {
            if(locationEngine!=null)
            locationEngine.requestLocationUpdates();
            if(locationComponent!=null)
            locationComponent.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationEngine.removeLocationUpdates();
        locationComponent.onStop();

        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationEngine.deactivate();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        originLocation = location;
        setCameraPosition(location);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permission to be able to show your location on the map", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        } else {
            Toast.makeText(this, "User location was not granted", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (!mapboxMap.getMarkers().isEmpty()) {
            mapboxMap.clear();
        }
        if(currentLng!=0.0 && currentLng!= 0.0) {
            LatLng point =new LatLng(currentLat, currentLng);
            mapboxMap.addMarker(new MarkerOptions().setTitle("I'm a marker :]").setSnippet("This is a snippet about this marker that will show up here").position(point));
        }
        checkLocation();
        if(originLocation!=null)
         {
            Point startPoint = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
             Point endPoint = Point.fromLngLat(latLng.getLongitude(),latLng.getLatitude());
            getRoute(startPoint, endPoint);
        }
    }




    @SuppressLint("MissingPermission")
    private void checkLocation() {
        if (originLocation == null) {
            if (mapboxMap.getLocationComponent().getLastKnownLocation() != null)
                originLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        if (mapboxMap != null) {
            this.mapboxMap = mapboxMap;

            LocationSettingsRequest.Builder locationRequestBuilder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
            //3
            LocationSettingsRequest locationRequest = locationRequestBuilder.build();
            settingsClient.checkLocationSettings(locationRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    enableLocation();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }
    }

    private void getRoute(Point originPoint, Point endPoint) {
        NavigationRoute.builder(this) //1
                .accessToken(Mapbox.getAccessToken()) //2
                .origin(originPoint) //3
                .destination(endPoint) //4
                .build() //5
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (navigationMapRoute != null) {
                            navigationMapRoute.updateRouteVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null,mapView , mapboxMap);
                        }

                        currentRoute = response.body().routes().get(0);
                        if (currentRoute != null) {
                            navigationMapRoute.addRoute(currentRoute);
                        }

                        btnNavigate.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }
}
