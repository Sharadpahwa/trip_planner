package mobile.android.trip.planner.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlacesClickListener;
import mobile.android.trip.planner.app.listner.OnPlannedtripClickListener;
import mobile.android.trip.planner.app.model.TripBean;


public class PlannedTripAdapter extends RecyclerView.Adapter<PlannedTripAdapter.MyClass> {
    Context context;
    List<TripBean> placesList;
    OnPlannedtripClickListener onPlannedtripClickListener;



    public PlannedTripAdapter(Context context, ArrayList<TripBean> placesList,OnPlannedtripClickListener onPlannedtripClickListener) {
        this.context = context;
        this.placesList = placesList;
        this.onPlannedtripClickListener=onPlannedtripClickListener;

    }

    @NonNull
    @Override
    public MyClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.planned_trip_item, viewGroup, false);

        return new MyClass(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyClass myClass, int i) {

        final TripBean result = placesList.get(i);
        if (result != null) {

            if (result.getTripTitle() != null)

                myClass.name.setText(result.getTripTitle());

            if (result.getTripDestination() != null)
                myClass.location.setText(result.getTripDestination().toString());
            if (result.getStartDate() != null) {

                    myClass.startDate.setText("Start date: "+result.getStartDate());
                    myClass.startDate.setTextColor(context.getResources().getColor(R.color.quantum_googgreen));
            }
            if (result.getEndDate() != null) {
                myClass.endDate.setText("End date: "+result.getEndDate());
                myClass.endDate.setTextColor(context.getResources().getColor(R.color.reply_red_200));
            }
            myClass.clickLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlannedtripClickListener.goToClickListener(result);
                }
            });
            myClass.materialCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlannedtripClickListener.goToClickListener(result);
                }
            });

        }


    }


    @Override
    public int getItemCount() {
        Log.e("places_list_size", placesList.size() + "");
        return placesList.size();
    }

    public class MyClass extends RecyclerView.ViewHolder {

        TextView name, location, endDate,startDate;
        ImageView profile_image_view,photos;
        MaterialCardView materialCardView;
        RelativeLayout view_background;
RelativeLayout clickLay;

        public MyClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subject_text_view);
            location = itemView.findViewById(R.id.body_preview_text_view);
            endDate = itemView.findViewById(R.id.endDate);
            startDate = itemView.findViewById(R.id.startDate);
            materialCardView = itemView.findViewById(R.id.card_view);
            view_background = itemView.findViewById(R.id.view_background);
            clickLay = itemView.findViewById(R.id.email_frame);



        }
    }
    public void removeItem(int position) {
        placesList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }
}
