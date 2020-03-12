package mobile.android.trip.planner.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.adapter.NearByRestaurantsAdapter;
import mobile.android.trip.planner.app.adapter.PlannedTripAdapter;
import mobile.android.trip.planner.app.adapter.RecyclerItemTouchHelper;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlannedtripClickListener;
import mobile.android.trip.planner.app.model.TripBean;

public class PlannedTripActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, OnPlannedtripClickListener {
    private RecyclerView searchResultLV;
    private DatabaseReference databaseReference;
    private String location, childKey;
    private PlannedTripAdapter plannedTripAdapter;
    ArrayList plannedList = new ArrayList<>();
    ArrayList idList = new ArrayList<String>();
    MKLoader mkLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_trip);
        mkLoader=findViewById(R.id.mkloader);
        if (getIntent() != null)
            childKey = getIntent().getStringExtra("key");
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchResultLV = findViewById(R.id.searchResultLV);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchResultLV.setLayoutManager(layoutManager1);
        mkLoader.setVisibility(View.VISIBLE);
        if(FirebaseDatabase.getInstance().getReference("user_data/" + childKey+"/planned_trip")!=null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        idList.add(noteDataSnapshot.getKey());

                        Log.e("Planned Trip size", String.valueOf(idList.size()));
                        TripBean tripBean=new TripBean();
                        tripBean.setTripTitle(noteDataSnapshot.child("tripTitle").getValue().toString());
                        tripBean.setEndDate(noteDataSnapshot.child("endDate").getValue().toString());
                        tripBean.setStartDate(noteDataSnapshot.child("startDate").getValue().toString());
                        tripBean.setTripDestination(noteDataSnapshot.child("tripDestination").getValue().toString());
                        //TripBean tripBean = noteDataSnapshot.getValue(TripBean.class);
                        plannedList.add(tripBean);

                    }
                    if(plannedList.size()>0) {
                        mkLoader.setVisibility(View.GONE);
                        plannedTripAdapter = new PlannedTripAdapter(PlannedTripActivity.this, plannedList,PlannedTripActivity.this::goToClickListener);
                        searchResultLV.setItemAnimator(new DefaultItemAnimator());
                        searchResultLV.addItemDecoration(new DividerItemDecoration(PlannedTripActivity.this, DividerItemDecoration.VERTICAL));
                        searchResultLV.setAdapter(plannedTripAdapter);
                        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, PlannedTripActivity.this);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(searchResultLV);

                    }
                    else {
                        mkLoader.setVisibility(View.GONE);
                        Toast.makeText(PlannedTripActivity.this, "No trip planned yet", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mkLoader.setVisibility(View.GONE);
                    Toast.makeText(PlannedTripActivity.this,"Somethig went wrong",Toast.LENGTH_SHORT).show();

                }
            });

        }


    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PlannedTripAdapter.MyClass) {
            // get the removed item name to display it in snack bar


            // backup of removed item for undo purpose
            final TripBean deletedItem = (TripBean) plannedList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            plannedTripAdapter.removeItem(viewHolder.getAdapterPosition());
            databaseReference.child(idList.get(deletedIndex).toString()).removeValue();





        }
    }

    @Override
    public void goToClickListener(TripBean result) {
        Intent intent = new Intent(PlannedTripActivity.this, PlannedtripListActivity.class);
        intent.putExtra("key",childKey);
        intent.putExtra("Title",result.getTripTitle());
        intent.putExtra("startDate",result.getStartDate());
        intent.putExtra("endDate",result.getEndDate());


        startActivity(intent);
    }
}
