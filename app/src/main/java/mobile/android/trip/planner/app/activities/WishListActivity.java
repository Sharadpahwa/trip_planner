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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.adapter.AttractionPlacesAdapter;
import mobile.android.trip.planner.app.adapter.PlannedTripAdapter;
import mobile.android.trip.planner.app.adapter.RecyclerItemTouchHelper;
import mobile.android.trip.planner.app.adapter.RecyclerItemTouchHelperWish;
import mobile.android.trip.planner.app.adapter.WishListAdapter;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlacesClickListener;
import mobile.android.trip.planner.app.model.TripBean;

public class WishListActivity extends AppCompatActivity implements OnPlacesClickListener, RecyclerItemTouchHelperWish.RecyclerItemTouchHelperListener {
    private RecyclerView searchResultLV;
    private DatabaseReference databaseReference;
    private String location, childKey;
    private WishListAdapter wishListAdapter;
    ArrayList<Result> wishList = new ArrayList<>();
    ArrayList<Result> destinalionList = new ArrayList<>();
    ArrayList idList = new ArrayList<String>();
    MKLoader mkLoader;
    Boolean isGone;
    private String startDate,endDate,Title;
    int count,countShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        mkLoader=findViewById(R.id.mkloader);
        if (getIntent() != null) {
            childKey = getIntent().getStringExtra("key");
            isGone = getIntent().getBooleanExtra("isGone",false);
        }
        if(!isGone)
        {
            Title= getIntent().getStringExtra("tripTitle");
            startDate = getIntent().getStringExtra("startDate");
            endDate = getIntent().getStringExtra("endDate");
            count=Integer.valueOf(getIntent().getStringExtra("count"));
            countShow=getIntent().getIntExtra("countShow",0);
        }
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
        if(!isGone) {

            if (FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip") != null) {
                databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/planned_trip/" + Title.replace(".", "_") + startDate.replace("/", "_") + endDate.replace("/", "_") + "/list");
                if (databaseReference != null) {
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

                            Toast.makeText(WishListActivity.this, "Somethig went wrong", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        }
        if(FirebaseDatabase.getInstance().getReference("user_data/" + childKey+"/wish_list")!=null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey + "/wish_list");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        idList.add(noteDataSnapshot.getKey());
                        Log.e("Wish Trip", String.valueOf(noteDataSnapshot.getValue()));
                        Result result = noteDataSnapshot.getValue(Result.class);
                        wishList.add(result);

                    }
                    if(wishList.size()>0) {
                        mkLoader.setVisibility(View.GONE);

                        if(isGone)
                        wishListAdapter = new WishListAdapter(WishListActivity.this, wishList,WishListActivity.this,isGone);
                        else
                            wishListAdapter = new WishListAdapter(WishListActivity.this, wishList,WishListActivity.this,isGone);
                        searchResultLV.setItemAnimator(new DefaultItemAnimator());
                        searchResultLV.addItemDecoration(new DividerItemDecoration(WishListActivity.this, DividerItemDecoration.VERTICAL));
                        searchResultLV.setAdapter(wishListAdapter);
                        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperWish(0, ItemTouchHelper.LEFT, WishListActivity.this);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(searchResultLV);
                    }
                    else {
                        mkLoader.setVisibility(View.GONE);
                        Toast.makeText(WishListActivity.this, "No wish list yet", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mkLoader.setVisibility(View.GONE);
                    Toast.makeText(WishListActivity.this,"Somethig went wrong",Toast.LENGTH_SHORT).show();

                }
            });

        }


    }

    @Override
    public void goToClickListener(Result result) {
        Intent intent = new Intent(WishListActivity.this, MapsActivity.class);
        intent.putExtra("POST_LATITUDE", result.getGeometry().getLocation().getLat().toString());
        intent.putExtra("POST_LONGITUDE", result.getGeometry().getLocation().getLng().toString());
        startActivity(intent);
    }

    @Override
    public void addToList(Result result) {

    }

    @Override
    public void addTodestination(Result result,int i) {
        if(destinalionList.size()>0) {
            for (int j = 0; j < destinalionList.size(); j++) {
                Result result1 = destinalionList.get(j);
                if (result1.getName().equals(result.getName())) {
                    Toast.makeText(WishListActivity.this, "Location already added", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        destinalionList.add(result);
        countShow++;
        count++;
        databaseReference = FirebaseDatabase.getInstance().getReference("user_data/" + childKey+"/planned_trip/"+Title.replace(".", "_")+startDate.replace("/", "_")+endDate.replace("/", "_")+"/list");
        databaseReference.child(String.valueOf(count)).setValue(result);
        Toast.makeText(WishListActivity.this, "Add to destination successfully", Toast.LENGTH_SHORT).show();
        wishListAdapter.removeItem(i);
        databaseReference.child(idList.get(i).toString()).removeValue();

    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof WishListAdapter.MyClass) {
            // get the removed item name to display it in snack bar


            // backup of removed item for undo purpose
            final Result deletedItem = (Result) wishList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            wishListAdapter.removeItem(viewHolder.getAdapterPosition());
            databaseReference.child(idList.get(deletedIndex).toString()).removeValue();





        }
    }
}
