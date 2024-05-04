package com.rowbotix.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rowbotix.R;

import java.util.Random;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mWheel;
    private final MutableLiveData<String> mFertiliser;
    private final MutableLiveData<String> mDistance;
    private final MutableLiveData<Integer> mDeviceImageResource;

    public DashboardViewModel() {
        mWheel = new MutableLiveData<>();
        mFertiliser = new MutableLiveData<>();
        mDistance = new MutableLiveData<>();
        mDeviceImageResource = new MutableLiveData<>();
        generateRandomData();
    }

    public LiveData<String> getWheel() {
        return mWheel;
    }

    public LiveData<String> getFertiliser() {
        return mFertiliser;
    }

    public LiveData<String> getDistance() {
        return mDistance;
    }

    public LiveData<Integer> getDeviceImageResource() {
        return mDeviceImageResource;
    }

    private void generateRandomData() {
        Random random = new Random();
        mWheel.setValue(String.valueOf(random.nextInt(100)));
        mFertiliser.setValue(String.valueOf(random.nextInt(100)));
        mDistance.setValue(String.valueOf(random.nextInt(100)));
        mDeviceImageResource.setValue(getRandomDeviceImage());
    }

    private int getRandomDeviceImage() {
        // Replace with your logic to get a random device image resource
        return R.drawable.ic_dashboard_black_24dp;
    }
}
