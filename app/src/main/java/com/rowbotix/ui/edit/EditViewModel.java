package com.rowbotix.ui.edit;

import androidx.lifecycle.ViewModel;

public class EditViewModel extends ViewModel {

    private String wheelNo = "";
    private String distance = "";
    private String fertilizer = "";
    private String rows = "";
    private String rowDistance = "";

    public void saveData(String wheelNo, String distance, String fertilizer, String rows, String rowDistance) {
        // Save data to ViewModel
        this.wheelNo = wheelNo;
        this.distance = distance;
        this.fertilizer = fertilizer;
        this.rows = rows;
        this.rowDistance = rowDistance;
    }

    public String getFormattedData() {
        // Format data as needed
        return "{\"SETUP\": { \"Whl\": " + wheelNo +
                ", \"Dist\": " + distance +
                ", \"Fert\": " + fertilizer +
                ", \"Rows\": " + rows +
                ", \"RSpc\": " + rowDistance + "}}";
    }
}
