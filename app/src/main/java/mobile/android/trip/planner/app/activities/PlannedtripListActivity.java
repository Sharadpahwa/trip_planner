package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.adapter.PlannedPlacesListAdapter;
import mobile.android.trip.planner.app.adapter.RecyclerItemTouchHelperPlannedList;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlacesClickListener;

public class PlannedtripListActivity extends AppCompatActivity implements OnPlacesClickListener, RecyclerItemTouchHelperPlannedList.RecyclerItemTouchHelperListener {
    TextInputEditText edtLocation;
    PlacesClient placesClient;

    PlannedPlacesListAdapter plannedPlacesListAdapter;
    private RecyclerView searchResultLV;
    private DatabaseReference databaseReference;
    private String location, childKey, startDate, endDate, Title;
    ArrayList<Result> destinationList = new ArrayList<Result>();
    ArrayList<Result> wishList = new ArrayList<Result>();
    MKLoader mkloader;
    ArrayList idList = new ArrayList<String>();
    FloatingActionButton addmanually;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planned_list_lay);
        mkloader = findViewById(R.id.mkloader);
        mkloader.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            childKey = getIntent().getStringExtra("key");
            Title = getIntent().getStringExtra("Title");
            startDate = getIntent().getStringExtra("startDate");
            endDate = getIntent().getStringExtra("endDate");


        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.wishListAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlannedtripListActivity.this, WishListActivity.class);
                intent.putExtra("key", childKey);
                intent.putExtra("isGone", false);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                intent.putExtra("tripTitle", Title);
                if (idList.size() > 0) {
                    intent.putExtra("countShow", idList.size());
                    intent.putExtra("count", idList.get(idList.size() - 1).toString());
                } else {
                    intent.putExtra("countShow", count);
                    intent.putExtra("count", String.valueOf(count));
                }
                startActivity(intent);
            }
        });
        findViewById(R.id.addFloating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(PlannedtripListActivity.this, SearchLocationActivity.class);
                intent.putExtra("key", childKey);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                intent.putExtra("tripTitle", Title);
                if (idList.size() > 0) {
                    intent.putExtra("countShow", idList.size());
                    intent.putExtra("count", idList.get(idList.size() - 1).toString());
                } else {
                    intent.putExtra("countShow", count);
                    intent.putExtra("count", String.valueOf(count));
                }
                startActivity(intent);
            }
        });


        mkloader.setVisibility(View.VISIBLE);
        searchResultLV = findViewById(R.id.searchResultLV);
        addmanually = findViewById(R.id.addFloating);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchResultLV.setLayoutManager(layoutManager1);

        placesClient = com.google.android.libraries.places.api.Places.createClient(PlannedtripListActivity.this);

        if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip") != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationList.clear();
                    idList.clear();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        idList.add(noteDataSnapshot.getKey());
                        Result tripBean = noteDataSnapshot.getValue(Result.class);
                        destinationList.add(tripBean);

                    }
                    if (destinationList.size() > 0) {
                        mkloader.setVisibility(View.GONE);
                        LinkedHashSet<Result> hashSet = new LinkedHashSet<>(destinationList);
                        ArrayList<Result> listWithoutDuplicates = new ArrayList<>(hashSet);
                        plannedPlacesListAdapter = new PlannedPlacesListAdapter(PlannedtripListActivity.this, listWithoutDuplicates, PlannedtripListActivity.this);
                        searchResultLV.setItemAnimator(new DefaultItemAnimator());
                        searchResultLV.addItemDecoration(new DividerItemDecoration(PlannedtripListActivity.this, DividerItemDecoration.VERTICAL));
                        searchResultLV.setAdapter(plannedPlacesListAdapter);
                        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperPlannedList(0, ItemTouchHelper.LEFT, PlannedtripListActivity.this);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(searchResultLV);


                    } else {
                        mkloader.setVisibility(View.GONE);
                        Toast.makeText(PlannedtripListActivity.this, "No trip planned yet", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mkloader.setVisibility(View.GONE);
                    Toast.makeText(PlannedtripListActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

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

                    Toast.makeText(PlannedtripListActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }


    @Override
    public void goToClickListener(Result result) {
        Intent intent = new Intent(PlannedtripListActivity.this, MapsActivity.class);
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
                    Toast.makeText(PlannedtripListActivity.this, "Location already added", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            wishList.add(result);
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey);
            databaseReference.child("wish_list").push().setValue(result);
            Toast.makeText(PlannedtripListActivity.this, "Successfully added to wish list", Toast.LENGTH_SHORT).show();
        }
        else {
            wishList.add(result);
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey);
            databaseReference.child("wish_list").push().setValue(result);
            Toast.makeText(PlannedtripListActivity.this, "Successfully added to wish list", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addTodestination(Result result, int i) {


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PlannedPlacesListAdapter.MyClass) {
            // get the removed item name to display it in snack bar


            // backup of removed item for undo purpose
            final Result deletedItem = (Result) destinationList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            plannedPlacesListAdapter.removeItem(viewHolder.getAdapterPosition());
            databaseReference.child(idList.get(deletedIndex).toString()).removeValue();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip") != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationList.clear();
                    idList.clear();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        idList.add(noteDataSnapshot.getKey());
                        Result tripBean = noteDataSnapshot.getValue(Result.class);
                        destinationList.add(tripBean);

                    }
                    if (destinationList.size() > 0) {
                        LinkedHashSet<Result> hashSet = new LinkedHashSet<>(destinationList);
                        ArrayList<Result> listWithoutDuplicates = new ArrayList<>(hashSet);
                        mkloader.setVisibility(View.GONE);
                        plannedPlacesListAdapter = new PlannedPlacesListAdapter(PlannedtripListActivity.this, listWithoutDuplicates, PlannedtripListActivity.this);
                        searchResultLV.setItemAnimator(new DefaultItemAnimator());
                        searchResultLV.addItemDecoration(new DividerItemDecoration(PlannedtripListActivity.this, DividerItemDecoration.VERTICAL));
                        searchResultLV.setAdapter(plannedPlacesListAdapter);


                    } else {
                        mkloader.setVisibility(View.GONE);
                        Toast.makeText(PlannedtripListActivity.this, "No trip planned yet", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mkloader.setVisibility(View.GONE);
                    Toast.makeText(PlannedtripListActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}
