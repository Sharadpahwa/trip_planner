package mobile.android.trip.planner.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.activities.MapsActivity;
import mobile.android.trip.planner.app.beans.placesbeans.Result;
import mobile.android.trip.planner.app.listner.OnPlacesClickListener;


public class NearByRestaurantsAdapter extends RecyclerView.Adapter<NearByRestaurantsAdapter.MyClass> {
    Context context;
    List<Result> placesList;

    OnPlacesClickListener onPlacesClickListener;


    public NearByRestaurantsAdapter(Context context, ArrayList<Result> placesList) {
        this.context = context;
        this.placesList = placesList;
        this.onPlacesClickListener = onPlacesClickListener;
    }

    @NonNull
    @Override
    public MyClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.restaurant_item, viewGroup, false);

        return new MyClass(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyClass myClass, int i) {

        final Result result = placesList.get(i);
        if (result != null) {

            if (result.getName() != null)

                myClass.name.setText(result.getName());

            if (result.getFormattedAddress() != null)
                myClass.location.setText(result.getFormattedAddress().toString());
            if (result.getOpeningHours() != null) {
                if(result.getOpeningHours().getOpenNow()!=null) {
                    if (result.getOpeningHours().getOpenNow()) {
                        myClass.open_text_view.setText("Open");
                        myClass.open_text_view.setTextColor(context.getResources().getColor(R.color.quantum_googgreen));
                    } else {
                        myClass.open_text_view.setText("Closed");
                        myClass.open_text_view.setTextColor(context.getResources().getColor(R.color.reply_red_200));
                    }
                }
                else
                {
                    myClass.open_text_view.setText("Always Open");
                    myClass.open_text_view.setTextColor(context.getResources().getColor(R.color.quantum_googgreen));
                }
            }
            if (result.getOpeningHours() == null) {
                myClass.open_text_view.setText("Always Open");
                myClass.open_text_view.setTextColor(context.getResources().getColor(R.color.quantum_googgreen));
            }

            if (result.getIcon() != null) {
                Glide.with(context).load(result.getIcon())
                        .thumbnail(0.5f)
                        .error(R.drawable.user)
                        .placeholder(R.drawable.user)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(myClass.profile_image_view);

            }
            if (result.getRating() != null) {
                myClass.rating.setText("Rating- " + result.getRating());
            } else
                myClass.rating.setText("No Rating");
            if (result.getPhotos() != null) {
                if (result.getPhotos().get(0).getPhotoReference() != null) {
                    myClass.photos.setVisibility(View.VISIBLE);
                    Glide.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + context.getResources().getString(R.string.key_google_apis_maps))
                            .thumbnail(0.5f)
                            .error(R.drawable.fourth)
                            .placeholder(R.drawable.fourth)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(myClass.photos);

                } else
                    myClass.photos.setVisibility(View.GONE);
            }
            myClass.finfPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("POST_LATITUDE", result.getGeometry().getLocation().getLat().toString());
                    intent.putExtra("POST_LONGITUDE", result.getGeometry().getLocation().getLng().toString());
                    context.startActivity(intent);
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

        TextView name, location, open_text_view, rating;
        ImageView profile_image_view, photos;
        Button finfPath;

        public MyClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subject_text_view);
            location = itemView.findViewById(R.id.body_preview_text_view);
            open_text_view = itemView.findViewById(R.id.open_text_view);
            rating = itemView.findViewById(R.id.rating);
            profile_image_view = itemView.findViewById(R.id.profile_image_view);
            photos = itemView.findViewById(R.id.photos);
            finfPath = itemView.findViewById(R.id.btnAddToDes);


        }
    }
}
