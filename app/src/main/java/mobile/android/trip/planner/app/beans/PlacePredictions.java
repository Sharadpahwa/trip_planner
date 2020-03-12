package mobile.android.trip.planner.app.beans;

import java.util.ArrayList;

public class PlacePredictions {

    public ArrayList<PlaceAutoComplete> getPlaces() {
        return predictions;
    }

    public void setPlaces(ArrayList<PlaceAutoComplete> places) {
        this.predictions = places;
    }

    private ArrayList<PlaceAutoComplete> predictions;
}
