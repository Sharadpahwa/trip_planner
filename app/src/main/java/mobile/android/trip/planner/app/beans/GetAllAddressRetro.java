package mobile.android.trip.planner.app.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAllAddressRetro {
    @SerializedName("history")
    @Expose
    private ArrayList<AddressData> history;
    @SerializedName("data")
    @Expose
    private ArrayList<AddressData> data;
    private int status;
    private String message;


    public ArrayList<AddressData> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<AddressData> history) {
        this.history = history;
    }

    public ArrayList<AddressData> getData() {
        return data;
    }

    public void setData(ArrayList<AddressData> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
