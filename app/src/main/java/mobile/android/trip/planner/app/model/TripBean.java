package mobile.android.trip.planner.app.model;

import java.util.ArrayList;

import mobile.android.trip.planner.app.beans.placesbeans.Result;

public class TripBean {
    String tripTitle;
    String tripDestination;
    String startDate;
    String endDate;
    ArrayList<Result> list;

    public TripBean() {

    }

    public TripBean(String tripTitle, String tripDestination, String startDate, String endDate, ArrayList<Result> list) {
        this.tripTitle = tripTitle;
        this.tripDestination = tripDestination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.list = list;
    }

    public TripBean(String tripTitle, String tripDestination, String startDate, String endDate) {
        this.tripTitle = tripTitle;
        this.tripDestination = tripDestination;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ArrayList<Result> getList() {
        return list;
    }

    public void setList(ArrayList<Result> list) {
        this.list = list;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
