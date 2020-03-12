package mobile.android.trip.planner.app.listner;

import mobile.android.trip.planner.app.beans.placesbeans.Result;

public interface OnPlacesClickListener {
    void goToClickListener(Result result);
    void  addToList(Result result);
    void  addTodestination(Result result,int pos);
}
