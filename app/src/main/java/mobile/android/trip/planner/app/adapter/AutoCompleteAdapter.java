package mobile.android.trip.planner.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.beans.PlaceAutoComplete;
import mobile.android.trip.planner.app.beans.placesbeans.Result;


public class AutoCompleteAdapter extends ArrayAdapter<Result> {

    Context context;
    ArrayList<Result> Places;
    private Activity mActivity;

    public AutoCompleteAdapter(Context context, ArrayList<Result> modelsArrayList, Activity activity) {
        super(context, R.layout.autocomplete_row, modelsArrayList);
        this.context = context;
        this.Places = modelsArrayList;
        this.mActivity = activity;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("places_list_size",Places.size()+"");
        View rowView = convertView;
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.autocomplete_row, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) rowView.findViewById(R.id.place_name);
            holder.location = (TextView) rowView.findViewById(R.id.place_detail);
            rowView.setTag(holder);

        } else
            holder = (ViewHolder) rowView.getTag();
        /***** Get each Model object from ArrayList ********/
        holder.Place = Places.get(position);
        StringTokenizer st=new StringTokenizer(holder.Place.getName(), ",");
        /************  Set Model values in Holder elements ***********/

        if(holder.Place.getName()!=null) {
            if(holder.Place.getName()!=null)
            {
                holder.name.setText(holder.Place.getName());
            }
            if(holder.Place.getFormattedAddress()!=null)
            holder.location.setText(holder.Place.getFormattedAddress().toString());
        }

        return rowView;
    }

    class ViewHolder {
        Result Place;
        TextView name, location;
    }

    @Override
    public int getCount(){
        return Places.size();
    }
}